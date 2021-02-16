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

        Graph graph = new Graph();
        String inFile, outFile;
        System.out.println("Enter the name of a graph file: ");
        inFile = in.next();
        graph.makeGraph(inFile);

        System.out.println("Enter the name of IPs file: ");
        File portsFile = new File(in.next());
        BufferedReader br = new BufferedReader(new FileReader(portsFile));

        ArrayList<ObjectInputStream> ois = new ArrayList<>();
        ArrayList<ObjectOutputStream> oos = new ArrayList<>();

        String ip;
        int numClients = 0;
        while ((ip = br.readLine()) != null) {
            try(ServerSocket serverSocket = new ServerSocket(Integer.parseInt(ip.substring(ip.indexOf(':') + 1)))) {
                System.out.println("Listening to " + ip);
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected to " + clientSocket);

                ois.add(new ObjectInputStream(clientSocket.getInputStream()));
                oos.add(new ObjectOutputStream(clientSocket.getOutputStream()));

                numClients += 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int numVert = graph.getNodeList().size();
        int factor = (int) Math.floor(numVert/numClients);

        int n = 0;
        for (int j = 0; j < numClients; j++) {
            if(j < numVert % numClients) {
                oos.get(j).writeObject(new Package(j*factor + n, ((j*factor) + factor) + 1 + n, graph));
                n += 1;
            } else {
                oos.get(j).writeObject(new Package(j*factor + n, ((j*factor) + factor) + n, graph));
            }
        }

        for (int j = 0; j < numClients; j++) {
            Package p = (Package) ois.get(j).readObject();
            for (int k = p.getStart(); k < p.getEnd(); k++) {
                for (int a = 0; a < numVert; a++) {
                    graph.getNodeList().get(a).setDist(k, p.getGraph().getNodeList().get(a).getDist(k));
                    graph.getNodeList().get(a).setShortPath(k, p.getGraph().getNodeList().get(a).getShortPath(k));
                }
            }
        }

        for (int j = 0; j < numClients; j++) {
            ois.get(j).close();
            oos.get(j).close();
        }

        System.out.println("Enter the name of an output file: ");
        outFile = in.next();
        graph.makeOut(outFile);

        LOG.debug("Server.main() ended");
    }
}