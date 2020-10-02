package com.b14.model;

import java.awt.*;
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
    private float dissonanceIncrease; // negative, in case of conflicting information (Not in use)
    private static int connectionLimit = 50; // for now. If we want super spreaders or influencers we might want to go back to create limit for each object.
    
    // Some stats (also needed for dissonance updates)
    private int numberOfContacts;
    private int numberOfConflicts;

    // Sampling seed
    private final static Random random = new Random(0);

    //Graphics
    private float size = 30.0f;


    public Node(int id) {
        this.id = id;
        neighbours = new ArrayList<>();
        belief = random.nextFloat();
        openness = 0.05f + random.nextFloat()*0.2f-0.05f;
        confidenceSet = new ArrayList<>();
        dissonanceThreshold = 0.5f + random.nextFloat()*1.5f-0.5f;
        dissonanceDecay = 0.5f;
        dissonanceDecrease = -0.05f;
        dissonanceIncrease = 0.3f;
        numberOfConflicts = 0;
        numberOfContacts = 0;
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
     * Update Dissonance according to a leaky diffusion process.
     * The updating equation is the optimized baseline activation
     * implemented in ACT-R: http://act-r.psy.cmu.edu/ + some noise.
     * 
     * Details can also be found in the AOI reader from 2019 by Dr. J. Borst.
     * 
     * We motivate this choice in our report.
     */

    public void updateDissonance() {
        currentDissonance = ((float) Math.log(numberOfConflicts/(1.0f-dissonanceDecay)))
                            - (dissonanceDecay * (float) Math.log(numberOfContacts))
                            + (float) random.nextGaussian()*0.01f;
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
            } else {
                numberOfConflicts += 1;
                if (currentDissonance > dissonanceThreshold) {
                    prunedConnections.add(n);
                    
                }
            numberOfContacts += 1; // update number of total contacts
            updateDissonance();
            }
        }
        for (Node n : prunedConnections) {
            removeNeighbour(n);
            currentDissonance += dissonanceDecrease; // reduction strategy has minimal (still linear) immediate effect (currently).
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
                    currentDissonance += dissonanceDecrease; // If I hear what I want to hear that makes me feel good (initially only, mere exposure!!)
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

    /**
     * Determines the color of the node based on belief.
     * @return the color of the node
     */
    public Color getColorBelief(boolean transparent) {

        float beliefDeviation = Math.abs(0.5f - belief) * 2;

        int r = (int)((belief > 0.5 ? 1 : 0) * 255 * beliefDeviation);
        int g = 0;
        int b = (int)((belief < 0.5 ? 1 : 0) * 255 * beliefDeviation);
        int a = 127 + ((transparent ? 0 : 1) * 128);

        return new Color(r, g, b, a);
    }

    /**
     * Determines the color of the node based on dissonance
     * @return the color of the node
     */
    public Color getColorDissonance(boolean transparent) {
        int isDistressed = (isDissonanceOverThreshold() ? 255 : 0);
        int a = 127 + ((transparent ? 0 : 1) * 128);

        return new Color(0, isDistressed, 0, a);
    }


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

    public int getNumberOfContacts() {
        return numberOfContacts;
    }

    public int getNumberOfConflicts() {
        return numberOfConflicts;
    }

    /**
     * Setters
     */

     public void setDissonance(float value) {
        currentDissonance += value;
     }

     public static void setConnectionLimit(int connectionLimit) {
         Node.connectionLimit = connectionLimit;
     }

    @Override
    public String toString() {
        return  "Node:                     " + id                    + "\n" +
                "Current Belief:        " + belief                + "\n" +
                "Openness margin:  " + openness              + "\n" +
                "Current dis:            " + currentDissonance     + "\n" +
                "Max. dis:                " + dissonanceThreshold   + "\n" +
                "Num. neighbours:  " + neighbours.size()     + "/"  + connectionLimit + "\n";
    }
}