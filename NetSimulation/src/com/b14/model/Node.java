package com.b14.model;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Random;



/**
 *  The node in the network, aka the representation of a user
 */

public class Node extends Physics2DObject {

    protected ArrayList<Node> neighbours;
    protected int id;
    private float belief;
    private float error;
    private float[] dissonance = {0.0f,0.0f};
    private ArrayList<Node> confidenceSet; // in theory only values would be possible as well.
    private int connectionLimit;
    private final static Random random = new Random(0);



    public Node(int id) {
        this.id = id;
        neighbours = new ArrayList<>();
        belief = random.nextFloat();
        error = 0.1f + random.nextFloat()*0.5f-0.1f;
        confidenceSet = new ArrayList<>();
        dissonance[1] = 0.5f + random.nextFloat()*1.5f-0.5f; // treshold for dissonance
        connectionLimit = 15;
        reset();
    }

    /**
     * Adds the passed in node as neighbour, if not already labelled as such
     * @param node The neighbour to add.
     */

    public void addNeighbour(Node node) {
        if (!neighbours.contains(node)) {
            neighbours.add(node);
            node.getNeighbours().add(this);
        }
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

    public void updateDissonance(float value){
        dissonance[0] =  dissonance[0] + value;
    }

    /**
     * Receive messages from all Neighbors, add those in reccomended, update belief and dissonance.
     * @param recommended Set of recommended nodes chosen by algorithm implemented in GraphModel
    */

    public void receiveMessages(ArrayList<Node> recommended) {
        ArrayList<Node> possibleConnections = neighbours;
        ArrayList<Node> prunedConnections = new ArrayList<>();
        possibleConnections.addAll(recommended);
        for (Node n : possibleConnections) {

            if(Math.abs(n.belief - belief) < error) {
                confidenceSet.add(n);
                addNeighbour(n); // update if other agent was in reccomended
            } else {
                updateDissonance(0.3f);
                if (dissonance[0] > dissonance[1]) {
                    prunedConnections.add(n);
                    
                }
            }
        }
        for (Node n : prunedConnections) {
            removeNeighbour(n);
            updateDissonance(-0.1f); // reduction strategy has minimal immediate effect (currently).
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
                if(Math.abs(n2.belief - belief) < error) {
                    possibleConnections.add(n2);
                }
            }
        }

        for (Node n : possibleConnections) {
            addNeighbour(n);
        }
    }

    /**
     * Getters 
     */

    public void reset() {
        confidenceSet.clear();
    }

    public int getConnectionLimit () {
        return connectionLimit;
    }

    public float getBelief () {
        return belief;
    }

    public float[] getDissonance () {
        return dissonance;
    }

    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }

    public int getId() {
        return id;
    }

}
