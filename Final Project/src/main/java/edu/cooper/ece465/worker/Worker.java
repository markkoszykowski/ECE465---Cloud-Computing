package edu.cooper.ece465.worker;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
            System.out.println("Hola 0");
            while (true) {
                String status = (String) ois.readObject();
                if (status.equals("TERMINATE")) {
                    LOG.debug("TERMINATING node.");
                    return;
                }
                else if (status.equals("HANDSHAKE")) {
                    oos.writeObject("HANDSHAKE RECEIVED");
                }
                else if (status.equals("JOB")) {
                    LOG.debug("Beginning JOB.");

                    doFFT(ois, oos, 0);

                    doFFT(ois, oos, 1);

                    // Backend will do compression here (throwing away small coefficients)

                    doIFFT(ois, oos, 0);

                    doIFFT(ois, oos, 1);
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
        LOG.debug("Reading pixels values from backend");
        Complex[][] red = (Complex[][]) ois.readObject();
        Complex[][] green = (Complex[][]) ois.readObject();
        Complex[][] blue = (Complex[][]) ois.readObject();

        int x = red.length;
        int y = red[0].length;

        LOG.debug("Performing FFT along axis = " + axis);
        ExecutorService pool = Executors.newFixedThreadPool(3);
        pool.execute(new FFTThread(red, axis, x, y));
        pool.execute(new FFTThread(green,axis, x, y));
        pool.execute(new FFTThread(blue, axis, x, y));
        pool.shutdown();
        awaitTerminationAfterShutdown(pool);

        LOG.debug("Sending FFT along axis = " + axis + " back to backend");
        oos.writeObject(red);
        oos.writeObject(green);
        oos.writeObject(blue);
    }

    public static void doIFFT(ObjectInputStream ois, ObjectOutputStream oos, int axis) throws IOException, ClassNotFoundException {
        Complex[][] red = (Complex[][]) ois.readObject();
        Complex[][] green = (Complex[][]) ois.readObject();
        Complex[][] blue = (Complex[][]) ois.readObject();

        int x = red.length;
        int y = red[0].length;

        LOG.debug("Performing IFFT along axis = " + axis);
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                red[i][j].set(red[i][j].conjugate());
                green[i][j].set(green[i][j].conjugate());
                blue[i][j].set(blue[i][j].conjugate());
            }
        }

        LOG.debug("Performing FFT along axis = " + axis + " as part of IFFT");
        ExecutorService pool = Executors.newFixedThreadPool(3);
        pool.execute(new FFTThread(red, axis, x, y));
        pool.execute(new FFTThread(green,axis, x, y));
        pool.execute(new FFTThread(blue, axis, x, y));
        pool.shutdown();
        awaitTerminationAfterShutdown(pool);

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                red[i][j].set(red[i][j].conjugate());
                green[i][j].set(green[i][j].conjugate());
                blue[i][j].set(blue[i][j].conjugate());
            }
        }

        int n;
        if (axis == 0) {
            n = y;
        }
        else {
            n = x;
        }
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                red[i][j].set(red[i][j].scale(1.0 / n));
                green[i][j].set(green[i][j].scale(1.0 / n));
                blue[i][j].set(blue[i][j].scale(1.0 / n));
            }
        }

        LOG.debug("Sending IFFT along axis = " + axis + " back to backend");
        oos.writeObject(red);
        oos.writeObject(green);
        oos.writeObject(blue);
    }
}
