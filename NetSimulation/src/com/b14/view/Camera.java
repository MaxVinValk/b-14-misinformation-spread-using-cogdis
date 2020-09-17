package com.b14.view;

import com.b14.model.Vector2D;

import java.awt.*;

/**
 *  The camera class is responsible for keeping track of where the view of the user is located.
 *  Without a camera, we could only draw on the screen-coordinates themselves. The camera allows us to move
 *  the view independently of the coordinate system used in the background.
 */

public class Camera {

    private static final float MAX_SCALE = 16f;
    private static final float MIN_SCALE = 0.125f;
    private static final float SCALE_FACTOR = 1.05f;

    private float scale = 1;

    private double x = 0.0f;
    private double y = 0.0f;

    private int width;
    private int height;

    /**
     * Creates a camera at 0,0 with a specified width and height
     * @param dim the dimensions that the camera needs to assume
     */

    public Camera(Dimension dim) {
        this.width = dim.width;
        this.height = dim.height;
    }

    /**
     * Move the camera around
     * @param x x-movement
     * @param y y-movement
     */

    public void moveCamera(double x, double y) {
        this.x += x;
        this.y += y;
    }

    /**
     * Given a position, this centers the camera on it.
     * TODO: Does not work well with zoom yet. For now it resets the zoom
     * @param pos The position to center on
     */
    public void centerCameraOn(Vector2D pos) {
        setScale(1.0f);
        x = pos.getX() - (width / 2);
        y = pos.getY() - (height / 2);
    }

    /**
     * This function zooms the camera in or out.
     * @param scrollUp indicates scroll direction
     */

    public void scale(boolean scrollUp) {
        if (scrollUp) {
            if (scale * SCALE_FACTOR <= MAX_SCALE) {
                scale *= SCALE_FACTOR;
            }
        } else {
            if (scale / SCALE_FACTOR >= MIN_SCALE) {
                scale /= SCALE_FACTOR;
            }
        }
    }

    //Getters & Setters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidthScaled() {
        return (int)(width / scale);
    }

    public int getHeightScaled() {
        return (int)(height / scale);
    }

    public float getScale() {
        return scale;
    }


    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
