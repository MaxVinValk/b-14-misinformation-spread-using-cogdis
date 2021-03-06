package com.b14.controller.actions;

import com.b14.model.GraphModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Inits the spread of information
 */

public class ActionUpdateNetworkDissonance extends AbstractAction {

    private final GraphModel model;

    public ActionUpdateNetworkDissonance(GraphModel model) {
        super("Update dissonance network wide");
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        float dissonanceUpdate;
        String input;

        do {
            try {
                input = JOptionPane.showInputDialog("Dissonance update (-1.0 - 1.0):", 0.0f);

                if (input == null) {
                    return;
                }

                dissonanceUpdate = Float.parseFloat(input);
            } catch (NumberFormatException e) {
                dissonanceUpdate = -2;
            }

            if (dissonanceUpdate < -1 || dissonanceUpdate > 1) {
                dissonanceUpdate = -2;
            }

        } while (dissonanceUpdate == -2);

        model.updateDissonances(dissonanceUpdate);
    }
}
