package edu.cooper.ece465.network;

import edu.cooper.ece465.model.Graph;

import java.io.Serializable;

public class Package implements Serializable {
    /**
     * A class that carries data necessary for transfer between the coordinator and the workers.
     * A Package instance carries the graph, and the starting and ending vertices for the given worker.
     * The Package class allows for streamlined communication.
     */
    private final int start, end;
    private final Graph graph;

    public Package(int s, int e, Graph g) {
        this.start = s;
        this.end = e;
        this.graph = new Graph(g);
    }

    public Graph getGraph() { return this.graph; }

    public int getStart() { return this.start; }

    public int getEnd() { return this.end; }
}