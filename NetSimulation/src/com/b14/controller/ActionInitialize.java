package com.b14.controller;

import com.b14.model.GraphModel;
import com.b14.model.ModelManager;
import com.b14.model.DataLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.locks.ReentrantLock;

public class ActionInitialize extends AbstractAction {

    private final GraphModel model;
    private final ModelManager manager;
    private final DataLogger dataLogger;

    public ActionInitialize(ModelManager manager, GraphModel model, DataLogger dataLogger) {
        super("Reset model");
        this.model = model;
        this.manager = manager;
        this.dataLogger = dataLogger;
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

        ReentrantLock physicsLock = manager.getPhysicsLock();

        try {
            physicsLock.lock();
            model.startRandom(numNodes);
            dataLogger.startNewSession();
        } finally {
            physicsLock.unlock();
        }

    }
}
