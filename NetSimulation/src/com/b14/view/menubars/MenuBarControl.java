package com.b14.view.menubars;

import com.b14.ModelManager;
import com.b14.controller.actions.*;
import com.b14.model.GraphModel;
import com.b14.view.GraphFrame;

import javax.swing.*;

/**
 * This menu houses all options that are related to the simulation itself
 */

public class MenuBarControl extends JMenu {

    public MenuBarControl(ModelManager manager, GraphModel model, GraphFrame frame) {
        super("Sim Control");

        add(new JMenuItem(new ActionInitialize(manager, model)));
        add(new JMenuItem(new ActionUpdateNetworkDissonance(model)));
        add(new JMenuItem(new ActionUpdateNetworkRecommendation(model, frame)));
        add(new JMenuItem(new ActionUpdateDissonanceWeight(model)));
        add(new JMenuItem(new ActionUpdateOpennessWeight(model)));
        add(new JMenuItem(new ActionUpdateNodeConnectionLimit(manager, model)));
        add(new JMenuItem(new ActionStepSim(manager, model)));
        add(new JMenuItem(new ActionRunSimulationSteps(manager, model)));
    }
}
