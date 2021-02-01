package edu.cooper.ece465;

import edu.cooper.ece465.model.Graph;
import edu.cooper.ece465.parallel.DijkstraParallel;
import edu.cooper.ece465.sequential.DijkstraSequential;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        String inFile, outFile;
        int numThreads;

        Graph g1 = new Graph(); // For parallel
        Graph g2 = new Graph(); // For sequential

        Scanner in = new Scanner(System.in);
        while(true) {
            System.out.println("Number of Threads: ");
            try {
                numThreads = Integer.parseInt(in.next());
            } catch (NumberFormatException e) {
                System.out.println("Number of Threads must be an integer!");
                continue;
            }
            break;
        }

        g1.generateRandomGraph(1000, 10, 20);
        g2.generateRandomGraph(1000, 10, 20);

        DijkstraParallel dijkstraParallel = new DijkstraParallel();
        DijkstraSequential dijkstraSequential = new DijkstraSequential();

//        System.out.println("Enter name of output file: ");
//        outFile = in.nextLine();
//        g1.makeOut(outFile);


        long startTime = System.nanoTime();
        dijkstraParallel.solveAllPairsShortestPath(g1, numThreads);
        long endTime = System.nanoTime();
        System.out.println("Parallel Execution time in milliseconds: " + (endTime - startTime)/1000000 + "\n");

//        g1.initializeGraph();

        startTime = System.nanoTime();
        dijkstraSequential.solveAllPairsShortestPath(g2);
        endTime = System.nanoTime();

        System.out.println("Sequential Execution time in milliseconds: " + (endTime - startTime)/1000000 + "\n");
    }
}