package com.b14.view.menubars;

import com.b14.controller.actions.ActionCapture;
import com.b14.model.ImageCapture;

import javax.swing.*;

public class MenuBarCapture extends JMenu {

    public MenuBarCapture(ImageCapture ic) {
        super("capture");
        add(new JMenuItem(new ActionCapture(ic)));
    }


}