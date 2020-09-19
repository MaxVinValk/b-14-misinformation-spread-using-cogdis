package com.b14.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 *  Functionality for the entire network is stored here.
 */

public class GraphModelNew extends GraphModel{

 
    private final Random random = new Random(0);

    private ArrayList<Node> recommended;

    public GraphModelNew() {
        recommended = new ArrayList<>();
    }

    /**
     *  Setup a simple simulation.
     */

    public void startRandom(int numNodes) {
        createNodes(numNodes);
        connectProportionate();
        createLoops();

        nodeSpacingSetup();
    }

    /**
     *  Create Nodes.
     */

    public void createNodes(int numNodes) {
        assert(numNodes > 5) : "Too few nodes defined in startRandom";
        nextFreeID = 0;
        nodes.clear();

        for (int i = 0; i < numNodes; i++) {
            nodes.add(new NodeNew(nextFreeID++));
        }
    }

    /**
     *  Recommendation set selection algorithm.
     * @param agent the agent for which to create reccomend set
     * @param size the size of the reccomendation set
     * @param alg the algorithm by which to select nodes.
     */

    void recommend(Node agent, int size, String alg) {
        recommended.clear();
        switch (alg) {
            case "random":
                while (recommended.size() < size) {
                    Node n = nodes.get(random.nextInt(nodes.size()));
                    if (n != agent) {
                        recommended.add(n);
                    }
                }
                break;
        
            default:
                System.out.println("No Algorithm selected.");
                break;
        }
    }

    /**
     *  Performs cleaning of confidence sets.
     */

    public void initSimulateSpread(Message Message) {

        for (Node n : nodes) {
            ((NodeNew) n).reset();
        }
    }

    /**
     *  Performs 1 spreading step for entire network.
     */
    public boolean simulateSpreadStep() {
        for (Node n : nodes) {
            recommend(n, 5, "random");
            ((NodeNew) n).receiveMessages(recommended);
            ((NodeNew) n).updateDissonance(-0.01f); // decay over time.
        }

        return false;
    }

    
    public void pruneDissidents() {
        /**
         * Performs nothing since pruning is handled in sim step.
         * Kept to prevent issues with rendering parts.
         */
    }

    /**
     * Performs the fraternize action on each node
     */
    public void fraternize() {
        for (Node n : nodes) {
            if(((NodeNew) n).getNeighbours().size() < ((NodeNew) n).getConnectionLimit()) {
                ((NodeNew) n).fraternize();
            }
            
        }
    }
}

