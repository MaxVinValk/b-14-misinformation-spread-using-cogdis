package com.b14;

import java.util.List;
import java.util.Random;

public class Source extends Node {

    private float bias;
    private float credibility;

    public Source(int entityID) {
        super(entityID);
        Random random = new Random();
        bias = random.nextFloat()*2 - 1;
        credibility = random.nextFloat();
    }



    public Source(int entityID, float bias, float credibility) {
        super(entityID);
        this.bias = bias;
        this.credibility = credibility;
    }

    //returns a list of the receivers of the broadcast
    public List<Node> broadcast(Message message) {
        lastMessage = message;
        this.sendMessage();

        return neighbours;
    }

    @Override
    public boolean receiveMessage(Node sender, Message message) {
        return false;
    }

}
