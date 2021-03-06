package com.b14.controller.actions;

import com.b14.model.DataLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ActionSetAvgMaxMovement extends AbstractAction implements PropertyChangeListener {

    private final DataLogger dl;

    public ActionSetAvgMaxMovement(DataLogger dl) {
        super("set max avg. movement (capture)");
        this.dl = dl;
        setState();
    }

    private void setState() {
        setEnabled(dl.isImgsGenerated());
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        float num;
        String input;
        do {
            try {
                input = JOptionPane.showInputDialog("Please specify the maximum allowed average" +
                        "velocity in a system before a capture is taken (> 0)\nCurrent: " +
                        dl.getIc().getMaxAvgVelocityBeforeCapture(), 10.0f);

                if (input == null) {
                    return;
                }

                num = Float.parseFloat(input);

            } catch (NumberFormatException e) {
                num = -1;
            }
        } while (num < 0);

        dl.getIc().setMaxAvgVelocityBeforeCapture(num);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setState();
    }
}
