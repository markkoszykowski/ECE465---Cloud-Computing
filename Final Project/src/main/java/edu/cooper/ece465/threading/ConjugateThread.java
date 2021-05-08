package edu.cooper.ece465.threading;

import edu.cooper.ece465.FFT.Complex;

public class ConjugateThread implements Runnable {
    private final Complex[][] image;
    private final int x;
    private final int y;

    public ConjugateThread(Complex[][] pixels, int x, int y) {
        this.image = pixels;
        this.x = x;
        this.y = y;
    }


    @Override
    public void run() {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                this.image[i][j] = this.image[i][j].conjugate();
            }
        }
    }
}
