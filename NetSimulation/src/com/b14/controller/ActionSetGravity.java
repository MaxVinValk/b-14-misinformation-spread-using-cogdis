package com.b14.controller;

import com.b14.model.GraphModel;
import com.b14.ModelManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.locks.ReentrantLock;

public class ActionSetGravity extends AbstractAction {

    private final GraphModel model;
    private final ModelManager manager;

    public ActionSetGravity(ModelManager manager, GraphModel model) {
        super("Change gravity");
        this.manager = manager;
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        double gravitationalPull;

        do {
            try {
                gravitationalPull = Double.parseDouble(JOptionPane.showInputDialog("Gravitational pull force " +
                        "(Currently: " + model.getCenterForce() + ", needs to be >= 0, <= 1000)"));
            } catch (NumberFormatException e) {
                gravitationalPull = -1;
            }

        } while (gravitationalPull < 0 || gravitationalPull > 1000);


        ReentrantLock physicsLock = manager.getPhysicsLock();

        try {
            physicsLock.lock();
            model.setCenterForce(gravitationalPull);
        } finally {
            physicsLock.unlock();
        }
    }
}
