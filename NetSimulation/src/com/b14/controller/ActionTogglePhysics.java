package com.b14.controller;

import com.b14.ModelManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *  Toggles physics simulation on/off
 */

public class ActionTogglePhysics extends AbstractAction {

    private final ModelManager manager;

    public ActionTogglePhysics(ModelManager manager) {
        super("Toggle simulation");
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        manager.togglePhysics();
    }
}
