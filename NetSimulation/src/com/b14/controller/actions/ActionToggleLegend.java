package com.b14.controller.actions;

import com.b14.view.GraphPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Performs the action of toggling the drawing of the legend
 */

public class ActionToggleLegend extends AbstractAction {

    private final GraphPanel panel;

    /**
     * Creates a ActionToggleLegend
     *
     * @param panel the GraphPanel on which the legend is toggled
     */
    public ActionToggleLegend(GraphPanel panel) {
        super("Toggle legend");

        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        panel.toggleShowLegend();
    }
}
