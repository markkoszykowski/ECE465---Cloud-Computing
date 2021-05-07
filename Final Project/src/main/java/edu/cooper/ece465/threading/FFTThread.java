package edu.cooper.ece465.threading;

import edu.cooper.ece465.FFT.Complex;

public class FFTThread implements Runnable {
    private final Complex[][] pixels;
    private final int axis;
    private final int x;
    private final int y;

    public FFTThread(Complex[][] p, int a, int x, int y) {
        this.pixels = p;
        this.axis = a;
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {
        if (this.axis == 0) {
            for (int i = 0; i < this.x; i++) {
                // bit reversal permutation
                int shift = 1 + Integer.numberOfLeadingZeros(this.y);
                for (int k = 0; k < this.y; k++) {
                    int j = Integer.reverse(k) >>> shift;
                    if (j > k) {
                        Complex temp = this.pixels[i][j];
                        this.pixels[i][j].set(this.pixels[i][k]);
                        this.pixels[i][k].set(temp);
                    }
                }

                // butterfly updates
                for (int L = 2; L <= this.y; L = L+L) {
                    for (int k = 0; k < L/2; k++) {
                        double kth = -2 * k * Math.PI / L;
                        Complex w = new Complex(Math.cos(kth), Math.sin(kth));
                        for (int j = 0; j < this.y/L; j++) {
                            Complex tao = w.times(this.pixels[i][j * L + k + L/2]);
                            this.pixels[i][j * L + k + L/2].set(this.pixels[i][j * L + k].minus(tao));
                            this.pixels[i][j * L + k].set(this.pixels[i][j * L + k].plus(tao));
                        }
                    }
                }
            }
        }
        else {
            for (int i = 0; i < this.y; i++) {
                // bit reversal permutation
                int shift = 1 + Integer.numberOfLeadingZeros(this.x);
                for (int k = 0; k < this.x; k++) {
                    int j = Integer.reverse(k) >>> shift;
                    if (j > k) {
                        Complex temp = this.pixels[j][i];
                        this.pixels[j][i].set(this.pixels[k][i]);
                        this.pixels[k][i].set(temp);
                    }
                }

                // butterfly updates
                for (int L = 2; L <= this.x; L = L+L) {
                    for (int k = 0; k < L/2; k++) {
                        double kth = -2 * k * Math.PI / L;
                        Complex w = new Complex(Math.cos(kth), Math.sin(kth));
                        for (int j = 0; j < this.y/L; j++) {
                            Complex tao = w.times(this.pixels[j * L + k + L/2][i]);
                            this.pixels[j * L + k + L/2][i].set(this.pixels[j * L + k][i].minus(tao));
                            this.pixels[j * L + k][i].set(this.pixels[j * L + k][i].plus(tao));
                        }
                    }
                }
            }
        }
    }
}
