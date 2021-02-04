package edu.cooper.ece465.model;

public class Edge {
    private Node dest;
    private int cost;

    public Edge(Node dest, int cost) {
        this.dest = dest;
        this.cost = cost;
    }

    public Node getDest() { return this.dest; }

    public int getCost() { return this.cost; }
}