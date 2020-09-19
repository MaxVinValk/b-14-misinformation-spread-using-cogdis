package com.b14;

import com.b14.model.ModelManager;

/*
 *  TODO: there is the issue that sometimes nodes can overlap. This causes a calculation of the unit vector which results
 *          in a 0-division, which gives us NaN. That will cause it to appear that nodes disappear to the top-left corner.
 */

public class Main {

    public static final String VERSION = "Version 0.5";

    public static void main(String[] args) {
        ModelManager manager = new ModelManager(2); // change this to 1 to get the original version!
        manager.runSimulation();

    }
}
