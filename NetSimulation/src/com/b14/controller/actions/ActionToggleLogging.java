package com.b14.controller.actions;

import com.b14.model.DataLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ActionToggleLogging extends AbstractAction {

    private final DataLogger dataLogger;

    public ActionToggleLogging(DataLogger dataLogger) {
        super("Toggle output");
        this.dataLogger = dataLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dataLogger.toggleAllowOutput();
    }
}
