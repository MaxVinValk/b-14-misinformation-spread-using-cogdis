package com.b14;

public class Message {

    private float bias;
    private float informationQuality;

    public Message(float bias, float informationQuality) {
        this.bias = bias;
        this.informationQuality = informationQuality;
    }

    public float getBias() {
        return bias;
    }

    public float getInformationQuality() {
        return  informationQuality;
    }


}
