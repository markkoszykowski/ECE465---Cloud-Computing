package edu.cooper.ece465.network;

import edu.cooper.ece465.model.Graph;

import java.io.Serializable;

public class Package implements Serializable {
    private final int start, end;
    private final Graph graph;

    public Package(int s, int e, Graph g) {
        this.start = s;
        this.end = e;
        this.graph = g;
    }

    public Graph getGraph() { return this.graph; }

    public int getStart() { return this.start; }

    public int getEnd() { return this.end; }
}