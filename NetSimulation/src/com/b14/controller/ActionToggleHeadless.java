package com.b14.controller;

import com.b14.view.GraphPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ActionToggleHeadless extends AbstractAction {

    private final GraphPanel panel;

    public ActionToggleHeadless(GraphPanel panel) {
        super("Toggle headless");
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        panel.toggleHeadless();
    }
}
