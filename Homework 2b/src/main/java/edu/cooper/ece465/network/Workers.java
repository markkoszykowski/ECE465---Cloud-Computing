package edu.cooper.ece465.network;

import edu.cooper.ece465.model.Configuration;

import java.util.ArrayList;
import java.util.Scanner;

public class Workers {
    /**
     * Creates and connects the "Nodes" to the coorindator.
     */
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration("config.json");
        System.out.println(configuration.getNumThreads());
        ArrayList<String> sockets = configuration.getSockets();
        for (int i = 0; i<sockets.size(); i++) {
            new Thread(new Worker(sockets.get(i).substring(0, sockets.get(i).indexOf(':')),
                    Integer.parseInt(sockets.get(i).substring(sockets.get(i).indexOf(':') + 1)),
                    configuration.getNumThreads()))
                    .start();
            Thread.sleep(2000);

        }
    }
}