package com.b14.controller.actions;

import com.b14.view.GraphPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ActionShowDissonance extends AbstractAction implements PropertyChangeListener {

    private GraphPanel panel;

    public ActionShowDissonance(GraphPanel panel) {
        super("Show dissonance");
        this.panel = panel;
        setState();
    }

    private void setState() {
        setEnabled(panel.isDrawingBelief());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        panel.setDrawBelief(false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setState();
    }
}
