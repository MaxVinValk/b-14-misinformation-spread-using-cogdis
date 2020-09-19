package com.b14.model;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Random;



/**
 *  The node in the network, aka the representation of a user
 */

public abstract class Node extends Physics2DObject {

    protected ArrayList<Node> neighbours;
    // New Stuff
    protected int id;



    public Node(int id) {
        this.id = id;
        neighbours = new ArrayList<>();
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
     * A friend of a friend can be a friend. 
     */
    public abstract void fraternize();

    /**
     * Necessary for drawing. 
     */
    public abstract boolean getHasShared();


    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }

    public int getId() {
        return id;
    }

}
