package com.b14.controller.actions;

import com.b14.model.DataLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ActionDisableLogging extends AbstractAction implements PropertyChangeListener {

    private DataLogger dataLogger;

    public ActionDisableLogging(DataLogger dataLogger) {
        super("Disable data output");
        this.dataLogger = dataLogger;
        setState();
    }

    private void setState() {
        setEnabled(dataLogger.isOutputAllowed());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dataLogger.setAllowOutput(false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setState();
    }
}
