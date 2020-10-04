package com.b14.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 *  Functionality for the entire network is stored here.
 */

//TODO: Perhaps break up the physics simulation aspect of the model and the information aspect of the model?
/*
        Strategy proposed: Have a GraphModel that contains the physics, which governs a list of PhysicsNodes.
        Then extend this GraphModel with a InformationNetwork class, that then stores InformationNetworkNodes (which
        extend the PhysicsNodes). This way we can cleanly break up this class in the two distinct roles it plays.
 */

public class GraphModel extends GraphPhysicsModel {

    public enum RecommendationStrategy {
        RANDOM, POLARISE
    }

    private int epoch;

    private RecommendationStrategy rs;
    private int recommendationSize;

    private ArrayList<Node> recommended;

    private DataLogger dl;


    public GraphModel(DataLogger dl) {
        this.dl = dl;

        recommended = new ArrayList<>();

        rs = RecommendationStrategy.POLARISE;
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
     * Recommendation set selection algorithm.
     * @param agent the agent for which to create recommend set
     * @param size the size of the recommendation set
     * @param rs the algorithm by which to select nodes.
     */

    // ToDo: distinct functions per algorithm.

    void recommend(Node agent, int size, RecommendationStrategy rs) {
        recommended.clear();
        switch (rs) {
            case RANDOM:
                while (recommended.size() < size) {
                    Node n = nodes.get(random.nextInt(nodes.size()));
                    if (n != agent) {
                        recommended.add(n);
                    }
                }
                break;
            
            case POLARISE:
                // recommends only other agents the agent does not know that are close to the agents own belief.
                for (Node n : nodes) {
                    if(n == agent) {
                        continue;
                    }
                    if(recommended.size() > size) {
                        break;
                    }
                    if((Math.abs(n.getBelief() - agent.getBelief()) < agent.getOpenness()) &&
                        !agent.getNeighbours().contains(n)) {
                            recommended.add(n);
                    }
                }
                break;
        
            default:
                break;
        }
    }

    /**
     *  Performs 1 spreading step for entire network.
     */
    public void simulateSpreadStep() {
        for (Node n : nodes) {
            n.reset(); // clear confidence set
            recommend(n, recommendationSize, rs);
            n.receiveMessages(recommended);
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


    public int getEpoch(){
        return epoch;
    }

    /*
     * Setters
     */

    public void setRecommendationStrategy(RecommendationStrategy rs) {
        this.rs = rs;
    }

    public void setRecommendationSize(int recommendationSize) {
        this.recommendationSize = recommendationSize;
    }

    public void setConnectionLimitOnNodes(int newLimit) {
        Node.setConnectionLimit(newLimit);

        for(Node n : nodes) {

            while (n.getConnectionCount() > newLimit) {
                Node nodeToRemove = n.getNeighbours().get(random.nextInt(n.getConnectionCount()));
                n.removeNeighbour(nodeToRemove);
            }
        }
        pcs.firePropertyChange(new PropertyChangeEvent(this, "modelChange", null, null));
    }

}

