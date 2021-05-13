package edu.cooper.ece465.FFT;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.lang.Math;

import edu.cooper.ece465.threading.CompressThread;
import edu.cooper.ece465.threading.DistributionInputThread;
import edu.cooper.ece465.threading.DistributionOutputThread;

public class Image {
	
	public BufferedImage img;
	public int height;
	public int width;
	public int paddedWidth;
	public int paddedHeight;
	public Complex[][] red;
	public Complex[][] green;
	public Complex[][] blue;
	public IterativeFFT iterativeFFT;
	
	public Image() {
		this.iterativeFFT = new IterativeFFT();
	}

	public int log2(int x ) {
		return (int)(Math.log(x) / Math.log(2));
	}
	
	public void readImage(String path) throws Exception {
		this.img = ImageIO.read(new File(path));
		
		this.width = this.img.getWidth();
		this.height = this.img.getHeight();

		this.paddedWidth = this.width;
		this.paddedHeight = this.height;

		if (Integer.highestOneBit(this.width) != this.width) {
			this.paddedWidth = (int) Math.pow(2, (log2(this.width) + 1));
		}

		if (Integer.highestOneBit(this.height) != this.height) {
			this.paddedHeight = (int) Math.pow(2, (log2(this.height) + 1));
		}
		
		this.red = new Complex[this.paddedHeight][this.paddedWidth];
		this.green = new Complex[this.paddedHeight][this.paddedWidth];
		this.blue = new Complex[this.paddedHeight][this.paddedWidth];
		
		// Load pixel data into 2D arrays, one for each color
		for (int h = 0; h < this.paddedHeight; h++) {
			for (int w = 0; w < this.paddedWidth; w++) {
				if (h >= this.height || w >= this.width) {
					this.red[h][w] = new Complex(0, 0);
					this.green[h][w] = new Complex(0, 0);
					this.blue[h][w] = new Complex(0, 0);
				}
				else {
					Color color = new Color(img.getRGB(w, h), true);
					this.red[h][w] = new Complex(color.getRed(), 0);
					this.green[h][w] = new Complex(color.getGreen(), 0);
					this.blue[h][w] = new Complex(color.getBlue(), 0);
				}
			}
		}

	}
	
	
	public void writeImage(String path) {
		BufferedImage imgOut = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		double[][] r = this.complexToDouble(this.red);
		double[][] g = this.complexToDouble(this.green);
		double[][] b = this.complexToDouble(this.blue);
		
		for (int i = 0; i < this.height; i++) {
	        for (int j = 0; j < this.width; j++) {
	        	Color color = new Color((int) r[i][j], (int) g[i][j], (int) b[i][j]);
	            imgOut.setRGB(j, i, color.getRGB());
	        }
	    }
	    File ImageFile = new File(path);
	    try {
	        ImageIO.write(imgOut, "jpg", ImageFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	/*
	 * Converts the Complex 2D array to a double 2D array by extracting the real value from each entry. If the value is out
	 * of range it is truncated to either 0 or 255.
	 */
	public double[][] complexToDouble(Complex[][] x) {
		double[][] y = new double[this.height][this.width];

		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				y[i][j] = x[i][j].re();
				
				// Truncate values greater than 255 and less than 0.
				if (y[i][j] > 255) {
					y[i][j] = 255;
				}
				else if (y[i][j] < 0) {
					y[i][j] = 0;
				}
			}
		}
		return y;
	}
	
	public void rgbCompress(float threshold) {
		this.red = this.compress(this.iterativeFFT.fft2(this.red), threshold);
		this.red = this.iterativeFFT.ifft2(this.red);
		this.green = this.compress(this.iterativeFFT.fft2(this.green), threshold);
		this.green = this.iterativeFFT.ifft2(this.green);
		this.blue = this.compress(this.iterativeFFT.fft2(this.blue), threshold);
		this.blue = this.iterativeFFT.ifft2(this.blue);
	}

	public Complex[][] compress(Complex[][] x, float threshold) {
		
		// Flatten the 2d array, taking the absolute value of each complex value before placing
		// it into the flattened array
		Double[] flat = new Double[x.length*x[0].length];
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				flat[i * x[0].length + j] = x[i][j].abs();
			}
		}
		
		// Get the unique values in the array. We will threshold by only taking values greater than 
		// or equal to the unique value located at threshold times the length of the sorted keyset.
		// Array is sorted from smallest to largest!
		Set<Double> uniques = new HashSet<>(Arrays.asList(flat));
		double[] sortedVals = uniques.stream().mapToDouble(Double::doubleValue).toArray();
		Arrays.sort(sortedVals);

		// Find the index of the threshold value. Use that index to find the threshold value
		int idx = (int) Math.floor((1-threshold)*sortedVals.length);
		double thresholdValue = sortedVals[idx];
		
		// Apply the threshold to the FFT'd 2D array
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				if (x[i][j].abs() < thresholdValue) {
					x[i][j] = new Complex(0, 0);
				}
			}
		}
		
		return x;
	}

	public void distRGBCompress(ArrayList<ObjectInputStream> ois, ArrayList<ObjectOutputStream> oos, float threshold, int numWorkers) {
		for (int i = 0; i < numWorkers; i++) {
			try {
				oos.get(i).writeObject("JOB");
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		// Distribution of Rows to do FFT
		createPoolAndOutput(this.red, oos, numWorkers, 0);
		createPoolAndOutput(this.green, oos, numWorkers, 0);
		createPoolAndOutput(this.blue, oos, numWorkers, 0);
		// Receive Calculated Rows
		createPoolAndInput(this.red, ois, numWorkers, 0);
		createPoolAndInput(this.green, ois, numWorkers, 0);
		createPoolAndInput(this.blue, ois, numWorkers, 0);

		// Distribution of Columns to do FFT
		createPoolAndOutput(this.red, oos, numWorkers, 1);
		createPoolAndOutput(this.green, oos, numWorkers, 1);
		createPoolAndOutput(this.blue, oos, numWorkers, 1);
		// Receive Calculated Columns
		createPoolAndInput(this.red, ois, numWorkers, 1);
		createPoolAndInput(this.green, ois, numWorkers, 1);
		createPoolAndInput(this.blue, ois, numWorkers, 1);

		// do thresholding here
		ExecutorService pool = Executors.newFixedThreadPool(3);
		pool.execute(new CompressThread(this.red, threshold));
		pool.execute(new CompressThread(this.green, threshold));
		pool.execute(new CompressThread(this.blue, threshold));
		pool.shutdown();
		awaitTerminationAfterShutdown(pool);

		// Distribution of Rows to do IFFT
		createPoolAndOutput(this.red, oos, numWorkers, 0);
		createPoolAndOutput(this.green, oos, numWorkers, 0);
		createPoolAndOutput(this.blue, oos, numWorkers, 0);
		// Receive Calculated Rows
		createPoolAndInput(this.red, ois, numWorkers, 0);
		createPoolAndInput(this.green, ois, numWorkers, 0);
		createPoolAndInput(this.blue, ois, numWorkers, 0);

		// Distribution of Columns to do IFFT
		createPoolAndOutput(this.red, oos, numWorkers, 1);
		createPoolAndOutput(this.green, oos, numWorkers, 1);
		createPoolAndOutput(this.blue, oos, numWorkers, 1);
		// Receive Calculated Columns
		createPoolAndInput(this.red, ois, numWorkers, 1);
		createPoolAndInput(this.green, ois, numWorkers, 1);
		createPoolAndInput(this.blue, ois, numWorkers, 1);
	}

	private static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
		try {
			if (!threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
				threadPool.shutdownNow();
			}
		} catch (InterruptedException ex) {
			threadPool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	private void createPoolAndOutput(Complex[][] image, ArrayList<ObjectOutputStream> oos, int numWorkers, int axis) {
		// Cant have these all in the same loop because Red must be sent before Green which must be sent before Blue
		ExecutorService pool = Executors.newFixedThreadPool(8);
		for (int i = 0; i < numWorkers; i++) {
			pool.execute(new DistributionOutputThread(image, oos.get(i), i, axis, numWorkers));
		}
		pool.shutdown();
		awaitTerminationAfterShutdown(pool);
	}

	private void createPoolAndInput(Complex[][] image, ArrayList<ObjectInputStream> ois, int numWorkers, int axis) {
		// Cant have all images in the same loop because Red must be received before Green which must be received before Blue
		ExecutorService pool = Executors.newFixedThreadPool(8);
		for (int i = 0; i < numWorkers; i++) {
			pool.execute(new DistributionInputThread(image, ois.get(i), i, axis, numWorkers));
		}
		pool.shutdown();
		awaitTerminationAfterShutdown(pool);
	}
}