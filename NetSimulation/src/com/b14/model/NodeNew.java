package com.b14.model;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Random;



/**
 *  The node in the network, aka the representation of a user
 */

public class NodeNew extends Node {

    
    private float belief;
    private float error;
    private float[] dissonance = {0.0f,0.0f};
    private ArrayList<Node> confidenceSet; // in theory only values would be possible as well.
    private int connectionLimit;
    private final static Random random = new Random(0);


    public NodeNew(int id) {
        super(id);
        
        belief = random.nextFloat();
        error = random.nextFloat()*0.5f-0.01f;
        confidenceSet = new ArrayList<>();
        dissonance[1] = random.nextFloat()*1.5f-0.5f; // treshold for dissonance
        connectionLimit = 15;
        reset();
    }

    
    /**
     * Update Dissonance using a super simple example function.
     */

    public void updateDissonance(float value){
        dissonance[0] =  dissonance[0] + value;
    }

    /**
     * Receive messages from all Neighbors, add those in reccomended, update belief and dissonance.
    */

    public void receiveMessages(ArrayList<Node> recommended) {
        ArrayList<Node> possibleConnections = neighbours;
        ArrayList<Node> prunedConnections = new ArrayList<>();
        possibleConnections.addAll(recommended);
        for (Node n : possibleConnections) {

            if(Math.abs(((NodeNew) n).belief - belief) < error) {
                confidenceSet.add(n);
                addNeighbour(n); // update if other agent was in reccomended
            } else {
                updateDissonance(0.05f);
                if (dissonance[0] > dissonance[1]) {
                    prunedConnections.add(n);
                    
                }
            }
        }
        for (Node n : prunedConnections) {
            removeNeighbour(n);
            updateDissonance(-0.05f); // reduction strategy has minimal immediate effect (currently).
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
            nextBelief += weight * ((NodeNew) n).belief;
        }
        belief = nextBelief;

    }

    /**
     * Basically fraternize by Max.
    */

    public void fraternize() {
        for (Node n : neighbours) {
            NodeNew oN = (NodeNew) n;
            for (Node n2 : n.getNeighbours()) {
                NodeNew oN2 = (NodeNew) n2;
                if (oN2 == this || oN == oN2) {
                    continue;
                }

                if(Math.abs(oN2.belief - belief) < error) {
                    addNeighbour(n2);
                }
            }
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

    /**
     * Necessary for drawing. 
     */
    public boolean getHasShared() {
        return (belief > 0.5 ? true : false);
    }

}
