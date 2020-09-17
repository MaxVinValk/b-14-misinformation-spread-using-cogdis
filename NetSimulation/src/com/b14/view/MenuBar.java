package com.b14.view;

import com.b14.model.GraphModel;
import com.b14.model.ModelManager;

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
    public MenuBar(ModelManager manager, GraphModel model, Camera camera, GraphPanel panel) {
        add(new MenuBarControl(model));
        add(new MenuBarPhysics(manager));
        add(new MenuBarView(model, camera, panel));
    }
}
