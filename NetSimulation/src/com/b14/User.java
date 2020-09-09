 package com.b14;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class User extends Node {

    private float bias;
    private float skepticism;
    private float maxDisagreement;
    private boolean hasShared;

    private int numberOfTimesExposed;   // How often a connection shared the message

    private int peerPressureThreshold;
    private float peerPressureDamage;

    private int dissidentPruningThreshold;



    private Node lastMessageSender;

    public User(int entityID) {
        super(entityID);
        Random random = new Random();
        bias = random.nextFloat() * 2 - 1;
        skepticism = random.nextFloat();
        maxDisagreement = random.nextFloat();
        peerPressureThreshold = random.nextInt(4);
        peerPressureDamage = random.nextFloat();
        dissidentPruningThreshold = random.nextInt(4);
        reset();
    }

    public User(int entityID, float bias, float skepticism, float maxDisagreement, int peerPressureThreshold, float peerPressureDamage,
                int dissidentPruningThreshold) {

        super(entityID);
        this.bias = bias;
        this.skepticism = skepticism;
        this.maxDisagreement = maxDisagreement;
        this.peerPressureThreshold = peerPressureThreshold;
        this.peerPressureDamage = peerPressureDamage;
        this.dissidentPruningThreshold = dissidentPruningThreshold;
        reset();
    }

    /*
        if it has a message, it tests to see whether the message meets the standard for sharing
        The message then is sent to each other agent if it passes the test.
        The list of receiving nodes (the neighbours) are returned.

     */
    public List<Node> shareMessage() {
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
            List<Node> outgoing = new ArrayList<>();
            outgoing.addAll(neighbours);
            outgoing.remove(lastMessageSender);
            hasShared = true;

            return outgoing;
        } else {
            return null;
        }
    }

    public List<User> pruneDissidents() {
        if (!hasShared) {
            return null;
        }

        int numSharers = 0;
        List<User> dissidents = new ArrayList<>();

        for (Node n : neighbours) {
            if (n instanceof User) {
                User u = (User)n;

                if (u.getHasShared()) {
                    numSharers++;
                } else {
                    dissidents.add(u);
                }
            }
        }

        if (numSharers >= dissidentPruningThreshold) {

            for (User u : dissidents) {
                u.removeNeighbour(this);
                neighbours.remove(u);
            }

            return dissidents;

        }
            return null;
    }

    public void removeNeighbour(Node neighbour) {
        if (neighbours.contains(neighbour)) {
            neighbours.remove(neighbour);
        }
    }

    @Override
    public boolean receiveMessage(Node sender, Message message) {
        if (lastMessage == null) {
            lastMessageSender = sender;
            lastMessage = message;
            return true;
        }

        return false;
    }

    @Override
    public void reset() {
        super.reset();
        lastMessageSender = null;
        hasShared = false;
        numberOfTimesExposed = 0;
    }

    // Getters
    public boolean getHasShared() {
        return hasShared;
    }
}
