package com.b14.controller.actions;

import com.b14.view.GraphPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ActionResetColors extends AbstractAction {

    private GraphPanel panel;

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
