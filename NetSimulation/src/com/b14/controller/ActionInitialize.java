package com.b14.controller;

import com.b14.model.GraphModel;
import com.b14.model.Message;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ActionInitialize extends AbstractAction {

    private final GraphModel model;

    public ActionInitialize(GraphModel model) {
        super("Reset model");
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        int numNodes;

        do {
            try {
                numNodes = Integer.parseInt(JOptionPane.showInputDialog("Number of nodes (>=5)?"));
            } catch (NumberFormatException e) {
                numNodes = -1;
            }

            if (numNodes < 5) {
                numNodes = -1;
            }

        } while (numNodes == -1);

        model.startRandom(numNodes);
    }
}
