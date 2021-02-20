package edu.cooper.ece465.network;

import edu.cooper.ece465.model.Configuration;
import edu.cooper.ece465.model.Graph;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.net.Socket;
import java.net.ServerSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Coordinator {
    private static final Logger LOG = LogManager.getLogger(Coordinator.class);

    public static void main(String[] args) throws Exception {
        LOG.debug("Coordinator.main() started");

        Configuration configuration = new Configuration("../config.json");

        Graph graph = new Graph();
        /*graph.generateRandomGraph(configuration.getNumVertices(),
                configuration.getMinEdgesPerVertex(),
                configuration.getMaxCost());*/
        graph.makeGraph(configuration.getInputFile());
        ArrayList<ObjectInputStream> ois = new ArrayList<>();
        ArrayList<ObjectOutputStream> oos = new ArrayList<>();

        int numNodes = 0;
        ArrayList<String> sockets = configuration.getSockets();
        for (String socket : sockets) {
            try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(socket.substring(socket.indexOf(':') + 1)))) {
                System.out.println("Listening to " + socket);
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected to " + clientSocket);

                ois.add(new ObjectInputStream(clientSocket.getInputStream()));
                oos.add(new ObjectOutputStream(clientSocket.getOutputStream()));

                numNodes += 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int numVert = graph.getNodeList().size();
        int factor = (int) Math.floor(numVert/numNodes);

        int n = 0;
        for (int j = 0; j < numNodes; j++) {
            if(j < numVert % numNodes) {
                oos.get(j).writeObject(new Package(j*factor + n, ((j*factor) + factor) + 1 + n, graph));
                n += 1;
            } else {
                oos.get(j).writeObject(new Package(j*factor + n, ((j*factor) + factor) + n, graph));
            }
        }

        for (int j = 0; j < numNodes; j++) {
            Package p = (Package) ois.get(j).readObject();
            for (int k = p.getStart(); k < p.getEnd(); k++) {
                for (int a = 0; a < numVert; a++) {
                    graph.getNodeList().get(a).setDist(k, p.getGraph().getNodeList().get(a).getDist(k));
                    graph.getNodeList().get(a).setShortPath(k, p.getGraph().getNodeList().get(a).getShortPath(k));
                }
            }
        }

        for (int j = 0; j < numNodes; j++) {
            ois.get(j).close();
            oos.get(j).close();
        }

        graph.makeOut(configuration.getOutputFile());

        LOG.debug("Coordinator.main() ended");
    }
}