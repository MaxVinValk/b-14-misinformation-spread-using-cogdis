package com.b14.view.menubars;

import com.b14.controller.actions.*;
import com.b14.model.DataLogger;

import javax.swing.*;

public class MenuBarLogging extends JMenu {

    public MenuBarLogging(DataLogger dataLogger) {
        super("Log generation");

        ActionEnableLogging ael = new ActionEnableLogging(dataLogger);
        ActionDisableLogging adl = new ActionDisableLogging(dataLogger);
        ActionDisableImg adi = new ActionDisableImg(dataLogger);
        ActionSetAvgMaxMovement asamm = new ActionSetAvgMaxMovement(dataLogger);

        dataLogger.addPropertyChangeListener(ael);
        dataLogger.addPropertyChangeListener(adl);
        dataLogger.addPropertyChangeListener(adi);
        dataLogger.addPropertyChangeListener(asamm);

        add(new JMenuItem(ael));
        add(new JMenuItem(adl));
        add(new JMenuItem(new ActionEnableImg(dataLogger)));
        add(new JMenuItem(adi));
        add(new JMenuItem(asamm));

    }
}