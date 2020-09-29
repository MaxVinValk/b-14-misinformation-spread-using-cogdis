package com.b14.view;

import com.b14.controller.*;
import com.b14.model.DataLogger;
import com.b14.model.GraphModel;
import com.b14.model.ModelManager;

import javax.swing.*;

public class MenuBarLogging extends JMenu {

    public MenuBarLogging(DataLogger dataLogger) {
        super("Log generation");

        ActionEnableLogging ael = new ActionEnableLogging(dataLogger);
        ActionDisableLogging adl = new ActionDisableLogging(dataLogger);

        dataLogger.addPropertyChangeListener(ael);
        dataLogger.addPropertyChangeListener(adl);

        add(new JMenuItem(ael));
        add(new JMenuItem(adl));

    }
}