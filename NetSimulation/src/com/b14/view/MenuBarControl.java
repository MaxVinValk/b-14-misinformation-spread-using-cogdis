package com.b14.view;

import com.b14.controller.*;
import com.b14.model.GraphModel;
import com.b14.model.ModelManager;
import com.b14.model.DataLogger;

import javax.swing.*;

/**
 * This menu houses all options that are related to the simulation itself
 */

public class MenuBarControl extends JMenu {

    public MenuBarControl(ModelManager manager, GraphModel model, GraphPanel panel, DataLogger dataLogger) {
        super("Sim Control");

        add(new JMenuItem(new ActionInitialize(manager, model)));
        add(new JMenuItem(new ActionUpdateNetworkDissonance(model)));
        add(new JMenuItem(new ActionStepSim(manager, model)));
        add(new JMenuItem(new ActionRunSimulationSteps(manager, model)));
    }
}
