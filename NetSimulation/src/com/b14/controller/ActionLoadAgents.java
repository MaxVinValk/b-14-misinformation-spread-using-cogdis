package com.b14.controller;

import com.b14.view.GraphFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ActionLoadAgents extends AbstractAction {

    private final GraphFrame frame;

    private final FileChooser fileChooser;

    public ActionLoadAgents(GraphFrame frame) {
        super("Load agents");

        this.frame = frame;

        fileChooser = new FileChooser(FileChooser.OPEN_DIALOG);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            System.out.println(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
}
