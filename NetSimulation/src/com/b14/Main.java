package com.b14;


import java.rmi.server.ExportException;

public class Main {
    static final String VERSION = "Version 0.1";

    public static void main(String[] args) {
        Network n = new Network(25, 5);
        n.simulateSpread(new Message(0.0f, 0.5f));

    }
}
