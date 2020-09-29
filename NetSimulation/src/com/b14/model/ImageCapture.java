package com.b14.model;

import com.b14.view.Camera;
import com.b14.view.GraphPanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCapture {

    private GraphPanel panel;
    private Camera camera;
    private GraphModel model;


    public ImageCapture(GraphModel model, GraphPanel panel, Camera camera) {
        this.model = model;
        this.panel = panel;
        this.camera = camera;
    }

    public void captureImage() {

        //keep waiting until the physics update cool down
        double avgVelocity;

        do {
            avgVelocity = model.physicsUpdate();
            System.out.println(avgVelocity);
        } while (avgVelocity > 0.5f);

        setCameraOnAll();
        int w = panel.getWidth();
        int h = panel.getHeight();

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();

        panel.paint(g);
        g.dispose();

        File outputFile = new File("test.png");

        try {
            ImageIO.write(bi, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


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
        camera.setScale(0.35f);
    }

}
