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

        long startTime = System.nanoTime();
        g.distributedDijkstra();
        long endTime = System.nanoTime();

        System.out.println("Execution time in milliseconds: " + (endTime - startTime)/1000000 + "\n");

        System.out.println("Enter name of output file: ");
        outFile = in.nextLine();
        g.makeOut(outFile);
    }
}