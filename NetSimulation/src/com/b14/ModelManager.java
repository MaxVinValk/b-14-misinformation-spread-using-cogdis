package com.b14;

import com.b14.controller.InputController;
import com.b14.model.DataLogger;
import com.b14.model.GraphModel;
import com.b14.model.ImageCapture;
import com.b14.view.Camera;
import com.b14.view.GraphFrame;
import com.b14.view.GraphPanel;
import com.b14.view.menubars.MenuBar;

import java.awt.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is responsible for managing the entire simulation
 */

public class ModelManager {

    private final int START_WIDTH = 1024;
    private final int START_HEIGHT = 768;
    private final int FPS = 30;

    private final GraphModel model;
    private final GraphPanel panel;
    private final Camera camera;

    private final DataLogger dataLogger;
    ReentrantLock physicsLock = new ReentrantLock();
    private boolean simulatePhysics = false;

    /**
     * Sets up all necessary elements for running the simulation, including menus, windows, and the model itself
     * A bit messy at this moment, but it links together a lot of components.
     */

    public ModelManager() {

        Dimension startingWindowSize = new Dimension(START_WIDTH, START_HEIGHT);


        dataLogger = new DataLogger();
        model = new GraphModel(dataLogger);
        dataLogger.setModel(model);

        model.startRandom(50);

        camera = new Camera(startingWindowSize);

        GraphFrame frame = new GraphFrame("Network Simulation " + Main.VERSION, startingWindowSize, model, camera);
        panel = frame.getPanel();

        MenuBar menuBar = new MenuBar(this, model, camera, panel, frame, dataLogger);
        frame.setJMenuBar(menuBar);

        frame.setupGraph();

        InputController inputController = new InputController(this, model, camera, panel, frame);
        panel.addInputController(inputController);
        inputController.addPropertyChangeListener(panel);

        ImageCapture ic = new ImageCapture(this, model, camera, panel);
        dataLogger.setImageCapture(ic);

        model.addPropertyChangeListener(panel);
        camera.addPropertyChangeListener(panel);
    }


    /**
     * Calling this function will enter the main loop of the program, which performs all actions that are not happening
     * in parallel (such as listeners for input).
     */
    public void runSimulation() {
        boolean runSimulation = true;

        while (runSimulation) {

            if (simulatePhysics) {
                physicsLock.lock();
                try {
                    model.physicsUpdate();
                } finally {
                    physicsLock.unlock();
                }
            }

            //update panels
            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void togglePhysics() {
        simulatePhysics = !simulatePhysics;
    }

    public void setPhysics(boolean val) {
        simulatePhysics = val;
    }

    public ReentrantLock getPhysicsLock() {
        return physicsLock;
    }

    public int getFPS() {
        return FPS;
    }
}
