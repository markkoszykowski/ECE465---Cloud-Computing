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
        System.out.println(this.image[0][0]);
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

            System.out.println("Output Node(" + this.numWorkers + ": " + this.node);
            System.out.println("Dimensions: " + size + "x" + this.image[0].length);
            System.out.println("Axis: " + this.axis);



            for (int i = 0; i < size; i++) {
                for (int j = 0; j < this.image[0].length; j++) {
                    sending[i][j] = this.image[(this.node * factor + offset) + i][j];
                }
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

            System.out.println("Output Node(" + this.numWorkers + ": " + this.node);
            System.out.println("Dimensions: " + this.image.length + "x" + size);
            System.out.println("Axis: " + this.axis);

            for (int i = 0; i < this.image.length; i++) {
                for (int j = 0; j < size; j++) {
                    sending[i][j] = this.image[i][(this.node * factor + offset) + j];
                }
            }
            try {
                this.oos.writeObject(sending);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
