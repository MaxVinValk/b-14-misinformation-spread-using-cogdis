package com.b14.controller;

import com.b14.model.GraphModel;
import com.b14.model.Node;
import com.b14.model.ModelManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  Changes connection limit for nodes.
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

        do {
            try {
                connectionLimit = Integer.parseInt(JOptionPane.showInputDialog("New connection limit (must be larger than 0, should be larger than 5):"));
            } catch (NumberFormatException e) {
                connectionLimit = 0;
            }

            if (connectionLimit < 1) {
                connectionLimit = 0;
            }

        } while (connectionLimit == 0);

        Node.setConnectionLimit(connectionLimit);

        ReentrantLock physicsLock = manager.getPhysicsLock();

        try {
            physicsLock.lock();
            for(Node n : model.getNodes()) {
                
                while (n.getConnectionCount() > connectionLimit) {
                    Node nodeToRemove = n.getNeighbours().get(random.nextInt(n.getConnectionCount()));
                    n.removeNeighbour(nodeToRemove);
                }
            }
        } finally {
            physicsLock.unlock();
        }
    }
}