package com.b14.controller;

import com.b14.model.GraphModel;
import com.b14.model.ModelManager;
import com.b14.model.DataLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Steps the simulation forward by 1
 */

public class ActionStepSim extends AbstractAction {

    private final GraphModel model;
    private final ModelManager manager;
    private final DataLogger dataLogger;

    public ActionStepSim(ModelManager manager, GraphModel model, DataLogger dataLogger) {
        super("Step simulation");
        this.model = model;
        this.manager = manager;
        this.dataLogger = dataLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        ReentrantLock physicsLock = manager.getPhysicsLock();

        try {
            physicsLock.lock();
            model.simulateSpreadStep();
            int epoch = model.getEpoch();
            dataLogger.logData(epoch);
        } finally {
            physicsLock.unlock();
        }
    }
}
