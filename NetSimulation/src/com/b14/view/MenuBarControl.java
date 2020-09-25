package com.b14.view;

import com.b14.controller.ActionInitialize;
import com.b14.controller.ActionRunSimulationSteps;
import com.b14.controller.ActionStepSim;
import com.b14.controller.ActionUpdateNetworkDissonance;
import com.b14.model.GraphModel;
import com.b14.model.ModelManager;
import com.b14.model.DataLogger;

import javax.swing.*;

/**
 * This menu houses all options that are related to the simulation itself
 */

public class MenuBarControl extends JMenu {

    public MenuBarControl(ModelManager manager, GraphModel model, DataLogger dataLogger) {
        super("Sim Control");

        add(new JMenuItem(new ActionInitialize(manager, model, dataLogger)));
        add(new JMenuItem(new ActionUpdateNetworkDissonance(model)));
        add(new JMenuItem(new ActionStepSim(manager, model, dataLogger)));
        add(new JMenuItem(new ActionRunSimulationSteps(manager, model, dataLogger)));

    }
}
