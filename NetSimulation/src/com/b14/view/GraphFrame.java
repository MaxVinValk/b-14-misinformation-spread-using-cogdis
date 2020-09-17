package com.b14.view;

import com.b14.model.GraphModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * The frame is responsible for the window on which the panel is drawn.
 */

public class GraphFrame extends JFrame {


    /*
        This internal class is used to resize the camera when the window itself is resized
     */
    class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) { resize();}
    }

    //The function that is called on window-resize
    private void resize() {
        camera.setSize(this.getSize().width, this.getSize().height);
    }


    //Start of actual class

    private GraphPanel panel;
    private Camera camera;

    /**
     * Creates a new graph frame.
     *
     * @param panelName     Name of the panel
     * @param preferredSize Initial window size
     * @param model         Model that should be displayed
     * @param camera        Camera that tracks what is visible
     */
    public GraphFrame(String panelName, Dimension preferredSize, GraphModel model, Camera camera) {
        super(panelName);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(preferredSize);

        this.camera = camera;

        this.panel = new GraphPanel(model, camera);
        this.getContentPane().add(this.panel);

        this.addComponentListener(new ResizeListener());
    }

    /**
     *  Run all setup needed to start displaying
     */
    public void setupGraph() {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     *
     * @return the panel that is attached to the frame
     */
    public GraphPanel getPanel() {
        return panel;
    }


}
