package com.b14.view.popup_menus;

import com.b14.view.GraphPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Creates a frame with the colour picker and preview window
 */
public class ColorChooserFrame extends JFrame {

    /**
     * Sets up the frame, and opens it up
     *
     * @param panel the GraphPanel that the colors will be picked for
     */
    public ColorChooserFrame(GraphPanel panel) {
        super("Colour selector");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(640, 512));

        this.getContentPane().add(new ColorChooserTabPanel(panel), BorderLayout.CENTER);

        setupFrame();
    }

    /**
     * Run all setup needed to start displaying
     */
    private void setupFrame() {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
