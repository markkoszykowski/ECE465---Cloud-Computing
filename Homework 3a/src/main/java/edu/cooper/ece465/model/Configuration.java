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
    private String inputFile;
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
        setInputFile((String) jsonObject.get("inputFile"));
        setOutputFile((String) jsonObject.get("outputFile"));
        setSockets((JSONArray) jsonObject.get("sockets"));
        setNumVertices(((Long) jsonObject.get("numVertices")).intValue());
        setMinEdgesPerVertex(((Long) jsonObject.get("minEdgesPerVertex")).intValue());
        setMaxCost(((Long) jsonObject.get("maxCost")).intValue());
        reader.close();
    }


    public int getNumThreads() { return this.numThreads; }

    public void setNumThreads(int numThreads) { this.numThreads = numThreads; }

    public String getInputFile() { return this.inputFile; }

    public void setInputFile(String inputFile) { this.inputFile = inputFile; }

    public String getOutputFile() { return this.outputFile; }

    public void setOutputFile(String outputFile) { this.outputFile = outputFile; }

    public ArrayList<String> getSockets() { return this.sockets; }

    public void setSockets(JSONArray sockets) {
        this.sockets = new ArrayList<>();
        for (Object socket : sockets) {
            this.sockets.add((String) socket);
        }
    }

    public int getNumVertices() { return this.numVertices; }

    public void setNumVertices(int numVertices) { this.numVertices = numVertices; }

    public int getMinEdgesPerVertex() { return this.minEdgesPerVertex; }

    public void setMinEdgesPerVertex(int minEdgesPerVertex) { this.minEdgesPerVertex = minEdgesPerVertex; }

    public int getMaxCost() { return this.maxCost; }

    public void setMaxCost(int maxCost) { this.maxCost = maxCost; }
}