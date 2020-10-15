package com.b14.view.menubars;

import com.b14.ModelManager;
import com.b14.controller.actions.ActionLoadAgents;
import com.b14.model.GraphModel;
import com.b14.view.GraphFrame;

import javax.swing.*;

public class MenuBarFile extends JMenu {
    public MenuBarFile(ModelManager manager, GraphModel model, GraphFrame frame) {
        super("File");
        add(new JMenuItem(new ActionLoadAgents(manager, model, frame)));
    }
}
