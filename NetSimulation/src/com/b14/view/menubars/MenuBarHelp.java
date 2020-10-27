package com.b14.view.menubars;

import com.b14.controller.actions.ActionLaunchAbout;

import javax.swing.*;

/**
 * Contains simulation info
 */
public class MenuBarHelp extends JMenu {

    public MenuBarHelp() {
        super("Help");

        add(new JMenuItem(new ActionLaunchAbout()));
    }

}
