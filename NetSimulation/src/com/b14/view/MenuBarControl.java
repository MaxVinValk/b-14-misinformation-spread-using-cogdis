package com.b14.view;

import com.b14.controller.ActionInitialize;
import com.b14.controller.ActionRunSimulationSteps;
import com.b14.controller.ActionStepSim;
import com.b14.controller.ActionUpdateNetworkDissonance;
import com.b14.model.GraphModel;
import com.b14.model.ModelManager;

import javax.swing.*;

/**
 * This menu houses all options that are related to the simulation itself
 */

public class MenuBarControl extends JMenu {

    public MenuBarControl(ModelManager manager, GraphModel model) {
        super("Sim Control");

        add(new JMenuItem(new ActionInitialize(manager, model)));
        add(new JMenuItem(new ActionUpdateNetworkDissonance(model)));
        add(new JMenuItem(new ActionStepSim(manager, model)));
        add(new JMenuItem(new ActionRunSimulationSteps(manager, model)));

    }
}
