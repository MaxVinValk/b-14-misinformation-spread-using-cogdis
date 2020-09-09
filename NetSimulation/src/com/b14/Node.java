package com.b14;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {

    protected List<Node> neighbours;
    protected Message lastMessage;
    private int entityID;

    public Node(int entityID) {
        neighbours = new ArrayList<>();
        lastMessage = null;
        this.entityID = entityID;
    }

    public void addNeighbour(Node neighbour) {
        neighbours.add(neighbour);
    }

    public void sendMessage() {

        assert (lastMessage != null) : "Node attempted a send without a message";

        for (Node neighbour : neighbours) {
            neighbour.receiveMessage(this, lastMessage);
        }

    }

    public void reset() {
        lastMessage = null;
    }

    //getters
    public List<Node> getNeighbours() {
        return neighbours;
    }

    public int getEntityID() {
        return entityID;
    }

    public abstract boolean receiveMessage(Node sender, Message message);

}
