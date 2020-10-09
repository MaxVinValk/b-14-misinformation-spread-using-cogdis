package com.b14.view;

import com.b14.controller.ActionLoadAgents;

import javax.swing.*;

public class MenuBarFile extends JMenu {
    public MenuBarFile(GraphFrame frame) {
        super("File");
        add(new JMenuItem(new ActionLoadAgents(frame)));
    }
}
