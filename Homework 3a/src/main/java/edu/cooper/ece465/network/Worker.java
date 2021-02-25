package edu.cooper.ece465.network;

import edu.cooper.ece465.model.Graph;
import edu.cooper.ece465.threading.DijkstraParallel;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Worker implements Runnable {
    private final int port;
    private final InetAddress ip;
    private final int numThreads;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    private static final Logger LOG = LogManager.getLogger(Worker.class);

    public Worker(String ip, int p, int n) throws Exception {
        this.ip = InetAddress.getByName(ip);
        this.port = p;
        this.numThreads = n;
    }

    @Override
    public void run() {
        LOG.debug("Worker.run() started [port: " + this.port + "]");

        try (Socket socket = new Socket(this.ip, this.port)) {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());

            Package p = (Package) this.ois.readObject();
            Graph graph = p.getGraph();
            DijkstraParallel dijkstraParallel = new DijkstraParallel();
            dijkstraParallel.solveShortestPaths(graph, this.numThreads, p.getStart(), p.getEnd());

            this.oos.writeObject(p);

            this.oos.close();
            this.ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOG.debug("Worker.run() ended [port: " + this.port + "]");
    }
}