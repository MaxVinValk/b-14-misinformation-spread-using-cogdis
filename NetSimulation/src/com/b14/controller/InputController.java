package com.b14.controller;

import com.b14.model.GraphModel;
import com.b14.model.Node;
import com.b14.view.Camera;
import com.b14.view.GraphPanel;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * This class is responsible for getting input from the user.
 * At the moment it only deals with mouse input, but it can be extended later to allow keyboard-input, too
 */

public class InputController extends MouseInputAdapter {

    private Camera camera;
    private GraphModel model;

    private boolean leftMouseButtonDown = false;
    private int mousePressLocationX = 0;
    private int mousePressLocationY = 0;

    private Node selectedNode = null;
    private int lastClicked = -1;

    /**
     * Creates an input controller
     * @param panel     The panel that displays the simulation
     * @param camera    The camera that determines what is within the panel view
     */
    public InputController(GraphPanel panel, Camera camera, GraphModel model) {
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);
        panel.addMouseWheelListener(this);
        this.camera = camera;
        this.model = model;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1 || event.getButton() == MouseEvent.BUTTON3 ) {
            selectedNode = model.getNodeOnPoint(camera.cameraToWorld(event.getX(), event.getY()));
            lastClicked = event.getButton();
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {

        if (event.getButton() == MouseEvent.BUTTON1) {
            mousePressLocationX = event.getX();
            mousePressLocationY = event.getY();

            leftMouseButtonDown = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1) {
            leftMouseButtonDown = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {

        if (leftMouseButtonDown) {

            double movementX = (-(event.getX() - mousePressLocationX) / camera.getScale());
            double movementY = (-(event.getY() - mousePressLocationY) / camera.getScale());

            camera.moveCamera(movementX, movementY);

            mousePressLocationX = event.getX();
            mousePressLocationY = event.getY();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        camera.scale(event.getWheelRotation() < 0);
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public int getLastClicked() {
        return lastClicked;
    }
}
