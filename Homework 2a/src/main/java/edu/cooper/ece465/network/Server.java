package edu.cooper.ece465.network;

import edu.cooper.ece465.model.Graph;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.Socket;
import java.net.ServerSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
    private static final ArrayList<ObjectInputStream> ois = new ArrayList<>();
    private static final ArrayList<ObjectOutputStream> oos = new ArrayList<>();
    private static final Scanner in = new Scanner(System.in);
    private static final Logger LOG = LogManager.getLogger(Server.class);

    public static void main(String[] args) throws Exception {
        LOG.debug("Server.main() started");

        Graph g = new Graph();
        String graphFile;
        System.out.println("Enter the name of a graph file: ");
        graphFile = in.next();
        g.makeGraph(graphFile);

        System.out.println("Enter the name of ports file: ");
        File portsFile = new File(in.next());
        BufferedReader br = new BufferedReader(new FileReader(portsFile));

        String port;
        int i = 0;
        while ((port = br.readLine()) != null) {
            try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))) {
                System.out.println("Listening to port: " + Integer.parseInt(port));
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected to " + clientSocket);

                ois.set(i, new ObjectInputStream(clientSocket.getInputStream()));
                oos.set(i, new ObjectOutputStream(clientSocket.getOutputStream()));

                // Do shit here (send graph and receive graph)

                ois.get(i).close();
                oos.get(i).close();
                clientSocket.close();
                i += 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LOG.debug("Server.main() ended");
    }
}
