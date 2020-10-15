package com.b14.model;

import com.b14.ModelManager;
import com.b14.view.Camera;
import com.b14.view.GraphPanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *  This class is responsible for generating image output from the simulation to files
 */

public class ImageCapture {

    private final String DIR_BELIEF = "/belief";
    private final String DIR_DISSONANCE = "/dissonance";

    private ModelManager manager;
    private GraphPanel panel;
    private Camera camera;
    private GraphModel model;

    private String outputFolder = null;

    private int maxPhysicsSettleStepsBeforeCapture  = 5000;
    private float maxAvgVelocityBeforeCapture       = 10.0f;

    /**
     * Sets up an image capture
     * @param manager   the manager the image capture is attached to, to acquire the physics lock
     * @param model     the model that we are capturing from
     * @param camera    the camera that the panel uses to decide which region to capture
     * @param panel     the panel which has all the drawing logic
     */
    public ImageCapture(ModelManager manager, GraphModel model, Camera camera, GraphPanel panel) {
        this.manager = manager;
        this.model = model;
        this.panel = panel;
        this.camera = camera;
    }

    /**
     * Captures two images, one for the belief and the other for dissonance
     */
    public void captureImage() {

        boolean prevHeadless = panel.isHeadless();
        panel.setHeadless(false);

        letPhysicsSettle();
        setCameraOnAll();

        panel.setDrawBelief(true);
        BufferedImage bi = paintImage();
        saveToFile(bi, DIR_BELIEF);

        panel.setDrawBelief(false);
        bi = paintImage();
        saveToFile(bi, DIR_DISSONANCE);

        panel.setHeadless(prevHeadless);
    }

    /**
     * Moves the camera such that it captures all nodes in the scene.
     */
    private void setCameraOnAll() {

        Vector2D initPos = model.getNodes().get(0).getPosition();

        int lowX = (int)initPos.getX();
        int lowY = (int)initPos.getY();
        int highX = lowX;
        int highY = lowY;

        for (Node n : model.getNodes()) {
            Vector2D pos = n.getPosition();

            int x = (int)pos.getX();
            int y = (int) pos.getY();

            if (x < lowX) {
                lowX = x;
            } else if (x > highX) {
                highX = x;
            }

            if (y < lowY) {
                lowY = y;
            } else if (y > highY) {
                highY = y;
            }

        }

        camera.setCameraTo(lowX - 50, lowY - 50);
        float heightScale = camera.getHeight() / (float)(highY - lowY + 150);
        camera.setScale(heightScale);



    }

    /**
     * Performs physics updates until either the average velocity of all nodes is under a threshold, or until
     * the maximum time to let the network settle has passed.
     */
    private void letPhysicsSettle() {
        manager.setPhysics(false);  // disable physics updating from main
        double avgVelocity;

        int physicsSteps = 0;

        do {
            avgVelocity = model.physicsUpdate();
            physicsSteps++;
        } while (avgVelocity > 10.0f && physicsSteps < maxPhysicsSettleStepsBeforeCapture);
    }

    /**
     * Creates an image and performs the drawing logic on it.
     * @return an image which was drawn on by the panel
     */
    private BufferedImage paintImage() {
        int w = panel.getWidth();
        int h = panel.getHeight();

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();

        panel.paint(g);
        g.dispose();

        return bi;
    }

    /**
     * Saves the given image to an image
     * @param bi            The image to save
     * @param subDirectory  The sub-directory in which the image will be stored
     */
    private void saveToFile(BufferedImage bi, String subDirectory) {

        assert(outputFolder != null) : "image was not provided with an output folder";

        File outputFile = new File(outputFolder + subDirectory, model.getEpoch() + ".png");

        try {
            ImageIO.write(bi, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Change the root output folder to be whatever string is passed in. Also sets up sub-directories that are needed
     * @param outputFolder The new root folder
     */

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;

        File folder = new File(outputFolder);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File belief = new File(outputFolder + DIR_BELIEF);
        File dissonance = new File(outputFolder + DIR_DISSONANCE);

        belief.mkdir();
        dissonance.mkdir();
    }

    /*
        Getters, setter
     */

    public float getMaxAvgVelocityBeforeCapture() {
        return maxAvgVelocityBeforeCapture;
    }

    public void setMaxAvgVelocityBeforeCapture(float maxAvgVelocityBeforeCapture) {
        this.maxAvgVelocityBeforeCapture = maxAvgVelocityBeforeCapture;
    }
}
