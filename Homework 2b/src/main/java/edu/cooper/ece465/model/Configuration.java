package edu.cooper.ece465.model;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Configuration {
    private final String configFile;

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

    private void parseConfigFile() throws IOException, ParseException, ClassCastException {
        FileReader reader = new FileReader(this.configFile);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

        setNumThreads(((Long) jsonObject.get("numThreads")).intValue());
        setOutputFile((String) jsonObject.get("outputFile"));
        setSockets((JSONArray) jsonObject.get("sockets"));
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

    public void setSockets(JSONArray sockets) {
        this.sockets = new ArrayList<>();
        for (Object socket : sockets) {
            this.sockets.add((String) socket);
        }
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