package com.b14.model;

import java.util.ArrayList;
import java.util.Random;

/**
 *  The node in the network, aka the representation of a user
 */

public class Node extends Physics2DObject {

    private ArrayList<Node> neighbours;
    private int id;

    private Message lastMessage = null;
    private Node lastMessageSender = null;

    private boolean hasShared;
    private int numberOfTimesExposed;

    private float bias;
    private float skepticism;
    private int peerPressureThreshold;
    private float peerPressureDamage;

    private int dissidentPruningThreshold;

    private float fraternizeThreshold;
    private float fraternizeWillingness;


    private final static Random random = new Random(0);


    public Node(int id) {
        this.id = id;
        neighbours = new ArrayList<>();

        bias = random.nextFloat()*2 - 1;
        skepticism = random.nextFloat();
        peerPressureThreshold = random.nextInt(4);
        peerPressureDamage = random.nextFloat();
        dissidentPruningThreshold = random.nextInt(4);
        fraternizeThreshold = random.nextFloat();
        fraternizeWillingness = random.nextFloat()/10;

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
     * Broadcasts a message to the network. This function should be used on the nodes that serve as 'initial spreaders'
     * of information into the network. Akin to making a new post on social media.
     * @param message The message that is shared
     * @return
     */

    public ArrayList<Node> broadcastIntoNetwork(Message message) {
        lastMessage = message;
        hasShared = true;
        sendMessage();

        return neighbours;
    }

    /**
     * Simply sends the message it is holding onto to all neighbours
     */
    public void sendMessage() {
        assert(lastMessage != null) : "Node attempted a send without a message";

        for (Node neighbour : neighbours) {
            neighbour.receiveMessage(this, lastMessage);
        }
    }

    /**
     * If convinced, this node will share the message it has. If it has no message or already shared, nothing happens
     * @return the list of recipients (if shared) or null otherwise
     */
    public ArrayList<Node> shareMessage() {
        if (lastMessage == null || hasShared) {
            return null;
        }

        numberOfTimesExposed += 1;

        float sustainedDamage = 0;

        if (numberOfTimesExposed >= peerPressureThreshold) {
            sustainedDamage = peerPressureDamage;
        }

        if (lastMessage.getInformationQuality() >= skepticism - sustainedDamage) {
            sendMessage();
            ArrayList<Node> outgoing = new ArrayList<>();

            outgoing.addAll(neighbours);
            outgoing.remove(lastMessageSender); // We do not need to send back the node that we spread from to begin with
            hasShared = true;

            return outgoing;
        }

        return null;
    }

    /**
     * Removes those that didn't share if this user's bias threshold has been reached.
     * @return
     */

    public ArrayList<Node> pruneDissidents() {
        if (!hasShared) {
            return null;
        }

        int numSharers = 0;
        ArrayList<Node> dissidents = new ArrayList<>();

        for (Node n : neighbours) {
            if (n.getHasShared()) {
                numSharers++;
            } else {
                dissidents.add(n);
            }
        }

        if (numSharers >= dissidentPruningThreshold) {
            for (Node n : dissidents) {
                removeNeighbour(n);
            }

            return dissidents;
        }

        return null;
    }

    /**
     * A friend from a friend that I agree with is a friend
     */

    public void fraternize() {
        ArrayList<Node> candidateFriends = new ArrayList<>();

        for (Node n : neighbours) {
            for (Node n2 : n.getNeighbours()) {

                if (n2 == this || n == n2) {
                    continue;
                }

                if (n2.hasShared == hasShared && Math.abs(n2.getSkepticism() - skepticism) < fraternizeThreshold) {
                    candidateFriends.add(n2);
                }
            }
        }

        for (Node candidate : candidateFriends) {
            if (random.nextFloat() < fraternizeWillingness) {
                addNeighbour(candidate);
                break; // Only fraternize once per message cycle
            }
        }
    }


    /**
     * Other nodes call this function on this node if they share their message. This stores the message
     * in the node such that it has 'seen' the post on social media.
     * @param sender    The sender. This is stored such that messages are never sent back and forth
     * @param message   The message
     */
    public void receiveMessage(Node sender, Message message) {
        if (lastMessage == null) {
            lastMessageSender = sender;
            lastMessage = message;
        }
    }

    /**
     * Resets flags such that spreading is possible again.
     */

    public void reset() {
        lastMessage = null;
        lastMessageSender = null;

        hasShared = false;
        numberOfTimesExposed = 0;
    }

    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }

    public int getId() {
        return id;
    }

    public boolean getHasShared() {
        return hasShared;
    }

    public float getSkepticism() {
        return skepticism;
    }

}
