package edu.cooper.ece465.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Node implements Serializable {
    private String name;
    private List<Integer> dist;
    private List<Boolean> known;
    private List<Edge> adjList;
    private List<List<String>> shortPath;

    public Node(String name) {
        this.name = name;
        this.dist = new ArrayList<>();
        this.known = new ArrayList<>();
        this.adjList = new ArrayList<>();
        this.shortPath = new ArrayList<>();
    }

    public void setup(int size) {
        for (int i = 0; i < size; i++) {
            this.dist.add(Integer.MAX_VALUE);
            this.known.add(false);
            this.shortPath.add(new ArrayList<>());
        }
    }

    public String getName() { return this.name; }

    public int getDist(int ind) { return this.dist.get(ind); }

    public void setDist(int ind, int val) { this.dist.set(ind, val); }

    public List<Integer> getDistList() { return this.dist; }

    public void setDistList(List<Integer> dist) { this.dist = new ArrayList<>(dist); }

    public boolean getKnown(int ind) { return this.known.get(ind); }

    public void setKnown(int ind) { this.known.set(ind, true); }

    public List<Boolean> getKnownList() { return this.known; }

    public void setKnownList(List<Boolean> known) { this.known = new ArrayList<>(known); }

    public void addAdj(Edge e) { this.adjList.add(e); }

    public List<Edge> getAdjList() { return this.adjList; }

    public List<String> getShortPath(int ind) { return this.shortPath.get(ind); }

    public void setShortPath(int ind, List<String> shortPath) { this.shortPath.set(ind, new ArrayList<>(shortPath)); }

    public void addShortPath(List<String> shortPath) { this.shortPath.add(new ArrayList<>(shortPath));}

    public List<List<String>> getShortPathList() { return this.shortPath; }
}