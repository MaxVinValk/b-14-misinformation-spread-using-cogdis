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

public class GraphModel {

    private int nextFreeID;
    private int epoch;
    private ArrayList<Node> nodes; 
    private ArrayList<Node> recommended;
    private final Random random = new Random(0);

    private DataLogger dl;

    private PropertyChangeSupport pcs;

    /*
        What follows are the parameters for the physics simulation
     */

    //For stable behaviour, pushRange < springLength
    private final double PUSH_RANGE         = 90.0f;
    private final double PUSH_CONSTANT      = 0.1f;

    private final double SPRING_LENGTH      = 110.0f;
    private final double SPRING_CONSTANT    = 0.005f;

    //Gravitational pull to a point
    private final Vector2D CENTER           = new Vector2D(512, 384);
    private final double CENTER_FORCE       = 0.5f;



    public GraphModel(DataLogger dl) {

        this.dl = dl;

        nodes = new ArrayList<>();
        recommended = new ArrayList<>();
        nextFreeID = 0;
        epoch = 0;

        pcs = new PropertyChangeSupport(this);
    }

    /**
     *  Setup a simple simulation.
     */

    public void startRandom(int numNodes) {
        epoch = 0;
        createNodes(numNodes);
        connectProportionate();
        createLoops();

        nodeSpacingSetup();

        dl.startNewSession();
        pcs.firePropertyChange(new PropertyChangeEvent(this, "modelChange", null, null));
    }

    /**
     *  Create Nodes.
     * @param numNodes Number of nodes to be created for the network.
     */

    public void createNodes(int numNodes) {
        assert(numNodes > 5) : "Too few nodes defined in startRandom";
        nextFreeID = 0;
        nodes.clear();

        for (int i = 0; i < numNodes; i++) {
            nodes.add(new Node(nextFreeID++));
        }
    }

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
     * Recommendation set selection algorithm.
     * @param agent the agent for which to create recommend set
     * @param size the size of the recommendation set
     * @param alg the algorithm by which to select nodes.
     */

    // ToDo: distinct functions per algorithm.

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
            
            case "polarize":
                // recommends only other agents the agent does not know that are close to the agents own belief.
                for (Node n : nodes) {
                    if(n == agent) {
                        continue;
                    }
                    if(recommended.size() > 5) {
                        break;
                    }
                    if((Math.abs(n.getBelief() - agent.getBelief()) < agent.getOpenness()) &&
                        !agent.getNeighbours().contains(n)) {
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
     *  Performs 1 spreading step for entire network.
     */
    public void simulateSpreadStep() {
        for (Node n : nodes) {
            n.reset(); // clear confidence set
            recommend(n, 20, "polarize");
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

    /**
     * Adds a node to the node at index idx
     * @param idx the idx of the node to which we append
     */
    public void addNodeAt(int idx) {
        Node old = nodes.get(idx);
        Node newNode = new Node(nextFreeID++);
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

        for (int i = 0; i < nodes.size(); i++) {
            applyPushForce(i);
            applySpringForce(i);
        }

        applyGravity();
        transferForces();

        pcs.firePropertyChange(new PropertyChangeEvent(this, "physicsUpdate", null, null));
    }

    private void applyPushForce(int nodeIdx) {
        Node currentNode = nodes.get(nodeIdx);

        // First, the force that pushes them all away from one another for the sake of spacing
        //It is like springs, but then they only enforce a minimum distance. So they only push away
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

        // Here we apply the forces to the nodes that are too close
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
        /**
         * Current issue: Exception in thread "main" java.lang.NullPointerException:
         * Cannot invoke "com.b14.model.Physics2DObject.getX()" because "other" is null
         */ 
        Node currentNode = nodes.get(nodeIdx);

        for (Node n : currentNode.getNeighbours()) {

            double actualDistance = currentNode.getDistance(n);
            double forceExperienced = SPRING_CONSTANT * (SPRING_LENGTH - actualDistance);

            //Get a vector from this node pointing to the other anchor-point
            Vector2D v = new Vector2D(n.getPosition(), currentNode.getPosition());

            v.setToUnitVector();
            v.multiplyWith(forceExperienced);
            currentNode.addAcceleration(v);

        }
    }

    private void applyGravity() {
        for (Node n : nodes) {
            //Finally apply the central pulling force:
            Vector2D v = new Vector2D(n.getPosition(), CENTER);
            v.setToUnitVector();
            v.multiplyWith(CENTER_FORCE);
            n.addAcceleration(v);
        }
    }

    private void transferForces() {
        for (Node n : nodes) {
            n.dampen();
            n.transferForce();
        }
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    /**
     * Finds the node that is on the indicated coordinates.
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

    public Node getNodeOnPoint(Vector2D pos) {
        return getNodeOnPoint(pos.getX(), pos.getY());
    }

    public int getEpoch(){
        return epoch;
    }

    //Functions for propertyChangeListeners / support

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }
}

