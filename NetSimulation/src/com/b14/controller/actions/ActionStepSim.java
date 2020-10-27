package com.b14.controller.actions;

import com.b14.ModelManager;
import com.b14.model.GraphModel;

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
        super("Step simulation (spacebar)");
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
