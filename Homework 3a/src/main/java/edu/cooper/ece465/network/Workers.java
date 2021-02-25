package edu.cooper.ece465.network;

import edu.cooper.ece465.model.Configuration;

import java.util.ArrayList;

public class Workers {
    /**
     * Creates and connects the "Nodes" to the coordinator.
     */
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration("../config.json");
        System.out.println("Number of threads per 'Node': " + configuration.getNumThreads());
        ArrayList<String> sockets = configuration.getSockets();
        for (String socket : sockets) {
            new Thread(new Worker(socket.substring(0, socket.indexOf(':')),
                    Integer.parseInt(socket.substring(socket.indexOf(':') + 1)),
                    configuration.getNumThreads())).start();
            Thread.sleep(2000);
        }
    }
}