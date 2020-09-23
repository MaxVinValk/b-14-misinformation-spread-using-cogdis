package com.b14.model;

import java.util.ArrayList;
import java.util.Random;


/**
 *  The node in the network, aka the representation of a user
 */

public class Node extends Physics2DObject {

    protected ArrayList<Node> neighbours;
    protected int id;

    // Tuning parameters
    private float belief; // agent's belief at current time
    private float openness; // how far another belief can be away from your's before being rejected
    private float currentDissonance = 0.0f;
    private float dissonanceThreshold; // dissonance becomes unbearable, agent engages in drastic measures: pruning network
    private ArrayList<Node> confidenceSet; // in theory only values would be possible as well.
    private float dissonanceDecay;
    private float dissonanceDecrease; // in case of a positive interaction
    private float dissonanceIncrease; // negative, in case of conflicting information
    private static int connectionLimit = 50; // for now. If we want super spreaders or influencers we might want to go back to create limit for each object. 

    // Sampling seed
    private final static Random random = new Random(0);

    //Graphics
    private float size = 30.0f;


    public Node(int id) {
        this.id = id;
        neighbours = new ArrayList<>();
        belief = random.nextFloat();
        openness = 0.1f + random.nextFloat()*0.3f-0.1f;
        confidenceSet = new ArrayList<>();
        dissonanceThreshold = 0.5f + random.nextFloat()*1.5f-0.5f;
        dissonanceDecay = -0.1f;
        dissonanceDecrease = -0.1f;
        dissonanceIncrease = 0.3f;
        reset();
    }

    /**
     * Adds the passed in node as neighbour, if not already labelled as such
     * and number of existing neighbors is below the connection limit.
     * @param node The neighbour to add.
     */

    public void addNeighbour(Node node) {
        if ((!neighbours.contains(node)) && canTwoConnect(node)) {
            neighbours.add(node);
            node.getNeighbours().add(this);
        }
    }

    /**
     * Checks whether for both nodes the number of existing neighbours is
     * less than the connection limit.
     * @param node the node chosen for comparison with this instance of Node.
     */

    public boolean canTwoConnect(Node node) {
        return ((neighbours.size() < connectionLimit) &&
                (node.getNeighbours().size() < connectionLimit));
    }

    /**
     * Removes the passed in node as neighbour
     * @param neighbour The neighbour to remove
     */

    public void removeNeighbour(Node neighbour) {
        if (neighbours.contains(neighbour)) {
            neighbours.remove(neighbour);
            neighbour.getNeighbours().remove(this);
        }
    }

    /**
     * Update Dissonance using a super simple example function.
     * @param value Value by which to update the current dissonance level.
     */

    public void updateDissonance(float value) {
        currentDissonance += value;
        currentDissonance = currentDissonance < 0.0f ? 0.0f: currentDissonance;
    }

    /**
     * Receive messages from all Neighbors, add those in reccomended, update belief and dissonance.
     * @param recommended Set of recommended nodes chosen by algorithm implemented in GraphModel
    */

    public void receiveMessages(ArrayList<Node> recommended) {
        ArrayList<Node> possibleConnections = new ArrayList<>(neighbours);
        ArrayList<Node> prunedConnections = new ArrayList<>();
        possibleConnections.addAll(recommended);

        for (Node n : possibleConnections) {
            if(Math.abs(n.belief - belief) < openness) {
                confidenceSet.add(n);
                addNeighbour(n); // update if other agent was in reccomended
                //updateDissonance(dissonanceDecrease/2.0f); // If I here something I like, I feel better.
            } else {
                updateDissonance(dissonanceIncrease);
                if (currentDissonance > dissonanceThreshold) {
                    prunedConnections.add(n);
                    
                }
            }
        }
        for (Node n : prunedConnections) {
            removeNeighbour(n);
            updateDissonance(dissonanceDecrease); // reduction strategy has minimal immediate effect (currently).
        }
        updateBelief();
    }

    /**
     * Update belief of agent.
    */

    public void updateBelief() {
        float weight = 1.0f/(confidenceSet.size() + 1);
        float nextBelief = weight * belief;
        for (Node n : confidenceSet) {
            nextBelief += weight * n.belief;
        }
        belief = nextBelief;

    }

    /**
     * Basically fraternize by Max.
    */

    public void fraternize() {
        ArrayList<Node> possibleConnections = new ArrayList<>();
        for (Node n : neighbours) {
            for (Node n2 : n.getNeighbours()) {
                if (n2 == this || n == n2) {
                    continue;
                }
                if(Math.abs(n2.belief - belief) < openness) {
                    possibleConnections.add(n2);
                }
            }
        }

        for (Node n : possibleConnections) {
            addNeighbour(n);
        }
    }

    /**
     * Returns whether or not a given position (in world coordinates) falls within a node
     * @param x The x-coordinate of the position to check
     * @param y The y-coordinate of the position to check
     * @return True iff (x,y) is within the node, False otherwise
     */

    public boolean pointInNode(double x, double y) {

        //First we check if it is within the bounding box surrounding the sphere
        float radius = size / 2;

        if (isInBetween(position.getX() - radius, position.getX() + radius, x) &&
            isInBetween(position.getY() - radius, position.getY() + radius, y)) {

            //More expensive check to see if it is actually located in the right position
            if ( Math.pow(x - position.getX(), 2) + Math.pow(y - position.getY(), 2) < radius*radius) {
                return true;
            }
        }
        return false;
    }

    //utility functions
    private boolean isInBetween(double lower, double upper, double number) {
        return (number >= lower && number < upper);
    }

    /**
     * Getters 
     */

    public void reset() {
        confidenceSet.clear();
    }

    public int getConnectionLimit() {
        return connectionLimit;
    }

    public float getBelief() {
        return belief;
    }

    public float getCurrentDissonance() {
        return currentDissonance;
    }

    public float getDissonanceThreshold() {
        return dissonanceThreshold;
    }

    public boolean isDissonanceOverThreshold() {
        return (currentDissonance > dissonanceThreshold);
    }

    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }

    public ArrayList<Node> getConfidenceSet() {
        return confidenceSet;
    }

    public int getId() {
        return id;
    }

    public float getSize() {
        return size;
    }

    public boolean getCanConnect() {
        return neighbours.size() < connectionLimit;
    }

    public float getDissonanceDecay() {
        return dissonanceDecay;
    }

    public float getOpenness() {
        return openness;
    }


    @Override
    public String toString() {
        return  "Node:\t\t\t"           + id                    + "\n" +
                "Current Belief:\t"     + belief                + "\n" +
                "Openness margin:\t"    + openness                 + "\n" +
                "Current dis:\t"        + currentDissonance     + "\n" +
                "Max. dis:\t\t"         + dissonanceThreshold   + "\n" +
                "Num. neighbours:\t"    + neighbours.size()     + "/"  + connectionLimit + "\n";
    }
}