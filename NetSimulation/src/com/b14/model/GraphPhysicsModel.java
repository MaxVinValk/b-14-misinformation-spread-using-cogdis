package com.b14.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * A class that is responsible for all physics updates to a network of nodes.
 */

public class GraphPhysicsModel {

    protected final Random random = new Random(0);
    //For stable behaviour, pushRange < springLength
    private final double PUSH_RANGE = 90.0f;
    private final double PUSH_CONSTANT = 0.1f;
    private final double SPRING_LENGTH = 110.0f;
    private final double SPRING_CONSTANT = 0.005f;
    //Gravitational pull to a point
    private final Vector2D CENTER = new Vector2D(512, 384);
    protected int nextFreeID;
    protected ArrayList<Node> nodes;
    protected PropertyChangeSupport pcs;
    private double centerForce = 0.5f;


    /**
     * Creates a new GraphPhysicsModel
     */
    public GraphPhysicsModel() {
        nodes = new ArrayList<>();

        pcs = new PropertyChangeSupport(this);
    }

    //Very expensive: Consider performing local updates

    /**
     * Forces nodes to space out: Edges function as springs,
     * and all nodes that are close to one another enact a force towards each other, to force them to space apart.
     */
    public double physicsUpdate() {
        for (int i = 0; i < nodes.size(); i++) {
            applyPushForce(i);
            applySpringForce(i);
        }

        applyGravity();
        double avgVelocity = transferForces();

        pcs.firePropertyChange(new PropertyChangeEvent(this, "physicsUpdate", null, avgVelocity));

        return avgVelocity;
    }

    /**
     * Setup a simple simulation.
     */

    protected void startRandom(int numNodes) {
        createNodes(numNodes);

        setupNetworkStructure();
    }

    /**
     * Create Nodes.
     *
     * @param numNodes Number of nodes to be created for the network.
     */

    private void createNodes(int numNodes) {
        assert (numNodes > 5) : "Too few nodes defined in startRandom";
        nextFreeID = 0;
        nodes.clear();

        for (int i = 0; i < numNodes; i++) {
            nodes.add(new Node(nextFreeID++));
        }
    }

    protected void setupNetworkStructure() {
        connectProportionate();
        createLoops();
        nodeSpacingSetup();

        pcs.firePropertyChange(new PropertyChangeEvent(this, "modelChange", null, null));
    }

    /**
     * Connects the nodes to each other in a proportionate fashion, where nodes with more connections
     * are more likely to receive new connections.
     */
    private void connectProportionate() {
        int numNodes = nodes.size();

        ArrayList<Node> unassigned = new ArrayList<>(nodes);

        Node outgoing = unassigned.get(0);
        Node ingoing = unassigned.get(1);

        outgoing.addNeighbour(ingoing);
        unassigned.remove(outgoing);
        unassigned.remove(ingoing);

        int totalConnections = 2;

        for (Node unconnected : unassigned) {

            int selected = random.nextInt(totalConnections);
            int selectedIdx = 0;

            for (int i = 0; i < numNodes; i++) {
                int numConnections = nodes.get(i).getNeighbours().size();

                if (numConnections != nodes.get(i).getIndividualConnectionLimit()) {
                    selected -= nodes.get(i).getNeighbours().size();
                }

                if (selected < 0) {
                    selectedIdx = i;
                    break;
                }
            }

            Node newNeighbour = nodes.get(selectedIdx);
            newNeighbour.addNeighbour(unconnected);

            totalConnections += 2;

            if (newNeighbour.getNeighbours().size() == newNeighbour.getIndividualConnectionLimit()) {
                totalConnections -= newNeighbour.getIndividualConnectionLimit();
            }

        }
    }

    /**
     * Finds nodes with 1 neighbour (dead ends) and connects them together
     */
    private void createLoops() {
        ArrayList<Node> onlyOneConnection = new ArrayList<>();

        for (Node n : nodes) {
            if (n.getNeighbours().size() == 1 && n.getIndividualConnectionLimit() > 1) {
                onlyOneConnection.add(n);
            }
        }

        while (onlyOneConnection.size() >= 2) {
            Node firstToConnect = onlyOneConnection.get(random.nextInt(onlyOneConnection.size()));
            Node secondToConnect;

            do {
                secondToConnect = onlyOneConnection.get(random.nextInt(onlyOneConnection.size()));
            } while (firstToConnect == secondToConnect);

            firstToConnect.addNeighbour(secondToConnect);

            onlyOneConnection.remove(firstToConnect);
            onlyOneConnection.remove(secondToConnect);
        }
    }

    /**
     * Sets the x, y coordinates of all nodes in an initial configuration.
     */
    private void nodeSpacingSetup() {

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

            double angle = (2 * Math.PI) / neighbours.size();

            for (int i = 0; i < neighbours.size(); i++) {

                Node currentNeighbour = neighbours.get(i);

                if (processedNodes.contains(currentNeighbour)) {
                    continue;
                }

                double desiredXPos = currentX + linkDistance * Math.cos(angle * i) + (random.nextFloat() * 10);
                double desiredYPos = currentY + linkDistance * Math.sin(angle * i) + (random.nextFloat() * 10);

                currentNeighbour.setPosition(desiredXPos, desiredYPos);
                nodesToProcess.add(currentNeighbour);
            }

            processedNodes.add(currentNode);
        }

        findPositionForUnlinkedNodes();

    }

    /**
     * Places all nodes that have not been assigned with an initial position by nodeSpacingSetup
     * in such a manner that the physics doesn't go haywire
     */

    private void findPositionForUnlinkedNodes() {

        int spiralPosition = 500;
        int spiralStepSize = 25;
        int x = 0;
        int y = 0;

        for (Node n : nodes) {
            Vector2D pos = n.getPosition();

            if (pos.getX() == 0 && pos.getY() == 0) {

                while (getNodeOnPoint(x, y) != null) {
                    spiralPosition += spiralStepSize;
                    x = (int) (0.25 * spiralPosition * Math.cos(spiralPosition));
                    y = (int) (0.25 * spiralPosition * Math.sin(spiralPosition));
                }

                n.setPosition(x, y);
                spiralPosition += spiralStepSize;
            }
        }
    }


    /**
     * Applies the push force to the given node of its surrounding nodes. Each node pushes on all others nearby,
     * to force nodes that are not connected with a link to space out themselves.
     *
     * @param nodeIdx The index of the node to which  the push force is applied
     */
    private void applyPushForce(int nodeIdx) {
        Node currentNode = nodes.get(nodeIdx);

        ArrayList<Node> tooClose = new ArrayList<>();

        for (int j = 0; j < nodes.size(); j++) {
            if (nodeIdx == j) {
                continue;
            }

            Node otherNode = nodes.get(j);
            if (currentNode.getDistance(otherNode) < PUSH_RANGE) {
                tooClose.add(otherNode);
            }
        }

        for (Node n : tooClose) {
            double actualDistance = currentNode.getDistance(n);
            double pushForce = PUSH_CONSTANT * (PUSH_RANGE - actualDistance);
            Vector2D v = new Vector2D(n.getPosition(), currentNode.getPosition());
            v.setToUnitVector();
            v.multiplyWith(pushForce);

            currentNode.addAcceleration(v);
        }
    }

    private void applySpringForce(int nodeIdx) {
        Node currentNode = nodes.get(nodeIdx);

        for (Node n : currentNode.getNeighbours()) {

            double actualDistance = currentNode.getDistance(n);
            double forceExperienced = SPRING_CONSTANT * (SPRING_LENGTH - actualDistance);

            Vector2D v = new Vector2D(n.getPosition(), currentNode.getPosition());

            v.setToUnitVector();
            v.multiplyWith(forceExperienced);
            currentNode.addAcceleration(v);

        }
    }

    private void applyGravity() {
        for (Node n : nodes) {
            Vector2D v = new Vector2D(n.getPosition(), CENTER);
            v.setToUnitVector();
            v.multiplyWith(centerForce);
            n.addAcceleration(v);
        }
    }

    /**
     * Transfers all accelerations to velocities, and returns average velocity at this step
     *
     * @return the average velocity
     */
    private double transferForces() {

        double avgVelocity = 0.0f;

        for (Node n : nodes) {
            n.dampen();
            n.transferForce();

            avgVelocity += n.getVelocity().getLength();
        }

        return avgVelocity / nodes.size();
    }

    /**
     * Finds the node that is on the indicated coordinates.
     *
     * @param x The x (world) coordinate to check
     * @param y The y (world) coordinate to check
     * @return The node if there is one on point (x, y), and NULL otherwise
     */

    public Node getNodeOnPoint(double x, double y) {
        for (Node n : nodes) {
            if (n.pointInNode(x, y)) {
                return n;
            }
        }

        return null;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public Node getNodeOnPoint(Vector2D pos) {
        return getNodeOnPoint(pos.getX(), pos.getY());
    }

    public double getCenterForce() {
        return centerForce;
    }

    public void setCenterForce(double centerForce) {
        this.centerForce = centerForce;
    }

    //Functions for propertyChangeListeners / support
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }
}
