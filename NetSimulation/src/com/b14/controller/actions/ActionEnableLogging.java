package com.b14.controller.actions;

import com.b14.model.DataLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ActionEnableLogging extends AbstractAction implements PropertyChangeListener {

    private DataLogger dataLogger;

    public ActionEnableLogging(DataLogger dataLogger) {
        super("Enable data output");
        this.dataLogger = dataLogger;
        setState();
    }

    private void setState() {
        setEnabled(!dataLogger.isOutputAllowed());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dataLogger.setAllowOutput(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setState();
    }
}
