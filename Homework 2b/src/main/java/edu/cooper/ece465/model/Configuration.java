package edu.cooper.ece465.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Configuration {
    private String configFile;

    private int numThreads;
    private String outputFile;
    private ArrayList<String> sockets;
    private int numVertices;
    private int minEdgesPerVertex;
    private int maxCost;

    public Configuration(String c) throws IOException, ParseException {
        this.configFile = c;
        parseConfigFile();
    }

    private void parseConfigFile() throws IOException, ParseException {
        FileReader reader = new FileReader(this.configFile);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

        setNumThreads(((Long) jsonObject.get("numThreads")).intValue());
        setOutputFile((String) jsonObject.get("outputFile"));
        setSockets((ArrayList<String>) jsonObject.get("sockets"));
        setNumVertices(((Long) jsonObject.get("numVertices")).intValue());
        setMinEdgesPerVertex(((Long) jsonObject.get("minEdgesPerVertex")).intValue());
        setMaxCost(((Long) jsonObject.get("maxCost")).intValue());
        reader.close();
    }


    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public ArrayList<String> getSockets() {
        return sockets;
    }

    public void setSockets(ArrayList<String> sockets) {
        this.sockets = sockets;
    }

    public int getNumVertices() {
        return numVertices;
    }

    public void setNumVertices(int numVertices) {
        this.numVertices = numVertices;
    }

    public int getMinEdgesPerVertex() {
        return minEdgesPerVertex;
    }

    public void setMinEdgesPerVertex(int minEdgesPerVertex) {
        this.minEdgesPerVertex = minEdgesPerVertex;
    }

    public int getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(int maxCost) {
        this.maxCost = maxCost;
    }
}



//public class Configuration {
//    /**
//     * Configuration for the program for easier change of important parameters.
//     */
//    private static final long numThreads = 4; // Number of threads for each node to use
//    private static final String graphFile = "graph.txt"; // Graph file
//    private static final String outputFile = "output.txt"; // Output file
//    private static final ArrayList<String> sockets = new ArrayList<String>(Arrays.asList(
//            "127.0.0.1:1859",
//            "127.0.0.1:1860"
//    )); // ArrayList of sockets
//
//    private static final int numVertices = 1000; // Number of vertices for randomly generated graph
//    private static final int minEdgesPerVertex = 50; // Minimum number of edges per vertex
//    private static final int maxCost = 30; // Maximum cost per edge
//
//    public static long getNumThreads(){
//        return numThreads;
//    }
//
//    public static ArrayList<String> getSockets() {
//        return sockets;
//    }
//
//    public static String getGraphFile() {
//        return graphFile;
//    }
//
//    public static String getOutputFile() {
//        return outputFile;
//    }
//
//    public static int getNumVertices() {
//        return numVertices;
//    }
//
//
//}
