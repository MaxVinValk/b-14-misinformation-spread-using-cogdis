package com.b14.controller;

import com.b14.model.GraphModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *  Not meant to be an actual implementation, more so to demonstrate how we can run the simulation in general.
 */

public class ActionRunSimulationSteps extends AbstractAction {

    private final GraphModel model;

    public ActionRunSimulationSteps(GraphModel model) {
        super("Perform n steps on Network");
        this.model = model;
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

        while (timesSpread-- > 0)
        {
            model.simulateSpreadStep(); // Note: pruning + fraternize is now included in each spreading step!
        }
    }
}
