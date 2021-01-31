package edu.cooper.ece465.parallel;

import edu.cooper.ece465.model.Graph;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DijkstraParallel {
    public void solveAllPairsShortestPath(Graph graph, int numThreads) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < graph.getSize(); i++) {
            executor.execute(new DijkstraThread(graph, graph.nodeList.get(i).getName(), i));
        }
        awaitTerminationAfterShutdown(executor);
    }

    public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(10000, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}