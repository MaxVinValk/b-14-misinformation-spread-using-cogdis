package com.b14.model;

import java.util.Random;

/**
 * A class that has some vector functionality
 */

public class Vector2D {

    private double x;
    private double y;

    private final static Random random = new Random(0);

    /**
     * Creates a vector by copying another
     * @param other Another vector to be copied
     */

    public Vector2D(Vector2D other) {
        this.x = other.getX();
        this.y = other.getY();
    }

    /**
     * Create a vector
     * @param x
     * @param y
     */

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Given 2 vectors, it creates a vector to the first from the second
     * @param to    destination Coords
     * @param from  origin Coords
     */
    public Vector2D(Vector2D to, Vector2D from) {
        this(to.getX(), to.getY(),from.getX(), from.getY());
    }

    /**
     * Given 4 coordinates, creates a vector to the first pair from the second
     * @param x1    to x-coordinate
     * @param y1    to y-coordinate
     * @param x2    from x-coordinate
     * @param y2    from y-coordinate
     */
    public Vector2D(double x1, double y1, double x2, double y2) {
        x = x2 - x1;
        y = y2 - y1;

    }

    /**
     * @return a copy of the vector
     */
    public Vector2D getCopy() {
        return new Vector2D(this);
    }

    /**
     * Trims the vector down such that its length is 1.
     */
    public void setToUnitVector() {

        //We set the vector to a random unit vector if length is 0. This is needed in the case of this vector
        //representing a distance between two nodes on the exact same spot
        if (getLength() == 0) {
            setToRandomUnitVector();
        }

        x = x / getLength();
        y = y / getLength();
    }

    /**
     * Multiplies the vector with a scalar
     * @param val the multiplier
     */
    public void multiplyWith(double val) {
        x *= val;
        y *= val;
    }

    public double getLength() {
        return Math.sqrt(x*x + y*y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setToRandomUnitVector() {
        x = random.nextFloat();
        y = Math.sqrt(1 - x*x);
    }

    /**
     * Adds the other vector to this one
     * @param other the vector to add
     */
    public void add(Vector2D other) {
        this.x += other.getX();
        this.y += other.getY();
    }

    /**
     * Adds to the vector
     * @param x x-value to add
     * @param y y-value to add
     */

    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    @Override
    public String toString() {

        return "[" + x + ", " + y + "]\n";
    }
}
