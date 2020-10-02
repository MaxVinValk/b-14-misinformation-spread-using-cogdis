package com.b14.controller;

import com.b14.model.GraphModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *  Sets new recommendation strategy and size
 */

public class ActionUpdateNetworkRecommendation extends AbstractAction {

    private final GraphModel model;

    public ActionUpdateNetworkRecommendation(GraphModel model) {
        super("Update recommendation strategy and set size");
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        String strategy;

        do {
            strategy = JOptionPane.showInputDialog("Select strategy (random | polarize):");
            try {
                if (!strategy.equals("random") && !strategy.equals("polarize")) {
                    strategy = "None";
                }
            } catch (NullPointerException e) {
                strategy = "None";
            }
            

        } while (strategy.equals("None"));

        Integer size;

        do {
            try {
                size = Integer.parseInt(JOptionPane.showInputDialog("Select recommendation set size (set to 0 to disable):"));
            } catch (NumberFormatException e) {
                size = -1;
            }

            if (size < 0) {
                size = -1;
            }

        } while (size == -1);

        model.setRecommendationStrategy(strategy);
        model.setRecommendationSize(size);
    }
}
