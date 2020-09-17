package com.b14.controller;

import com.b14.model.GraphModel;
import com.b14.model.Message;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *  Inits the spread of information
 */

public class ActionInitSpreadSimulation extends AbstractAction {

    private final GraphModel model;

    public ActionInitSpreadSimulation(GraphModel model) {
        super("Init message spread");
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        float informationQuality;

        do {
            try {
                informationQuality = Float.parseFloat(JOptionPane.showInputDialog("Information quality of initial message (0.0 - 1.0)?"));
            } catch (NumberFormatException e) {
                informationQuality = -1;
            }

            if (informationQuality < 0 || informationQuality > 1) {
                informationQuality = -1;
            }

        } while (informationQuality == -1);

        model.initSimulateSpread(new Message(0.5f, informationQuality));
    }
}
