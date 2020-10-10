package com.b14.controller;

import com.b14.model.GraphModel;
import com.b14.model.Node;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *  Updates maximum impact the dissonance ratio should have on one's openness.
 *  With higher values individuals become more narrow-minded if they experience dissonance
 */

public class ActionUpdateDissonanceWeight extends AbstractAction {

    private final GraphModel model;

    public ActionUpdateDissonanceWeight(GraphModel model) {
        super("Update Dissonance weight");
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        float dissonanceRatioWeight;

        do {
            try {
                dissonanceRatioWeight = Float.parseFloat(JOptionPane.showInputDialog("Weight applied to dissonance ratio to penalize openness (0-1):"));
            } catch (NumberFormatException | NullPointerException e) {
                dissonanceRatioWeight = -2;
            }

            if (dissonanceRatioWeight < 0 || dissonanceRatioWeight > 1) {
                dissonanceRatioWeight = -2;
            }

        } while (dissonanceRatioWeight == -2);

        Node.setDissonanceRatioWeight(dissonanceRatioWeight);
    }
}
