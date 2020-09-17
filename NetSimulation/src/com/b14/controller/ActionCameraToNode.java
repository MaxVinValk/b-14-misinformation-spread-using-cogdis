package com.b14.controller;

import com.b14.model.GraphModel;
import com.b14.model.Node;
import com.b14.view.Camera;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 *  Performs the action of pulling the camera to node 0. Handy if you lost the nodes in view
 */

public class ActionCameraToNode extends AbstractAction {

    private final GraphModel model;
    private final Camera camera;

    public ActionCameraToNode(GraphModel model, Camera camera) {
        super("Pull camera to node");

        this.model = model;
        this.camera = camera;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<Node> nodes = model.getNodes();

        if (nodes.size() > 0) {
            camera.centerCameraOn(nodes.get(0).getPosition());
        }
    }
}
