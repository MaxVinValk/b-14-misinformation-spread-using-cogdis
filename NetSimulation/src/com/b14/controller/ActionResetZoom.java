package com.b14.controller;

import com.b14.view.Camera;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Resets camera zoom to 1.
 */

public class ActionResetZoom extends AbstractAction {

    private final Camera camera;

    public ActionResetZoom(Camera camera) {
        super("Reset zoom");

        this.camera = camera;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        camera.setScale(1.0f);
    }
}
