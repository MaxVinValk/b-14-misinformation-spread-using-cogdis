package com.b14.controller.actions;

import com.b14.model.GraphModel;
import com.b14.model.recommendationstrategies.RecommendationStrategy;
import com.b14.view.GraphFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeSupport;

/**
 *  Sets new recommendation strategy and size
 */

//TODO: Make the pop-up multiple-choice

public class ActionUpdateNetworkRecommendation extends AbstractAction {

    private final GraphModel model;
    private final GraphFrame frame;


    public ActionUpdateNetworkRecommendation(GraphModel model, GraphFrame frame) {
        super("Update recommendation strategy and set size");
        this.model = model;
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        Object[] options = RecommendationStrategy.Strategy.values();

        RecommendationStrategy.Strategy rs = (RecommendationStrategy.Strategy)JOptionPane.showInputDialog(frame,
                "Select a recommendation strategy", "", JOptionPane.PLAIN_MESSAGE, null,
                options, RecommendationStrategy.Strategy.values()[0]);

        if (rs == null) {
            return;
        }

        int size;

        do {
            try {
                size = Integer.parseInt(JOptionPane.showInputDialog("Select recommendation set size" +
                        " (set to 0 to disable, currently set to: " + model.getRecommendationSize() + "):"));
            } catch (NumberFormatException e) {
                size = -1;
            }

            if (size < 0) {
                size = -1;
            }

        } while (size == -1);

        model.setRecommendationStrategy(rs);
        model.setRecommendationSize(size);
    }
}
