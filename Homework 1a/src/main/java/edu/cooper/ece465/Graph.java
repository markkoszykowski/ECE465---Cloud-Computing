package edu.cooper.ece465;

import java.io.*;
import java.util.*;

public class Graph {

    private class Node {
        String name;
        int dist;
        boolean known;
        List<Edge> adjList;
        List<String> shortPath;

        Node() {
            adjList = new ArrayList<>();
            shortPath = new ArrayList<>();
        }
    }

    private class Edge {
        Node dest;
        int cost;
    }

    private List<Node> nodeList;
    private Hashtable<String, Node> encountered;

    public Graph() {
        nodeList = new ArrayList<>();
        encountered = new Hashtable<>();
    }

    public void makeGraph(String inFile) throws Exception {
        File graphFile = new File(inFile);
        BufferedReader br = new BufferedReader(new FileReader(graphFile));

        String begin, end, cost;
        Node beginNode, endNode;
        while((begin = br.readLine()) != null && (end = br.readLine()) != null && (cost = br.readLine()) != null) {
            Node temp1 = new Node();
            if(!encountered.containsKey(begin)) {
                temp1.name = begin;
                temp1.dist = Integer.MAX_VALUE;
                temp1.known = false;

                nodeList.add(temp1);
                encountered.put(begin, temp1);
            }
            beginNode = encountered.get(begin);

            Node temp2 = new Node();
            if(!encountered.containsKey(end)) {
                temp2.name = end;
                temp2.dist = Integer.MAX_VALUE;
                temp2.known = false;

                nodeList.add(temp2);
                encountered.put(end, temp2);
            }
            endNode = encountered.get(end);

            Edge e = new Edge();
            e.dest = endNode;
            e.cost = Integer.parseInt(cost);
            beginNode.adjList.add(e);
        }
        br.close();
    }

    public void printGraph() {
        for(Node n : nodeList) {
            System.out.println("Origin Node Name: " + n.name);
            for(Edge e : n.adjList) {
                System.out.println("\tDestination Node Name: " + e.dest.name);

                System.out.println("\tCost: " + e.cost + "\n");
            }
            if(n.adjList.isEmpty()) { System.out.println("\tNo Destinations\n"); }
        }
    }

    public void dijkstra(String vertex) {
        System.out.println("Doing something?");
    }

    public boolean checkHash(String vertex) {
        return encountered.containsKey(vertex);
    }

    public void makeOut(String outFile) throws Exception {
        PrintWriter shortFile = new PrintWriter(outFile, "UTF-8");
        for(Node n : nodeList) {
            shortFile.printf("%s: ", n.name);

            if(n.shortPath.isEmpty()) {
                shortFile.println("NO PATH");
            }
            else {
                shortFile.printf(" [");
                for(String s : n.shortPath) {
                    shortFile.printf("%s", s);
                    if(!n.shortPath.get(n.shortPath.size() - 1).equals(s)) {
                        shortFile.printf(", ");
                    }
                }
                shortFile.println("]");
            }
        }
        shortFile.close();
    }
}
