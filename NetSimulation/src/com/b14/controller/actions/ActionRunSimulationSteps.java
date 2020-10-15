package com.b14.controller.actions;

import com.b14.model.GraphModel;
import com.b14.ModelManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  Not meant to be an actual implementation, more so to demonstrate how we can run the simulation in general.
 */

public class ActionRunSimulationSteps extends AbstractAction {

    private final ModelManager manager;
    private final GraphModel model;

    public ActionRunSimulationSteps(ModelManager manager, GraphModel model) {
        super("Perform n steps on Network");
        this.model = model;
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        int timesSpread;

        do {
            try {
                timesSpread = Integer.parseInt(JOptionPane.showInputDialog("Number of full exchanges + pruning to simulate?"));
            } catch (NumberFormatException e) {
                timesSpread = -1;
            }

        } while (timesSpread <= 0);

        ReentrantLock physicsLock = manager.getPhysicsLock();

        try {
            physicsLock.lock();
            while (timesSpread-- > 0) {
                model.simulateSpreadStep(); // Note: pruning + fraternize is now included in each spreading step!
            }
        } finally {
            physicsLock.unlock();
        }
    }
}
