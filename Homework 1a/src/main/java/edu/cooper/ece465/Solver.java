package edu.cooper.ece465;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Solver implements Runnable {

    private Log LOG = LogFactory.getLog(Solver.class);
    private Graph g;
    private String begin;
    private int number;

    public Solver(Graph g, String begin, int number) {
        this.g = g;
        this.begin = begin;
        this.number = number;
    }

    public void run() {
        LOG.debug("Solver.run() - begin");

        Node start = this.g.getNode(this.begin);
        PriorityQueue<Node> minHeap = new PriorityQueue<>(this.g.getSize(), Comparator.comparingInt(n -> n.getDist(this.number)));

        start.setDist(this.number, 0);
        start.getShortPath(this.number).add(this.begin);

        minHeap.addAll(this.g.getNodeList());

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
                    this.g.getNode(e.getDest().getName()).setDist(this.number, e.getDest().getDist(this.number));
                    if (!minHeap.isEmpty()) {
                        minHeap.add(minHeap.remove());
                    }
                }
            }
        }
        LOG.debug("Solver.run() - end");
    }
}