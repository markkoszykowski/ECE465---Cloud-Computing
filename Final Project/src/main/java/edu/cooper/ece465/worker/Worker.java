package edu.cooper.ece465.worker;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.cooper.ece465.threading.ConjugateThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.cooper.ece465.FFT.Complex;
import edu.cooper.ece465.threading.FFTThread;

public class Worker {
    private static final Logger LOG = LogManager.getLogger(Worker.class);

    public static void main(String[] args) {
        try {
            LOG.debug("Worker on Private IP: " + InetAddress.getLocalHost().getHostAddress() + "started.");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        String coordinatorIp;
        BufferedReader reader;
        try {
            // coordinators IP will be the first IP in the file
            reader = new BufferedReader(new FileReader("./ips.txt"));
            coordinatorIp = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        LOG.debug("Looking for Coordinator at IP: " + coordinatorIp);
        System.out.println("Looking for Coordinator at IP: " + coordinatorIp);

        ObjectInputStream ois;
        ObjectOutputStream oos;

        try (Socket socket = new Socket(coordinatorIp, 6969)) {
            //socket.setSoTimeout(120*1000);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            while (true) {
                String status = (String) ois.readObject();
                switch (status) {
                    case "TERMINATE":
                        LOG.debug("TERMINATING node.");
                        return;
                    case "HANDSHAKE":
                        oos.writeObject("HANDSHAKE RECEIVED");
                        break;
                    case "JOB":
                        LOG.debug("Beginning JOB.");

                        doFFT(ois, oos, 0);

                        doFFT(ois, oos, 1);

                        // Backend will do compression here (throwing away small coefficients)

                        doIFFT(ois, oos, 0);

                        doIFFT(ois, oos, 1);
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        try {
            if (!threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void doFFT(ObjectInputStream ois, ObjectOutputStream oos, int axis) throws IOException, ClassNotFoundException {
        LOG.debug("Performing FFT along axis = " + axis);
        // Parallelized so node doesnt need to wait for all channels to start doing work
        ExecutorService pool = Executors.newFixedThreadPool(3);
        Complex[][] red = (Complex[][]) ois.readObject();
        int x = red.length;
        int y = red[0].length;
        pool.execute(new FFTThread(red, axis, x, y));
        Complex[][] green = (Complex[][]) ois.readObject();
        pool.execute(new FFTThread(green,axis, x, y));
        Complex[][] blue = (Complex[][]) ois.readObject();
        pool.execute(new FFTThread(blue, axis, x, y));
        pool.shutdown();
        awaitTerminationAfterShutdown(pool);

        LOG.debug("Sending FFT along axis = " + axis + " back to backend");
        oos.writeObject(red);
        oos.writeObject(green);
        oos.writeObject(blue);
    }

    public static void doIFFT(ObjectInputStream ois, ObjectOutputStream oos, int axis) throws IOException, ClassNotFoundException {
        LOG.debug("Performing IFFT along axis = " + axis);
        // Parallelized so node doesnt need to wait for all channels to start doing work
        ExecutorService pool1 = Executors.newFixedThreadPool(3);
        Complex[][] red = (Complex[][]) ois.readObject();
        int x = red.length;
        int y = red[0].length;
        pool1.execute(new ConjugateThread(red, x, y));
        Complex[][] green = (Complex[][]) ois.readObject();
        pool1.execute(new ConjugateThread(green, x, y));
        Complex[][] blue = (Complex[][]) ois.readObject();
        pool1.execute(new ConjugateThread(blue, x, y));
        pool1.shutdown();
        awaitTerminationAfterShutdown(pool1);

        LOG.debug("Performing FFT along axis = " + axis + " as part of IFFT");
        ExecutorService pool2 = Executors.newFixedThreadPool(3);
        pool2.execute(new FFTThread(red, axis, x, y));
        pool2.execute(new FFTThread(green,axis, x, y));
        pool2.execute(new FFTThread(blue, axis, x, y));
        pool2.shutdown();
        awaitTerminationAfterShutdown(pool2);

        // No real point in parallelizing since operation (conjugate) is very fast
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                red[i][j] = red[i][j].conjugate();
                green[i][j] = green[i][j].conjugate();
                blue[i][j] = blue[i][j].conjugate();
            }
        }

        int n;
        if (axis == 0) {
            n = y;
        }
        else {
            n = x;
        }
        // No real point in parallelizing since operation (scale) is very fast
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                red[i][j] = red[i][j].scale(1.0 / n);
                green[i][j] = green[i][j].scale(1.0 / n);
                blue[i][j] = blue[i][j].scale(1.0 / n);
            }
        }

        LOG.debug("Sending IFFT along axis = " + axis + " back to backend");
        oos.writeObject(red);
        oos.writeObject(green);
        oos.writeObject(blue);
    }
}
