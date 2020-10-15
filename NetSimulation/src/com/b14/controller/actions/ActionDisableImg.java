package com.b14.controller.actions;

import com.b14.model.DataLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ActionDisableImg extends AbstractAction implements PropertyChangeListener {

    private DataLogger dataLogger;

    public ActionDisableImg(DataLogger dataLogger) {
        super("Disable img output");
        this.dataLogger = dataLogger;
        setState();
    }

    private void setState() {
        setEnabled(dataLogger.isImgsGenerated());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dataLogger.setImgsGenerated(false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setState();
    }
}
