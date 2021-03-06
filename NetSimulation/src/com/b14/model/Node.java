package com.b14.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


/**
 * The node in the network, aka the representation of a user
 */

public class Node extends Physics2DObject {

    // Sampling seed
    private final static Random random = new Random(0);
    private static final int windowSize = 30; // for contact history window
    private static int connectionLimit = 50; // for now. If we want super spreaders or influencers we might want to go back to create limit for each object.
    private static float dissonanceRatioWeight = 0.5f; // weightfor effect  of dissonance on openness
    private static float opennessWeight = 0.1f; // maximum belief distance to consider with openness score of 1
    private final ArrayList<Node> confidenceSet; // in theory only values would be possible as well.
    private final float dissonanceDecay;
    private final float dissonanceDecrease; // in case of a positive interaction
    private final float dissonanceIncrease; // negative, in case of conflicting information (Not in use)
    // Moving window of contact history
    private final ArrayList<Integer> contactHistory;
    //Graphics
    private final float size = 30.0f;
    protected ArrayList<Node> neighbours;
    protected int id;
    // Tuning parameters
    private float belief; // agent's belief at current time
    private double currentDissonance = 0.0f;
    private float dissonanceThreshold; // dissonance becomes unbearable, agent engages in drastic measures: pruning network
    //personality traits
    private float openness; // how far another belief can be away from your's before being rejected
    private float neuroticism; // use neuroticism to inform resilience to dissonance
    private float extraversion; // use extraversion to define benefit of positive encounter and network size
    // original values for personality traits
    private float openness_original; // to change openness in case weight is changed.
    // Interaction statistics
    private int numberOfContacts;
    private int numberOfConflicts;


    public Node(int id) {
        this.id = id;
        neighbours = new ArrayList<>();
        contactHistory = new ArrayList<>();
        belief = random.nextFloat();
        openness = 0.05f + random.nextFloat() * 0.2f - 0.05f;
        confidenceSet = new ArrayList<>();
        dissonanceThreshold = random.nextFloat();
        dissonanceDecay = 0.5f;
        dissonanceDecrease = -0.05f;
        dissonanceIncrease = 0.3f;
        numberOfConflicts = 0;
        numberOfContacts = 0;
        extraversion = 1f; // penalty on network size for individual is inactive for purely simulated data
        reset();
    }

    public Node(int id, float neuroticism, float extraversion, float openness) {
        this(id);
        this.openness_original = openness;
        this.openness = opennessWeight * openness;
        this.dissonanceThreshold = 1f - neuroticism;
        this.currentDissonance = random.nextFloat() * dissonanceThreshold;
        this.extraversion = extraversion;
    }

    public static void setConnectionLimit(int connectionLimit) {
        Node.connectionLimit = connectionLimit;
    }

    public static void setOpennessWeight(float opennessWeight) {
        Node.opennessWeight = opennessWeight;
    }

    public static void setDissonanceRatioWeight(float dissonanceRatioWeight) {
        Node.dissonanceRatioWeight = dissonanceRatioWeight;
    }

    /**
     * Adds the passed in node as neighbour, if not already labelled as such
     * and number of existing neighbors is below the connection limit.
     *
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
     *
     * @param node the node chosen for comparison with this instance of Node.
     */

    public boolean canTwoConnect(Node node) {
        return ((neighbours.size() < getIndividualConnectionLimit()) &&
                (node.getNeighbours().size() < node.getIndividualConnectionLimit()));
    }

    /**
     * Removes the passed in node as neighbour
     *
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
     * <p>
     * Details can also be found in the AOI reader from 2019 by Dr. J. Borst.
     * <p>
     * We motivate this choice in our report.
     *
     * @param conflict whether or not the current conact was a conflict
     */

    /*public void updateDissonance(boolean conflict) {
        ++numberOfContacts;

        if (conflict) {
            ++numberOfConflicts;
            contactHistory.add(1);
        } else {
            contactHistory.add(0);
        }

        if (contactHistory.size() > windowSize) {
            contactHistory.remove(0);
        }

        // Moving average for window

        currentDissonance = (double) contactHistory.stream().mapToInt(i -> i).sum() / contactHistory.size();
    }*/

    public void updateContact(boolean conflict) {
        ++numberOfContacts;

        if (conflict) {
            ++numberOfConflicts;
            contactHistory.add(1);
        } else {
            contactHistory.add(0);
        }

        if (contactHistory.size() > windowSize) {
            contactHistory.remove(0);
        }
    }

    /**
     * Calculates boost to dissonance level in case of a positive encounter or pruning after negative encounter.
     */

    public void boostDissonance() {
        currentDissonance += (extraversion * dissonanceDecrease);
        currentDissonance = (currentDissonance < 0) ? 0f : currentDissonance;
    }

    /**
     * Receive messages from all Neighbors, add those in reccomended, update belief and dissonance.
     *
     * @param recommended Set of recommended nodes chosen by algorithm implemented in GraphModel
     */

    public void receiveMessages(ArrayList<Node> recommended) {
        ArrayList<Node> possibleConnections = new ArrayList<>(neighbours);
        ArrayList<Node> causedConflict = new ArrayList<>();
        possibleConnections.addAll(recommended);

        for (Node n : possibleConnections) {
            if (Math.abs(n.belief - belief) < getWeightedOpenness()) {
                confidenceSet.add(n);
                addNeighbour(n); // update if other agent was in reccomended
                updateContact(false);
                //updateDissonance(false);
            } else {
                updateContact(true);
                //updateDissonance(true);
                if (currentDissonance >= dissonanceThreshold) {
                    causedConflict.add(n);
                }
            }
        }

        for (Node n : causedConflict) {

            float numDrawn = random.nextFloat();

            numDrawn *= 0.99;   // As the max difference is 0.99. Min weighted openness is 0.01, so
                                // max difference is being of belief 0, encountering a 1, with openness 0.01

            float outsideAcceptableRange = Math.abs(n.belief - belief) - getWeightedOpenness();

            if (outsideAcceptableRange < numDrawn) {
                removeNeighbour(n);
                boostDissonance(); // reduction strategy has minimal (still linear) immediate effect (currently).
            }
        }

        currentDissonance = (double) contactHistory.stream().mapToInt(i -> i).sum() / contactHistory.size();
        updateBelief();
    }

    /**
     * Update belief of agent.
     */

    public void updateBelief() {
        float weight = 1.0f / (confidenceSet.size() + 1);
        float nextBelief = weight * belief;
        for (Node n : confidenceSet) {
            nextBelief += weight * n.belief;
        }
        belief = nextBelief;

    }

    /*
      Getters
     */

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
                if (Math.abs(n2.belief - belief) < getWeightedOpenness()) {
                    possibleConnections.add(n2);
                    /*
                    If I hear what I want to hear that makes me feel
                    good (initially only, mere exposure!!). This boost
                    depends on agent's level of extraversion.
                    */
                    boostDissonance();
                }
            }
        }

        for (Node n : possibleConnections) {
            addNeighbour(n);
        }
    }

    /**
     * Returns whether or not a given position (in world coordinates) falls within a node
     *
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
            return Math.pow(x - position.getX(), 2) + Math.pow(y - position.getY(), 2) < radius * radius;
        }
        return false;
    }

    //utility functions
    private boolean isInBetween(double lower, double upper, double number) {
        return (number >= lower && number < upper);
    }

    /**
     * Determines the color of the node based on belief.
     *
     * @return the color of the node
     */
    public Color getColorBelief(Color zeroBelief, Color oneBelief, boolean transparent) {

        float beliefDeviation = Math.abs(0.5f - belief) * 2;

        int beliefOverHalf = (belief > 0.5 ? 1 : 0);
        int beliefUnderHalf = (belief < 0.5 ? 1 : 0);

        int r = (int) (beliefOverHalf * beliefDeviation * oneBelief.getRed() +
                beliefUnderHalf * beliefDeviation * zeroBelief.getRed()
        );

        int g = (int) (beliefOverHalf * beliefDeviation * oneBelief.getGreen() +
                beliefUnderHalf * beliefDeviation * zeroBelief.getGreen()
        );

        int b = (int) (beliefOverHalf * beliefDeviation * oneBelief.getBlue() +
                beliefUnderHalf * beliefDeviation * zeroBelief.getBlue()
        );


        //int r = (int)((belief > 0.5 ? 1 : 0) * 255 * beliefDeviation);
        //int g = 0;
        //int b = (int)((belief < 0.5 ? 1 : 0) * 255 * beliefDeviation);
        int a = 127 + ((transparent ? 0 : 1) * 128);

        return new Color(r, g, b, a);
    }

    /**
     * Determines the color of the node based on dissonance
     *
     * @return the color of the node
     */
    public Color getColorDissonance(Color distressColour, Color normalColor, boolean transparent) {
        int a = 127 + ((transparent ? 0 : 1) * 128);

        if (isDissonanceOverThreshold()) {
            return new Color(distressColour.getRed(), distressColour.getGreen(), distressColour.getBlue(), a);
        } else {
            return new Color(normalColor.getRed(), normalColor.getGreen(), normalColor.getBlue(), a);
        }
    }

    public void reset() {
        confidenceSet.clear();
    }

    public int getConnectionCount() {
        return neighbours.size();
    }

    public float getBelief() {
        return belief;
    }

    public double getCurrentDissonance() {
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
        return neighbours.size() < getIndividualConnectionLimit();
    }

    public int getIndividualConnectionLimit() {
        return (int) (extraversion * connectionLimit);
    }

    public float getWeightedOpenness() {
        double ratio = currentDissonance / dissonanceThreshold;
        ratio = (ratio > 1 ? 1 : ratio);
        float weightedOpenness = openness - (float) (dissonanceRatioWeight * ratio);
        return (Math.max(weightedOpenness, 0.01f));
    }

    public float getDissonanceDecay() {
        return dissonanceDecay;
    }

    public float getOpenness() {
        return openness;
    }

    public float getExtraversion() {
        return extraversion;
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

    public void setReweightedOpenness() {
        openness = opennessWeight * openness_original;
    }

    @Override
    public String toString() {
        return "Node:                     " + id + "\n" +
                "Current Belief:        " + belief + "\n" +
                "Openness margin:  " + openness + "\n" +
                "Current dis:            " + (float) currentDissonance + "\n" +
                "Max. dis:                " + dissonanceThreshold + "\n" +
                "Num. neighbours:  " + neighbours.size() + "/" + getIndividualConnectionLimit() + "\n" +
                "Num. conflicts: " + getNumberOfConflicts() + "\n";
    }
}