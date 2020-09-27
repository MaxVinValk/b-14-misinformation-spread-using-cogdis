package com.b14.view;

import com.b14.controller.*;
import com.b14.model.DataLogger;
import com.b14.model.GraphModel;
import com.b14.model.ModelManager;

import javax.swing.*;

public class MenuBarLogging extends JMenu {

    public MenuBarLogging(DataLogger dataLogger) {
        super("Log generation");

        add(new JMenuItem(new ActionToggleLogging(dataLogger)));

    }
}