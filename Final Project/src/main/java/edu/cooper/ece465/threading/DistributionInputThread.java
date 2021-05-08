package edu.cooper.ece465.threading;

import java.io.IOException;
import java.io.ObjectInputStream;

import edu.cooper.ece465.FFT.Complex;

public class DistributionInputThread implements Runnable {
    private final Complex[][] image;
    private final ObjectInputStream ois;
    private final int node;
    private final int axis;
    private final int numWorkers;

    public DistributionInputThread(Complex[][] pixels, ObjectInputStream ois, int n, int a, int num) {
        this.image = pixels;
        this.ois = ois;
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
            Complex[][] receiving;
            try {
                receiving = (Complex[][]) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

            if (receiving.length != size || receiving[0].length != this.image[0].length) {
                System.out.println("Houston we have a problem");
                System.out.println("Expected length: " + size);
                System.out.println("Received length: " + receiving.length);
                System.out.println("Expected width: " + this.image[0].length);
                System.out.println("Received width: " + receiving[0].length);
            }

            for (int i = 0; i < size; i++) {
                System.arraycopy(receiving[i], 0, this.image[(this.node * factor + offset) + i], 0, this.image[0].length);
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
            Complex[][] receiving;
            try {
                receiving = (Complex[][]) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

            if (receiving.length != this.image.length || receiving[0].length != size) {
                System.out.println("Houston we have a problem");
                System.out.println("Expected length: " + this.image.length);
                System.out.println("Received length: " + receiving.length);
                System.out.println("Expected width: " + size);
                System.out.println("Received width: " + receiving[0].length);
            }

            for (int i = 0; i < this.image.length; i++) {
                System.arraycopy(receiving[i], 0, this.image[i], (this.node * factor + offset), size);
            }
        }
    }
}
