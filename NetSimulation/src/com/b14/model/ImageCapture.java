package com.b14.model;

import com.b14.view.Camera;
import com.b14.view.GraphPanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

public class ImageCapture {

    private ModelManager manager;
    private GraphPanel panel;
    private Camera camera;
    private GraphModel model;

    private String outputFolder = null;

    private int maxPhysicsSettleStepsBeforeCapture  = 50000;
    private float maxAvgVelocityBeforeCapture       = 10.0f;


    public ImageCapture(ModelManager manager, GraphModel model, Camera camera, GraphPanel panel) {
        this.manager = manager;
        this.model = model;
        this.panel = panel;
        this.camera = camera;
    }


    public void captureImage() {

        boolean prevHeadless = panel.isHeadless();
        panel.setHeadless(false);

        letPhysicsSettle();
        setCameraOnAll();

        BufferedImage bi = paintImage();

        saveToFile(bi);

        panel.setHeadless(prevHeadless);
    }

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

        //float widthScale = camera.getWidthScaled() / (float)(highX - lowX + 100);
        float heightScale = camera.getHeight() / (float)(highY - lowY + 150);

        //float largestScaleDiff = Math.max(widthScale, heightScale);

        camera.setScale(heightScale);



    }

    private void letPhysicsSettle() {
        manager.setPhysics(false);  // disable physics updating from main
        double maxVelocity;

        int physicsSteps = 0;

        do {
            maxVelocity = model.physicsUpdate();

            physicsSteps++;

        } while (maxVelocity > 10.0f && physicsSteps < maxPhysicsSettleStepsBeforeCapture);
    }

    private BufferedImage paintImage() {
        int w = panel.getWidth();
        int h = panel.getHeight();

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();

        panel.paint(g);
        g.dispose();

        return bi;
    }

    private void saveToFile(BufferedImage bi) {

        assert(outputFolder != null) : "image was not provided with an output folder";

        File outputFile = new File(outputFolder, model.getEpoch() + ".png");

        try {
            ImageIO.write(bi, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;

        File folder = new File(outputFolder);

        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void setMaxAvgVelocityBeforeCapture(float maxAvgVelocityBeforeCapture) {
        this.maxAvgVelocityBeforeCapture = maxAvgVelocityBeforeCapture;
    }

    public float getMaxAvgVelocityBeforeCapture() {
        return maxAvgVelocityBeforeCapture;
    }
}
