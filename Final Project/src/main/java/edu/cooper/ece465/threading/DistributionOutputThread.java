package edu.cooper.ece465.threading;

import java.io.IOException;
import java.io.ObjectOutputStream;

import edu.cooper.ece465.FFT.Complex;

public class DistributionOutputThread implements Runnable {
    private final Complex[][] image;
    private final ObjectOutputStream oos;
    private final int node;
    private final int axis;
    private final int numWorkers;

    public DistributionOutputThread(Complex[][] pixels, ObjectOutputStream oos, int n, int a, int num) {
        this.image = pixels;
        this.oos = oos;
        this.node = n;
        this.axis = a;
        this.numWorkers = num;
    }

    @Override
    public void run() {
        if (this.axis == 0) {
            int size, offset;
            int factor = (int) Math.floor(this.image.length / this.numWorkers);
            if (this.node < this.image.length % this.numWorkers) {
                size = factor + 1;
                offset = this.node;
            }
            else {
                size = factor;
                offset = this.image.length % this.numWorkers;
            }
            Complex[][] sending = new Complex[size][this.image[0].length];

            for (int i = 0; i < size; i++) {
                System.arraycopy(this.image[(this.node * factor + offset) + i], 0, sending[i], 0, this.image[0].length);
            }
            try {
                this.oos.writeObject(sending);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            int size, offset;
            int factor = (int) Math.floor(this.image[0].length / this.numWorkers);
            if (this.node < this.image[0].length % this.numWorkers) {
                size = factor + 1;
                offset = this.node;
            }
            else {
                size = factor;
                offset = this.image[0].length % this.numWorkers;
            }
            Complex[][] sending = new Complex[this.image.length][size];

            for (int i = 0; i < this.image.length; i++) {
                System.arraycopy(this.image[i], (this.node * factor + offset), sending[i], 0, size);
            }
            try {
                this.oos.writeObject(sending);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
