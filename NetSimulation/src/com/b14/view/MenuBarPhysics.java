package com.b14.view;

import com.b14.controller.ActionSetGravity;
import com.b14.controller.ActionTogglePhysics;
import com.b14.model.GraphModel;
import com.b14.model.ModelManager;

import javax.swing.*;

public class MenuBarPhysics extends JMenu {

    public MenuBarPhysics(ModelManager manager, GraphModel model) {
        super("Physics");

        add(new JMenuItem(new ActionTogglePhysics(manager)));
        add(new JMenuItem(new ActionSetGravity(manager, model)));

    }
}