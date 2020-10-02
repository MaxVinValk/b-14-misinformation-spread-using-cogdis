package com.b14.controller;

import com.b14.model.GraphModel;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *  Changes connection limit for nodes.
 */

public class ActionUpdateNodeConnectionLimit extends AbstractAction {

    private final GraphModel model;

    public ActionUpdateNodeConnectionLimit(GraphModel model) {
        super("Update connection limit for nodes");
        this.model = model;
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

        model.changeNodeConnectionLimit(connectionLimit);
    }
}
