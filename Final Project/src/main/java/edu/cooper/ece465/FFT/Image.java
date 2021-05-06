package edu.cooper.ece465.FFT;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import javax.imageio.ImageIO;

import java.awt.Color;

public class Image {
	
	public BufferedImage img;
	public int height;
	public int width;
	public Complex[][] red;
	public Complex[][] blue;
	public Complex[][] green;
	public IterativeFFT iterativeFFT;
	
	public Image() {
		this.iterativeFFT = new IterativeFFT();
	}
	
	public void readImage(String path) throws IOException {
		this.img = ImageIO.read(new File(path));
		
		this.width = this.img.getWidth();
		this.height = this.img.getHeight();
		
		this.red = new Complex[height][width];
		this.green = new Complex[height][width];
		this.blue = new Complex[height][width];
		
		// Load pixel data into 2D arrays, one for each color
		for(int h=0; h<this.height; h++) {
			for(int w=0; w<this.width; w++) {
				Color color = new Color(img.getRGB(w, h), true);
//				this.red[h][w] = new Complex((color.getRed() + color.getGreen() + color.getBlue())/3, 0);
				this.red[h][w] = new Complex(color.getRed(), 0);
				this.green[h][w] = new Complex(color.getGreen(), 0);
				this.blue[h][w] = new Complex(color.getBlue(), 0);
			}
		}

	}
	
	
	public void writeImage(String path) throws IOException {
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
	    	
//	    	JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
//	    	jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//	    	jpegParams.setCompressionQuality(0.7f);
//	    	ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
//	    	// specifies where the jpg image has to be written
//	    	writer.setOutput(new FileImageOutputStream(
//	    	  new File(path)));
//
//	    	// writes the file with given compression level 
//	    	// from your JPEGImageWriteParam instance
//	    	writer.write(null, new IIOImage(imgOut, null, null), jpegParams);
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
		double max = 0;
		for (int i=0; i<this.height; i++) {
			for(int j=0; j<this.width; j++) {
				y[i][j] = x[i][j].re();
				if(y[i][j] > max) {
					max = y[i][j];
				}
				
				// Truncate values greater than 255 and less than 0.
				if(y[i][j] > 255) {
					y[i][j] = 255;
				}
				else if(y[i][j] < 0) {
					y[i][j] = 0;
				}
			}
		}
		System.out.println(max);
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
		double[] flat = new double[x.length*x[0].length];
		for(int i=0; i<x.length; i++) {
			for(int j=0; j<x[0].length; j++) {
				flat[i*j+j] = x[i][j].abs();
			}
		}
		
		Arrays.sort(flat); // Sort the flat array
		
		// Get the unique values in the array. We will threshold by only taking values greater than 
		// or equal to the unique value located at threshold times the length of the sorted keyset
		HashMap<Double, Integer> uniqKeys = new HashMap<Double, Integer>();
		for (int i = 0; i < flat.length; i++) {
            uniqKeys.put(flat[i], i);
        }
		Set<Double> keys = uniqKeys.keySet();
		double[] sortedKeys = new double[keys.size()];
		
		// Take the unique keys and put them into array, then sort. Array is sorted from smallest to largest!
		int idx = 0;
		for (double key : keys) {
			sortedKeys[idx++] = key;
		}
		Arrays.sort(sortedKeys);
		
		// Find the index of the threshold value. Use that index to find the threshold value
		idx = (int) Math.floor((1-threshold)*sortedKeys.length);		
		double thresholdValue = sortedKeys[idx];
		
		// Apply the threshold to the FFT'd 2D array
		for(int i=0; i<x.length; i++) {
			for(int j=0; j<x[0].length; j++) {
				if(x[i][j].abs() < thresholdValue) {
					x[i][j] = new Complex(0, 0);
				}
			}
		}
		
		return x;
	}
	
}
