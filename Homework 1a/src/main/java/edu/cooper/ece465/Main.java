package edu.cooper.ece465;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        String inFile, outFile;
        Graph g = new Graph();

        Scanner in = new Scanner(System.in);

        System.out.println("Enter name of graph file: ");
        inFile = in.nextLine();
        g.makeGraph(inFile);

        g.printGraph();

        g.distributedDijkstra();

        System.out.println("Enter name of output file: ");
        outFile = in.nextLine();
        g.makeOut(outFile);
    }
}