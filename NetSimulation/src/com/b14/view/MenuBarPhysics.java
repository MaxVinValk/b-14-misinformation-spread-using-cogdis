package com.b14.view;

import com.b14.controller.ActionTogglePhysics;
import com.b14.model.ModelManager;

import javax.swing.*;

public class MenuBarPhysics extends JMenu {

    public MenuBarPhysics(ModelManager manager) {
        super("Physics");

        add(new JMenuItem(new ActionTogglePhysics(manager)));

    }
}