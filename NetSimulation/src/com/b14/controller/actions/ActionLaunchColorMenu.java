package com.b14.controller.actions;

import com.b14.view.GraphPanel;
import com.b14.view.popup_menus.ColorChooserFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ActionLaunchColorMenu extends AbstractAction {

    private GraphPanel panel;

    public ActionLaunchColorMenu(GraphPanel panel) {
        super("Choose colours");
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new ColorChooserFrame(panel);
    }
}
