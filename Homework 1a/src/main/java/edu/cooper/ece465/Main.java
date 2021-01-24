package edu.cooper.ece465;

import java.util.Scanner;

public class Main {
    public static void main(String args[]) throws Exception {
        String inFile, outFile, vertex;
        Graph g = new Graph();

        Scanner in = new Scanner(System.in);

        System.out.println("Enter name of graph file: ");
        inFile = in.nextLine();
        g.makeGraph(inFile);

        g.printGraph();

        do {
            System.out.println("Enter a valid vertex name for the starting vertex: ");
            vertex = in.nextLine();
        } while(!g.checkHash(vertex));

        g.dijkstra(vertex);

        System.out.println("Enter name of output file: ");
        outFile = in.nextLine();
        g.makeOut(outFile);

        return;
    }
}
