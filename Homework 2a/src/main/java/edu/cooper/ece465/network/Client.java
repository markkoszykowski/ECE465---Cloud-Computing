package edu.cooper.ece465.network;

import edu.cooper.ece465.model.Graph;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client implements Runnable {
    private final int port;
    private Graph graph;
    private static final Logger LOG = LogManager.getLogger(Client.class);

    public Client(int p) {
        this.port = p;
    }

    @Override
    public void run() {
        LOG.debug("Client.run() started");

        try (Socket socket = new Socket("localhost", this.port)) {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            // Do shit here (get graph, nodes to calculate, & send back to server)

            ois.close();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOG.debug("Client.run() ended");
    }
}
