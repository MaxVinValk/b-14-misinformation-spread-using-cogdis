package com.b14.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 *  Functionality for the entire network is stored here.
 */

public abstract class GraphModel {

    protected int nextFreeID;

    ArrayList<Node> nodes; 
    private final Random random = new Random(0);

    public GraphModel() {
        nodes = new ArrayList<>();
        nextFreeID = 0;
    }

    /**
     * Start simulation
     */
    public abstract void startRandom(int numNodes);

    /**
     * Connects the nodes to each other in a proportionate fashion, where nodes with more connections
     * are more likely to receive new connections.
     */
    protected void connectProportionate() {
        int numNodes = nodes.size();

        ArrayList<Node> unassigned = new ArrayList<>();
        unassigned.addAll(nodes);

        Node outgoing = unassigned.get(0);
        Node ingoing = unassigned.get(1);

        outgoing.addNeighbour(ingoing);
        unassigned.remove(outgoing);
        unassigned.remove(ingoing);

        int totalConnections = 2;

        for (Node unconnected : unassigned) {
            int unconnectedIdx = nodes.indexOf(unconnected);

            int selected = random.nextInt(totalConnections);
            int selectedIdx = 0;

            for (int i = 0; i < numNodes; i++) {
                selected -= nodes.get(i).getNeighbours().size();

                if (selected < 0) {
                    selectedIdx = i;
                    break;
                }
            }

            Node newNeighbour = nodes.get(selectedIdx);
            newNeighbour.addNeighbour(unconnected);

            totalConnections += 2;
        }
    }

    /**
     * Finds nodes with 1 neighbour (dead ends) and connects them together
     */
    protected void createLoops() {
        ArrayList<Node> onlyOneConnection = new ArrayList<>();

        for (Node n : nodes) {
            if (n.getNeighbours().size() == 1) {
                onlyOneConnection.add(n);
            }
        }

        while (onlyOneConnection.size() >= 2) {
            Node firstToConnect = onlyOneConnection.get(random.nextInt(onlyOneConnection.size()));
            Node secondToConnect = null;

            do {
                secondToConnect = onlyOneConnection.get(random.nextInt(onlyOneConnection.size()));
            } while (firstToConnect == secondToConnect);

            firstToConnect.addNeighbour(secondToConnect);

            onlyOneConnection.remove(firstToConnect);
            onlyOneConnection.remove(secondToConnect);
        }
    }

    /**
     * Performs the pruning action on each node
     */
    public abstract void pruneDissidents();

    /**
     * Performs the fraternize action on each node
     */
    public abstract void fraternize();

    /**
     * Perform initial first step (what happens differs for each model)
     */
    public abstract void initSimulateSpread(Message Message);

    /**
     * Perform additional steps.
     */
    public abstract boolean simulateSpreadStep();

    
    /**
     * Adds a node to the node at index idx
     * @param idx the idx of the node to which we append
     */
    public void addNodeAt(int idx) {
        Node old = nodes.get(idx);
        NodeOrig newNode = new NodeOrig(nextFreeID++);
        nodes.add(newNode);

        old.addNeighbour(newNode);

        Vector2D pos = old.getPosition().getCopy();
        pos.add(150, 150);
        System.out.println(pos);

        newNode.setPosition(pos);
    }

    /**
     * Sets the x, y coordinates of all nodes in an initial configuration.
     */
    public void nodeSpacingSetup() {

        nodes.get(0).setPosition(400, 400);

        ArrayList<Node> processedNodes = new ArrayList<>();
        Queue<Node> nodesToProcess = new LinkedList<>();

        nodesToProcess.add(nodes.get(0));

        double linkDistance = 150;

        while (!nodesToProcess.isEmpty()) {
            Node currentNode = nodesToProcess.remove();

            double currentX = currentNode.getX();
            double currentY = currentNode.getY();

            ArrayList<Node> neighbours = currentNode.getNeighbours();

            double angle = (2*Math.PI) / neighbours.size();

            for (int i = 0; i < neighbours.size(); i++) {

                Node currentNeighbour = neighbours.get(i);

                if (processedNodes.contains(currentNeighbour)) {
                    continue;
                }

                //We add random noise to prevent overlapping... Overlapping does weird things to the system.
                //Perhaps should be made more robust to prevent issues with them still overlapping regardless of
                //the noise
                double desiredXPos = currentX + linkDistance * Math.cos(angle * i) + (random.nextFloat()*10);
                double desiredYPos = currentY + linkDistance * Math.sin(angle * i) + (random.nextFloat()*10);

                currentNeighbour.setPosition(desiredXPos, desiredYPos);
                nodesToProcess.add(currentNeighbour);
            }

            processedNodes.add(currentNode);
        }
    }

    //Very expensive: Consider performing local updates

    /**
     * Forces nodes to space out: Edges function as springs,
     * and all nodes that are close to one another enact a force towards each other, to force them to space apart.
     */
    public void physicsUpdate() {

        //For stable behaviour, pushRange < springLength
        double pushRange = 90.0f;
        double pushConstant = 0.1f;

        double springLength = 110.0f;
        double springConstant = 0.005f;

        Vector2D center = new Vector2D(512, 384);
        double centerForce = -0.25f;

        for (int i = 0; i < nodes.size(); i++) {

            Node currentNode = nodes.get(i);

            // First, the force that pushes them all away from one another for the sake of spacing
            //It is like springs, but then they only enforce a minimum distance. So they only push away
            ArrayList<Node> tooClose = new ArrayList<>();

            for (int j = 0; j < nodes.size(); j++) {
                if (i == j) {
                    continue;
                }

                Node otherNode = nodes.get(j);
                if (currentNode.getDistance(otherNode) < pushRange) {
                    tooClose.add(otherNode);
                }
            }

            // Here we apply the forces to the nodes that are too close
            for (Node n : tooClose) {
                double actualDistance = currentNode.getDistance(n);
                double pushForce = pushConstant * (pushRange - actualDistance);
                Vector2D v = new Vector2D(n.getPosition(), currentNode.getPosition());
                v.setToUnitVector();
                v.multiplyWith(pushForce);

                currentNode.addAcceleration(v);
            }

            //And here for spring-force enacted by neighbours
            for (Node n : currentNode.getNeighbours()) {

                double actualDistance = currentNode.getDistance(n);
                double forceExperienced = springConstant * (springLength - actualDistance);

                //Get a vector from this node pointing to the other anchor-point
                Vector2D v = new Vector2D(n.getPosition(), currentNode.getPosition());

                v.setToUnitVector();

                v.multiplyWith(forceExperienced);

                currentNode.addAcceleration(v);

            }


        }

        //Remove pull and push from the first node. This causes more stable behaviour, as one node is not
        //bouncing around.
        nodes.get(0).setAcceleration(0, 0);

        for (Node n : nodes) {

            //Finally apply the central pulling force:
            Vector2D v = new Vector2D(center, n.getPosition());
            v.setToUnitVector();
            v.multiplyWith(centerForce);

            //n.addAcceleration(v);

            n.dampen();
            n.transferForce();
        }
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }
}

