package com.b14.controller;

import com.b14.model.GraphModel;
import com.b14.model.ModelManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Steps the simulation forward by 1
 */

public class ActionStepSim extends AbstractAction {

    private final GraphModel model;
    private final ModelManager manager;

    public ActionStepSim(ModelManager manager, GraphModel model) {
        super("Step simulation");
        this.model = model;
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        ReentrantLock physicsLock = manager.getPhysicsLock();

        try {
            physicsLock.lock();
            model.simulateSpreadStep();
        } finally {
            physicsLock.unlock();
        }
    }
}
