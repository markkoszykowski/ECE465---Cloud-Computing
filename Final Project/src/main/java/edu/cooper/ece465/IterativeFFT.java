package edu.cooper.ece465;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class IterativeFFT {

	/*Takes the 1D fft on a row of data
	 Tukey-Cooley radix-2 inplace algorithm */
	public Complex[] fft(Complex[] x) {
		int n = x.length;
        if (Integer.highestOneBit(n) != n) {
            throw new RuntimeException("n is not a power of 2");
        }

        // bit reversal permutation
        int shift = 1 + Integer.numberOfLeadingZeros(n);
        for (int k = 0; k < n; k++) {
            int j = Integer.reverse(k) >>> shift;
            if (j > k) {
                Complex temp = x[j];
                x[j] = x[k];
                x[k] = temp;
            }
        }

        // butterfly updates
        for (int L = 2; L <= n; L = L+L) {
            for (int k = 0; k < L/2; k++) {
                double kth = -2 * k * Math.PI / L;
                Complex w = new Complex(Math.cos(kth), Math.sin(kth));
                for (int j = 0; j < n/L; j++) {
                    Complex tao = w.times(x[j*L + k + L/2]);
                    x[j*L + k + L/2] = x[j*L + k].minus(tao); 
                    x[j*L + k]       = x[j*L + k].plus(tao); 
                }
            }
        }
		return x;
	}
	
	
	/*
	 *  Takes a 1D ifft on a row of data
	 */
	public Complex[] ifft(Complex[] x) {
		int n = x.length;
        Complex[] y = new Complex[n];

        // take conjugate
        for (int i = 0; i < n; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = this.fft(y);

        // take conjugate again
        for (int i = 0; i < n; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by n
        for (int i = 0; i < n; i++) {
            y[i] = y[i].scale(1.0 / n);
        }

        return y;

	}
	
	/*
	 * Takes 1D FFTs of the rows and then the columns, taking a 2D FFT
	 */
	public Complex[][] fft2(Complex[][] x) {
		int width = x[0].length;
		int height = x.length;
		
		// Take FFT row-wise first
		for(int i=0; i<height; i++) {
			x[i] = fft(x[i]);
		}
		
		// Use take column-wise FFT on the already completed row-wise FFT matrix
		Complex[] column = new Complex[height]; // Temp variable to store column data
		for(int j=0; j<width; j++) {
			for(int k=0; k<height; k++) {
				column[k] = x[k][j];
			}
			column = fft(column);
			for(int k=0; k<height; k++) {
				x[k][j] = column[k];
			}
			
		}
		return x;
	}
	
	
	/*
	 * Takes a 1D IFFT of the rows and then the columns, taking a 2D IFFT
	 */
	public Complex[][] ifft2(Complex[][] x) {
		int width = x[0].length;
		int height = x.length;
		
		// Take FFT row-wise first
		for(int i=0; i<height; i++) {
			x[i] = ifft(x[i]);
		}
		
		// Use take column-wise FFT on the already completed row-wise FFT matrix
		Complex[] column = new Complex[height]; // Temp variable to store column data
		for(int j=0; j<width; j++) {
			for(int k=0; k<height; k++) {
				column[k] = x[k][j];
			}
			column = ifft(column);
			for(int k=0; k<height; k++) {
				x[k][j] = column[k];
			}
			
		}
		return x;
	}
	
//	public Complex[][] compress(Complex[][] x, float threshold) {
//		
//		// Flatten the 2d array, taking the absolute value of each complex value before placing
//		// it into the flattened array
//		double[] flat = new double[x.length*x[0].length];
//		for(int i=0; i<x.length; i++) {
//			for(int j=0; j<x[0].length; j++) {
//				flat[i*j+j] = x[i][j].abs();
//			}
//		}
//		
//		Arrays.sort(flat); // Sort the flat array
//		
//		// Get the unique values in the array. We will threshold by only taking values greater than 
//		// or equal to the unique value located at threshold times the length of the sorted keyset
//		HashMap<Double, Integer> uniqKeys = new HashMap<Double, Integer>();
//		for (int i = 0; i < flat.length; i++) {
//            uniqKeys.put(flat[i], i);
//        }
//		Set<Double> keys = uniqKeys.keySet();
//		double[] sortedKeys = new double[keys.size()];
//		
//		// Take the unique keys and put them into array, then sort. Array is sorted from smallest to largest!
//		int idx = 0;
//		for (double key : keys) {
//			sortedKeys[idx++] = key;
//		}
//		Arrays.sort(sortedKeys);
//		
//		// Find the index of the threshold value. Use that index to find the threshold value
//		idx = (int) Math.floor((1-threshold)*sortedKeys.length);		
//		double thresholdValue = sortedKeys[idx];
//		
//		// Apply the threshold to the FFT'd 2D array
//		for(int i=0; i<x.length; i++) {
//			for(int j=0; j<x[0].length; j++) {
//				if(x[i][j].abs() < thresholdValue) {
//					x[i][j] = new Complex(0, 0);
//				}
//			}
//		}
//		
//		return x;
//	}
	
}
