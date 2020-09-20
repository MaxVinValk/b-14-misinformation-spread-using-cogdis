package com.b14.controller;

import com.b14.model.GraphModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Steps the simulation forward by 1
 */

public class ActionStepSim extends AbstractAction {

    private final GraphModel model;

    public ActionStepSim(GraphModel model) {
        super("Step simulation");
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        model.simulateSpreadStep();
        model.fraternize();
    }
}
