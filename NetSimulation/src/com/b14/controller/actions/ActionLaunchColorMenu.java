package com.b14.controller.actions;

import com.b14.view.GraphPanel;
import com.b14.view.popup_menus.ColorChooserFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * This action will launch a colour menu chooser allowing the user to set colours for many parts of the simulation
 */

public class ActionLaunchColorMenu extends AbstractAction {

    private final GraphPanel panel;

    /**
     * Sets up the action
     *
     * @param panel The GraphPanel which holds the colours we can configure
     */
    public ActionLaunchColorMenu(GraphPanel panel) {
        super("Choose colours");
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new ColorChooserFrame(panel);
    }
}
