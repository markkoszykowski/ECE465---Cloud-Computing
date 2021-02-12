package edu.cooper.ece465.model;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Hashtable;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class Graph {
    public List<Node> nodeList;
    private Hashtable<String, Node> encountered;

    public Graph() {
        this.nodeList = new ArrayList<>();
        this.encountered = new Hashtable<>();
    }

    public int getSize() { return this.nodeList.size(); }

    public List<Node> getNodeList() {
        return this.nodeList;
    }

    public Node getNode(String name) {
        return this.encountered.get(name);
    }

    public void initializeGraph() {
        for (Node n : this.nodeList) {
            n.setup(this.getSize());
        }
    }

    public void makeGraph(String inFile) throws Exception {
        File graphFile = new File(inFile);
        BufferedReader br = new BufferedReader(new FileReader(graphFile));

        String begin, end, cost;
        Node beginNode, endNode;
        while ((begin = br.readLine()) != null && (end = br.readLine()) != null && (cost = br.readLine()) != null) {
            if (!this.encountered.containsKey(begin)) {
                Node temp = new Node(begin);

                this.nodeList.add(temp);
                this.encountered.put(begin, temp);
            }
            beginNode = encountered.get(begin);

            if (!this.encountered.containsKey(end)) {
                Node temp = new Node(end);

                this.nodeList.add(temp);
                this.encountered.put(end, temp);
            }
            endNode = encountered.get(end);

            Edge e = new Edge(endNode, Integer.parseInt(cost));
            beginNode.addAdj(e);
        }
        br.close();

        this.initializeGraph();
    }

    public void printGraph() {
        for (Node n : this.nodeList) {
            System.out.println("Origin Node Name: " + n.getName());
            for (Edge e : n.getAdjList()) {
                System.out.println("\tDestination Node Name: " + e.getDest().getName());

                System.out.println("\tCost: " + e.getCost() + "\n");
            }
            if (n.getAdjList().isEmpty()) { System.out.println("\tNo Destinations\n"); }
        }
    }

    public void generateRandomGraph(int numNodes, int edgesPerNode, int maxCost) {
        String name;
        Node node;
        for (int i = 1; i <= numNodes; i++) {
            name = "v" + i;
            node = new Node(name);
            this.nodeList.add(node);
            this.encountered.put(name, node);
        }

        Node endNode;
        for (Node n : this.nodeList) {
            for (int j = 0; j < edgesPerNode; j++) {
                endNode = this.encountered.get("v" + ThreadLocalRandom.current().nextInt(1, numNodes+1));
                Edge edge = new Edge(endNode, ThreadLocalRandom.current().nextInt(1, maxCost+1));
                n.addAdj(edge);
            }
        }

        this.initializeGraph();
    }

    public void makeOut(String outFile) throws Exception {
        PrintWriter shortFile = new PrintWriter(outFile, StandardCharsets.UTF_8);
        for (int i = 0; i < this.getSize(); i++) {
            shortFile.println("Shortest paths starting from " + this.nodeList.get(i).getName());
            for (Node n : this.nodeList) {
                shortFile.printf("%s: ", n.getName());

                if (n.getShortPath(i).isEmpty()) {
                    shortFile.println("NO PATH");
                } else {
                    shortFile.printf("%d [", n.getDist(i));
                    for (String s : n.getShortPath(i)) {
                        shortFile.printf("%s", s);
                        if (!n.getShortPath(i).get(n.getShortPath(i).size() - 1).equals(s)) {
                            shortFile.printf(", ");
                        }
                    }
                    shortFile.println("]");
                }
            }
            shortFile.println();
        }
        shortFile.close();
    }
}