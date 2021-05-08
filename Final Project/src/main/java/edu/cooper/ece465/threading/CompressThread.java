package edu.cooper.ece465.threading;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.cooper.ece465.FFT.Complex;

public class CompressThread implements Runnable {
    private final Complex[][] image;
    private final float threshold;

    public CompressThread(Complex[][] pixels, float t) {
        this.image = pixels;
        this.threshold = t;
    }

    @Override
    public void run() {
        Double[] flat = new Double[this.image.length * this.image[0].length];
        for (int i = 0; i < this.image.length; i++) {
            for (int j = 0; j < this.image[0].length; j++) {
                flat[i * this.image[0].length + j] = this.image[i][j].abs();
            }
        }
        Set<Double> uniques = new HashSet<>(Arrays.asList(flat));
        double[] sortedVals = uniques.stream().mapToDouble(Double::doubleValue).toArray();
        Arrays.sort(sortedVals);

        int ind = (int) Math.floor((1 - this.threshold) * sortedVals.length);
        double thresholdValue = sortedVals[ind];

        for (int i = 0; i < this.image.length; i++) {
            for (int j = 0; j < this.image[0].length; j++) {
                if (this.image[i][j].abs() < thresholdValue) {
                    this.image[i][j] = new Complex(0, 0);
                }
            }
        }
    }
}
