package com.b14.view;

import com.b14.model.GraphModel;
import com.b14.ModelManager;
import com.b14.model.DataLogger;

import javax.swing.*;

/**
 * The menu-bar on top of the panel
 */

public class MenuBar extends JMenuBar {

    /**
     * Creates a menu-bar
     * @param model     The model that houses all relevant information about the network
     * @param camera    The camera that decides what is in view (and what not)
     */
    public MenuBar(ModelManager manager, GraphModel model, Camera camera, GraphPanel panel, GraphFrame frame, DataLogger dataLogger) {
        add(new MenuBarFile(frame));
        add(new MenuBarControl(manager, model, panel, dataLogger));
        add(new MenuBarPhysics(manager, model));
        add(new MenuBarView(model, camera, panel));
        add(new MenuBarLogging(dataLogger));
    }
}
