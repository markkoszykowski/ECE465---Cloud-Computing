package edu.cooper.ece465.network;

import edu.cooper.ece465.model.Graph;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import edu.cooper.ece465.threading.DijkstraParallel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client implements Runnable {
    public int NUM_THREADS = 4;

    private final int port;
    private final InetAddress ip;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    private static final Logger LOG = LogManager.getLogger(Client.class);

    public Client(String ip, int p) throws Exception {
        this.ip = InetAddress.getByName(ip);
        this.port = p;
    }

    @Override
    public void run() {
        LOG.debug("Client.run() started");

        try (Socket socket = new Socket(this.ip, this.port)) {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());

            Package p = (Package) this.ois.readObject();
            Graph graph = p.getGraph();
            DijkstraParallel dijkstraParallel = new DijkstraParallel();
            dijkstraParallel.solveShortestPaths(graph, this.NUM_THREADS, p.getStart(), p.getEnd());

            this.oos.writeObject(p);

            this.oos.close();
            this.ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOG.debug("Client.run() ended");
    }
}