package com.b14.controller.actions;

import com.b14.model.GraphModel;
import com.b14.model.Node;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Updates maximum belief range that should be considered by
 * a person having an openness score of 1.
 */

public class ActionUpdateOpennessWeight extends AbstractAction {

    private final GraphModel model;

    public ActionUpdateOpennessWeight(GraphModel model) {
        super("Update Openness weight");
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        float opennessWeight;
        String input;

        do {
            try {
                input = JOptionPane.showInputDialog("Maximum belief range to consider with an openness score of 1 (0-1):", 0.1f);

                if (input == null) {
                    return;
                }

                opennessWeight = Float.parseFloat(input);
            } catch (NumberFormatException | NullPointerException e) {
                opennessWeight = -2;
            }

            if (opennessWeight < 0 || opennessWeight > 1) {
                opennessWeight = -2;
            }

        } while (opennessWeight == -2);

        Node.setOpennessWeight(opennessWeight);
        model.setReweightedOpenness();
    }
}
