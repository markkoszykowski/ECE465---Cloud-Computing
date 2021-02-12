package edu.cooper.ece465.network;

import edu.cooper.ece465.model.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.Socket;
import java.net.ServerSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
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

        ArrayList<ObjectInputStream> ois = new ArrayList<>();
        ArrayList<ObjectOutputStream> oos = new ArrayList<>();

        String port;
        int i = 0;
        while ((port = br.readLine()) != null) {
            try(ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))) {
                System.out.println("Listening to port: " + Integer.parseInt(port));
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected to " + clientSocket);

                ois.add(new ObjectInputStream(clientSocket.getInputStream()));
                oos.add(new ObjectOutputStream(clientSocket.getOutputStream()));

                i += 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int j = 0; j < i; j++) {
            System.out.println(ois.get(j).readObject());
        }

        for (int j = 0; j < i; j++) {
            ois.get(j).close();
            oos.get(j).close();
        }

        LOG.debug("Server.main() ended");
    }
}
