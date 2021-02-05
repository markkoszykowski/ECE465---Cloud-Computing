package edu.cooper.ece465.parallel;

import edu.cooper.ece465.model.Edge;
import edu.cooper.ece465.model.Graph;
import edu.cooper.ece465.model.Node;

import java.util.Comparator;
import java.util.PriorityQueue;
import org.apache.log4j.Logger;

public class DijkstraThread implements Runnable {
    private Graph graph;
    private String begin;
    private int number;
    private static final Logger LOG = Logger.getLogger(DijkstraThread.class);

    public DijkstraThread(Graph g, String begin, int number) {
        this.graph = g;
        this.begin = begin;
        this.number = number;
    }

    @Override
    public void run() {
        LOG.debug("DijkstraThread.run() " + this.number + " - begin");
        Node start = this.graph.getNode(this.begin);
        PriorityQueue<Node> minHeap = new PriorityQueue<>(this.graph.getSize(), Comparator.comparingInt(n -> n.getDist(this.number)));

        start.setDist(this.number, 0);
        start.getShortPath(this.number).add(this.begin);

        minHeap.addAll(this.graph.getNodeList());

        Node temp;
        while (!minHeap.isEmpty()) {
            temp = minHeap.remove();
            temp.setKnown(this.number);
            for (Edge e : temp.getAdjList()) {
                if (!e.getDest().getKnown(this.number) &&
                        temp.getDist(this.number) != Integer.MAX_VALUE &&
                        (temp.getDist(this.number) + e.getCost() < e.getDest().getDist(this.number))) {
                    e.getDest().setShortPath(this.number, temp.getShortPath(this.number));
                    e.getDest().getShortPath(this.number).add(e.getDest().getName());

                    e.getDest().setDist(this.number, e.getCost() + temp.getDist(this.number));
                    if (!minHeap.isEmpty()) {
                        minHeap.add(minHeap.remove());
                    }
                }
            }
        }
        LOG.debug("DijkstraThread.run() " + this.number + " - end");
    }
}
