package com.b14.controller.actions;

import com.b14.view.popup_menus.InfoFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Launches about section
 */
public class ActionLaunchAbout extends AbstractAction {
    public ActionLaunchAbout() {
        super("About");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new InfoFrame();
    }
}
