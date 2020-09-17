package com.b14.view;

import com.b14.controller.ActionInitSpreadSimulation;
import com.b14.controller.ActionInitialize;
import com.b14.controller.ActionRunSimulationSteps;
import com.b14.controller.ActionStepSim;
import com.b14.model.GraphModel;

import javax.swing.*;

/**
 * This menu houses all options that are related to the simulation itself
 */

public class MenuBarControl extends JMenu {

    public MenuBarControl(GraphModel model) {
        super("Sim Control");

        add(new JMenuItem(new ActionInitialize(model)));
        add(new JMenuItem(new ActionInitSpreadSimulation(model)));
        add(new JMenuItem(new ActionStepSim(model)));
        add(new JMenuItem(new ActionRunSimulationSteps(model)));

    }
}
