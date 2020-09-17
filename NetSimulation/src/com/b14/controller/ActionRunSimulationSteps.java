package com.b14.controller;

import com.b14.model.GraphModel;
import com.b14.model.Message;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *  Not meant to be an actual implementation, more so to demonstrate how we can run the simulation in general.
 */

public class ActionRunSimulationSteps extends AbstractAction {

    private final GraphModel model;

    public ActionRunSimulationSteps(GraphModel model) {
        super("Spread random message n-steps");
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

        int timesSpread;

        do {
            try {
                timesSpread = Integer.parseInt(JOptionPane.showInputDialog("Number of full spreads + pruning to simulate?"));
            } catch (NumberFormatException e) {
                timesSpread = -1;
            }

        } while (timesSpread <= 0);

        while (timesSpread-- > 0)
        {
            model.initSimulateSpread(new Message(0.5f, informationQuality));

            //ugly, but repeats the spread until no spread occurs anymore
            while(model.simulateSpreadStep()) {}

            //Note the order : if reversed, then we increase the chances of lone nodes seperated from the network
            model.fraternize();
            model.pruneDissidents();
        }
    }
}
