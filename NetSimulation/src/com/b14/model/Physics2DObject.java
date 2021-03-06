package com.b14.model;

/**
 * This class is responsible for performing physics updates of a 2d object
 */

public class Physics2DObject {

    private static final double FRICTION = 0.9;

    protected Vector2D position;
    protected Vector2D acceleration;
    protected Vector2D velocity;

    protected double mass = 1.0;

    public Physics2DObject() {
        position = new Vector2D(0, 0);
        acceleration = new Vector2D(0, 0);
        velocity = new Vector2D(0, 0);
    }

    /**
     * Returns the distance between this object and another
     *
     * @param other The other Physics2DObject object
     * @return the distance between the two Physics2DObjects
     */
    public double getDistance(Physics2DObject other) {
        return Math.sqrt(Math.pow((position.getX() - other.getX()), 2) + Math.pow((position.getY() - other.getY()), 2));
    }


    /**
     * Displaces this object by the provided vector
     *
     * @param displacement vector indicating displacement
     */
    public void moveBy(Vector2D displacement) {
        position.setX(displacement.getX() + position.getX());
        position.setY(displacement.getY() + position.getY());
    }

    /**
     * Adds the acceleration to the speed.
     */
    public void transferForce() {

        velocity.add(acceleration.getX() / mass, acceleration.getY() / mass);

        moveBy(velocity);

        setAcceleration(0, 0);
    }

    /**
     * Accelerates the object
     *
     * @param x acceleration in the x-axis
     * @param y acceleration in the y-axis
     */
    public void addAcceleration(double x, double y) {
        acceleration.add(x, y);
    }

    /**
     * Accelerates the object
     *
     * @param other the vector whose velocity gets applied to this one
     */
    public void addAcceleration(Vector2D other) {
        acceleration.add(other);
    }

    /**
     * Applies friction to the velocity
     */
    public void dampen() {
        velocity.setX(velocity.getX() * Physics2DObject.FRICTION);
        velocity.setY(velocity.getY() * Physics2DObject.FRICTION);

    }

    // Getters, setters

    public void setPosition(double x, double y) {
        position.setX(x);
        position.setY(y);
    }

    public void setAcceleration(double x, double y) {
        acceleration.set(x, y);
    }

    public double getX() {
        return position.getX();
    }

    public void setX(double x) {
        position.setX(x);
    }

    public double getY() {
        return position.getY();
    }

    public void setY(double y) {
        position.setY(y);
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D newPosition) {
        position = newPosition;
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public double getMass() {
        return mass;
    }


}
