package com.b14.controller.actions;

import com.b14.model.GraphModel;
import com.b14.model.Node;
import com.b14.ModelManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Changes connection limit for nodes.
 */

public class ActionUpdateNodeConnectionLimit extends AbstractAction {

    private final GraphModel model;
    private final ModelManager manager;
    private final Random random = new Random(0);

    public ActionUpdateNodeConnectionLimit(ModelManager manager, GraphModel model) {
        super("Update connection limit for nodes");
        this.model = model;
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        int connectionLimit;
        String input;

        do {
            try {
                input = JOptionPane.showInputDialog("New connection limit (must be larger than 1, should be larger than 5):", 50);

                if (input == null) {
                    return;
                }

                connectionLimit = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                connectionLimit = 0;
            }

            if (connectionLimit < 2) {
                connectionLimit = 0;
            }

        } while (connectionLimit == 0);

        Node.setConnectionLimit(connectionLimit);

        ReentrantLock physicsLock = manager.getPhysicsLock();

        try {
            physicsLock.lock();
            model.setConnectionLimitOnNodes(connectionLimit);
        } finally {
            physicsLock.unlock();
        }
    }
}
