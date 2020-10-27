package com.b14.controller.actions;

import com.b14.ModelManager;
import com.b14.model.GraphModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.locks.ReentrantLock;

public class ActionInitialize extends AbstractAction {

    private final GraphModel model;
    private final ModelManager manager;

    public ActionInitialize(ModelManager manager, GraphModel model) {
        super("Reset model");
        this.model = model;
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        int numNodes;
        String input;

        do {
            try {
                input = JOptionPane.showInputDialog("Number of nodes (>=5)?", 100);

                if (input == null) {
                    return;
                }

                numNodes = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                numNodes = -1;
            }

            if (numNodes < 5) {
                numNodes = -1;
            }

        } while (numNodes == -1);

        ReentrantLock physicsLock = manager.getPhysicsLock();

        try {
            physicsLock.lock();
            model.startRandom(numNodes);
        } finally {
            physicsLock.unlock();
        }

    }
}
