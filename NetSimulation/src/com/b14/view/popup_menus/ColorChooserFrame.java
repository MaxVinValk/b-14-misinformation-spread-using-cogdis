package com.b14.view.popup_menus;

import com.b14.view.GraphPanel;

import javax.swing.*;
import java.awt.*;

public class ColorChooserFrame extends JFrame {

    public ColorChooserFrame(GraphPanel panel) {
        super("Colour selector");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(640, 512));

        this.getContentPane().add(new ColorChooserTabPanel(panel), BorderLayout.CENTER);

        setupGraph();
    }

    /**
     *  Run all setup needed to start displaying
     */
    public void setupGraph() {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
