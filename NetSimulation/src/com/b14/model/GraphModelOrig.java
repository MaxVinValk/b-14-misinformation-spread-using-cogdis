package com.b14.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 *  Functionality for the entire network is stored here.
 */

public class GraphModelOrig extends GraphModel{

 
    private final Random random = new Random(0);

    private ArrayList<Node> toBeUpdated;

    public GraphModelOrig() {
        toBeUpdated = new ArrayList<>();
    }


    public void startRandom(int numNodes) {
        createNodes(numNodes);
        connectProportionate();
        createLoops();

        nodeSpacingSetup();
    }

    // Node creation

    public void createNodes(int numNodes) {
        assert(numNodes > 2) : "Too few nodes defined in startRandom";
        nextFreeID = 0;
        nodes.clear();

        for (int i = 0; i < numNodes; i++) {
            nodes.add(new NodeOrig(nextFreeID++));
        }
    }

    /**
     * Starts the spread simulation with an initial message
     * @param message the message to be spread throughout the network
     */
    public void initSimulateSpread(Message message) {
        toBeUpdated .clear();

        for (Node n : nodes) {
            ((NodeOrig) n).reset();
        }

        //TODO: Allow for specification of this number
        int numInitialSpreaders = 5;

        ArrayList<Node> initialSpreaders = new ArrayList<>();

        while (numInitialSpreaders-- > 0) {
            Node n = null;
            do {
                n = nodes.get(random.nextInt(nodes.size()));
            } while (initialSpreaders.contains(n));
            initialSpreaders.add(n);
        }

        for (Node n : initialSpreaders) {
            ArrayList<Node> recipients = ((NodeOrig) n).broadcastIntoNetwork(message);
            if (recipients != null) {
                toBeUpdated.addAll(recipients);
            }
        }
    }

    /**
     *  Performs 1 spreading step, assuming the spread has been initialized
     */
    public boolean simulateSpreadStep() {

        ArrayList<Node> nextToBeUpdated = new ArrayList<>();

        for (Node n : toBeUpdated) {
            ArrayList<Node> recipients = ((NodeOrig) n).shareMessage();

            if (recipients != null) {
                nextToBeUpdated.addAll(recipients);
            }
        }

        toBeUpdated = new ArrayList<>(nextToBeUpdated);

        return toBeUpdated.size() > 0;
    }

    /**
     * Performs the pruning action on each node
     */
    public void pruneDissidents() {
        for (Node n : nodes) {
            ((NodeOrig) n).pruneDissidents();
        }
    }

    /**
     * Performs the fraternize action on each node
     */
    public void fraternize() {
        for (Node n : nodes) {
            ((NodeOrig) n).fraternize();
        }
    }
}

