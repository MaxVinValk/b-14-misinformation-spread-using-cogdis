package com.b14.model;

import com.b14.Main;
import com.b14.controller.InputController;
import com.b14.view.Camera;
import com.b14.view.GraphFrame;
import com.b14.view.GraphPanel;
import com.b14.view.MenuBar;

import java.awt.*;

/**
 *  This class is responsible for managing the entire simulation
 */

public class ModelManager {

    private final int START_WIDTH = 1024;
    private final int START_HEIGHT = 768;
    private final int FPS = 30;

    private final GraphModel model;
    private final GraphPanel panel;

    private boolean simulatePhysics = false;

    /**
     * Sets up all necessary elements for running the simulation, including menus, windows, and the model itself
     */

    public ModelManager() {
        model = new GraphModel();

        model.startRandom(50);

        Dimension startingWindowSize = new Dimension(START_WIDTH, START_HEIGHT);

        Camera camera = new Camera(startingWindowSize);

        GraphFrame frame = new GraphFrame("Network Simulation " + Main.VERSION, startingWindowSize, model, camera);
        panel = frame.getPanel();

        MenuBar menuBar = new MenuBar(this, model, camera, panel);
        frame.setJMenuBar(menuBar);

        frame.setupGraph();
        InputController inputController = new InputController(panel, camera);

    }


    /**
     * Calling this function will enter the main loop of the program, which performs all actions that are not happening
     * in parallel (such as listeners for input).
     */
    public void runSimulation() {
        boolean runSimulation = true;

        while (runSimulation) {

            if (simulatePhysics) {
                model.physicsUpdate();
            }

            //update panels
            panel.repaint();
            try {
                Thread.sleep(1000/FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void togglePhysics() {
        simulatePhysics = !simulatePhysics;
    }
}
