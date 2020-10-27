package com.b14.model.recommendationstrategies;

import com.b14.model.Node;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Random;

public class RecommendationStrategy {
    private static final Random random = new Random(0);

    public static ArrayList<Node> recommend(Strategy strategy, ArrayList<Node> nodes, Node current, int size) throws OperationNotSupportedException {
        switch (strategy) {
            case RANDOM:
                return recommendRandom(nodes, current, size);
            case POLARIZE:
                return recommendPolarize(nodes, current, size);
            case NEUTRALIZE:
                return recommendNeutralize(nodes, current, size);
            default:
                throw new OperationNotSupportedException();
        }
    }

    /**
     * Recommends random other nodes to the agent
     *
     * @param agent the agent for which the recommendation set is constructed
     * @param size  the maximum size of the recommended connections
     */
    private static ArrayList<Node> recommendRandom(ArrayList<Node> nodes, Node agent, int size) {
        ArrayList<Node> recommended = new ArrayList<>();

        while (recommended.size() < size) {
            Node n = nodes.get(random.nextInt(nodes.size()));
            if (n != agent) {
                recommended.add(n);
            }
        }

        return recommended;
    }

    /**
     * Recommends nodes to node agent that are similar to the agent in belief
     *
     * @param agent the agent for which the recommendation set is constructed
     * @param size  the maximum size of the recommended connections
     */

    private static ArrayList<Node> recommendPolarize(ArrayList<Node> nodes, Node agent, int size) {
        ArrayList<Node> recommended = new ArrayList<>();

        for (Node n : nodes) {
            if (n == agent) {
                continue;
            }
            if (recommended.size() > size) {
                break;
            }
            if ((Math.abs(n.getBelief() - agent.getBelief()) < agent.getWeightedOpenness()) &&
                    !agent.getNeighbours().contains(n) && agent.canTwoConnect(n)) {
                recommended.add(n);
            }
        }
        return recommended;
    }

    private static ArrayList<Node> recommendNeutralize(ArrayList<Node> nodes, Node agent, int size) {
        ArrayList<Node> recommended = new ArrayList<>();
        float maxDeviationDifference = 0.1f;

        float ownDeviation = Math.abs(0.5f - agent.getBelief());

        for (Node n : nodes) {
            if (n == agent) {
                continue;
            }
            if (recommended.size() > size) {
                break;
            }

            float distanceToNeutral = Math.abs(0.5f - n.getBelief());

            if (distanceToNeutral < ownDeviation && Math.abs(agent.getBelief() - n.getBelief()) < maxDeviationDifference) {
                recommended.add(n);
            }
        }

        return recommended;
    }

    public enum Strategy {
        RANDOM, POLARIZE, NEUTRALIZE
    }
}
