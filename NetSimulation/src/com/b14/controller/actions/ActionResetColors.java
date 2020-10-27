package com.b14.controller.actions;

import com.b14.view.GraphPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * The action for resetting colours to the defaults
 */
public class ActionResetColors extends AbstractAction {

    private final GraphPanel panel;

    /**
     * Sets up the action
     *
     * @param panel the panel on which we are resetting the colours
     */
    public ActionResetColors(GraphPanel panel) {
        super("Reset Colours");
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        panel.resetColors();
        panel.repaint();
    }
}
