package edu.cooper.ece465.model;

import java.io.Serializable;

public class Edge implements Serializable {
    private final Node dest;
    private final int cost;

    public Edge(Node dest, int cost) {
        this.dest = dest;
        this.cost = cost;
    }

    public Node getDest() { return this.dest; }

    public int getCost() { return this.cost; }
}