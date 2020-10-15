package com.b14.model;

import com.b14.model.recommendationstrategies.RecommendationStrategy;

import javax.naming.OperationNotSupportedException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 *  Functionality for the entire network is stored here.
 */

public class GraphModel extends GraphPhysicsModel {

    private int epoch;

    private RecommendationStrategy.Strategy rs;
    private int recommendationSize;

    private DataLogger dl;

    /**
     * initializes a graph model
     * @param dl the data-logger instance that is being used to record data output
     */

    public GraphModel(DataLogger dl) {
        this.dl = dl;

        rs = RecommendationStrategy.Strategy.POLARIZE;
        recommendationSize = 20;
        epoch = 0;
    }

    /**
     *  Setup a simple simulation.
     */

    public void startRandom(int numNodes) {
        epoch = 0;
        super.startRandom(numNodes);
        dl.startNewCapture();
    }

    /**
     *  Performs 1 spreading step for entire network.
     */
    public void simulateSpreadStep() {
        try {
            for (Node n : nodes) {
                n.reset(); // clear confidence set
                ArrayList<Node> recommended = RecommendationStrategy.recommend(rs, nodes, n, recommendationSize);
                n.receiveMessages(recommended);
            }
        } catch (OperationNotSupportedException e) {
            System.err.println("Attempted to use a recommendation strategy that has not been implemented yet in recommend.");
            System.err.println("Update is aborted, but step may have been completed partially. Data may not be valid anymore!");
            return;
        }
        // perform fraternize on entire network AFTER all received message + dissonance update
        for (Node n : nodes) {
            if (n.getCanConnect()) {
                n.fraternize();
            }
        }
        epoch += 1;

        dl.logData(epoch);
        pcs.firePropertyChange(new PropertyChangeEvent(this, "modelChange", null, null));
    }

    /**
     *  Updates dissonance level for each agent.
     *  @param dissonance The update in dissonance that will be applied to each agent.
     */
    public void updateDissonances(float dissonance) {
        for (Node n : nodes) {
            n.setDissonance(dissonance);
        }
    }

    /**
     * Loads in agents on the basis of a CSV file specifying the traits of each agent in the network.
     */
    public void setAgentsFromFile(String filePath) throws FileNotFoundException, IOException {

        nextFreeID = 0;
        nodes.clear();
        epoch = 0;

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine().strip();

        if (!line.equals("neuroticism,extraversion,openness")) {
            throw new IOException();
        }

        do {
            line = reader.readLine();
            if (line != null) {
                String[] vals = line.strip().split(",");

                nodes.add(new Node( nextFreeID++, Float.parseFloat(vals[0]), Float.parseFloat(vals[1]),
                                    Float.parseFloat(vals[2])));
            }
        } while (line != null);

        reader.close();
        setupNetworkStructure();
    }

    // getters

    public int getEpoch() {
        return epoch;
    }

    public int getRecommendationSize() {
        return recommendationSize;
    }

    public RecommendationStrategy.Strategy getRecommendationStrategy() {
        return rs;
    }

    /*
     * Setters
     */

    public void setRecommendationStrategy(RecommendationStrategy.Strategy rs) {
        this.rs = rs;
        pcs.firePropertyChange(new PropertyChangeEvent(this, "recommendSettingsChange", null, null));
    }

    public void setRecommendationSize(int recommendationSize) {
        this.recommendationSize = recommendationSize;
        pcs.firePropertyChange(new PropertyChangeEvent(this, "recommendSettingsChange", null, null));
    }

    public void setConnectionLimitOnNodes(int newLimit) {
        Node.setConnectionLimit(newLimit);

        for(Node n : nodes) {

            while (n.getConnectionCount() > n.getIndividualConnectionLimit()) {
                Node nodeToRemove = n.getNeighbours().get(random.nextInt(n.getConnectionCount()));
                n.removeNeighbour(nodeToRemove);
            }
        }
        pcs.firePropertyChange(new PropertyChangeEvent(this, "modelChange", null, null));
    }

    public void setReweightedOpenness() {
        for (Node n : nodes) {
            n.setReweightedOpenness();
        }
        pcs.firePropertyChange(new PropertyChangeEvent(this, "modelChange", null, null));
    }

}

