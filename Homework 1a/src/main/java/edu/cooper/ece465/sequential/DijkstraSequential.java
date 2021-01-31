package edu.cooper.ece465.sequential;


import edu.cooper.ece465.model.Edge;
import edu.cooper.ece465.model.Node;
import edu.cooper.ece465.model.Graph;
import edu.cooper.ece465.parallel.DijkstraThread;

import java.util.Comparator;
import java.util.PriorityQueue;


public class DijkstraSequential {

    public void solveAllPairsShortestPath(Graph graph) {
        for (int i = 0; i < graph.getSize(); i++) {
            solveSingleSourceShortestPath(graph, graph.nodeList.get(i).getName(), i);
        }

    }


    public void solveSingleSourceShortestPath(Graph graph, String begin, int number) {
        Node start = graph.getNode(begin);
        PriorityQueue<Node> minHeap = new PriorityQueue<Node>(graph.getSize(), Comparator.comparingInt(n -> {
            return n.getDist(number);
        }));

        start.setDist(number, 0);
        start.getShortPath(number).add(begin);

        minHeap.addAll(graph.getNodeList());

        Node temp = null;
        while (!minHeap.isEmpty()) {
            temp = minHeap.remove();
            temp.setKnown(number);
            for (Edge e : temp.getAdjList()) {
                if (!e.getDest().getKnown(number) &&
                        temp.getDist(number) != Integer.MAX_VALUE &&
                        (temp.getDist(number) + e.getCost() < e.getDest().getDist(number))) {
                    e.getDest().setShortPath(number, temp.getShortPath(number));
                    e.getDest().getShortPath(number).add(e.getDest().getName());

                    e.getDest().setDist(number, e.getCost() + temp.getDist(number));
                    if (!minHeap.isEmpty()) {
                        minHeap.add(minHeap.remove());
                    }
                }
            }
        }
    }



}
