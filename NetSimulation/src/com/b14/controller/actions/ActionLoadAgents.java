package com.b14.controller.actions;

import com.b14.ModelManager;
import com.b14.controller.FileChooser;
import com.b14.model.GraphModel;
import com.b14.view.GraphFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.locks.ReentrantLock;

public class ActionLoadAgents extends AbstractAction {

    private final ModelManager manager;
    private final GraphModel model;
    private final GraphFrame frame;

    private final FileChooser fileChooser;

    public ActionLoadAgents(ModelManager manager, GraphModel model, GraphFrame frame) {
        super("Load agents");

        this.manager = manager;
        this.model = model;
        this.frame = frame;

        fileChooser = new FileChooser(FileChooser.OPEN_DIALOG);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {

            ReentrantLock physicsLock = manager.getPhysicsLock();

            try {
                physicsLock.lock();
                model.setAgentsFromFile(fileChooser.getSelectedFile().getAbsolutePath());

            } catch (Exception exception) {
                JOptionPane.showMessageDialog(frame, "Could not open provided file. Are you sure it is" +
                        " properly formatted?", "Error loading file", JOptionPane.ERROR_MESSAGE);

                model.startRandom(10);
            } finally {
                physicsLock.unlock();
            }
        }
    }
}
