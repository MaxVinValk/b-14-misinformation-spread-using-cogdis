package com.b14.controller.actions;

import com.b14.model.ImageCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ActionCapture extends AbstractAction {

    private ImageCapture ic;

    public ActionCapture(ImageCapture ic) {
        super("Capture image");
        this.ic = ic;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ic.captureImage();
    }
}
