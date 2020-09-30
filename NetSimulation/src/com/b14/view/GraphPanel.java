package com.b14.view;

import com.b14.controller.InputController;
import com.b14.model.GraphModel;
import com.b14.model.Node;
import com.b14.model.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Random;

/**
 *  The panel is responsible for actually drawing the network
 *  on the screen.
 */

public class GraphPanel extends JPanel implements PropertyChangeListener {

    //Bunch of constant colors, used for the edges
    private static final Color RED_TRANS = new Color(255, 0, 0, 127);
    private static final Color BLUE_TRANS = new Color(0, 0, 255, 127);
    private static final Color BLACK_TRANS = new Color(0, 0, 0, 127);
    private static final Color GRAY_TRANS = new Color(141, 141, 141, 139);


    //TODO: Let the colour vary in gradient by belief


    private GraphModel model;
    private Camera camera;
    private InputController controller = null;

    private final Random random = new Random(0);

    private boolean headlessMode = true;
    private boolean drawBelief = true;
    private boolean drawNodeIDs = false;

    private PropertyChangeSupport pcs;


    /**
     * Creates a new graph panel
     * @param model     The model that has to be displayed
     * @param camera    The camera that decides what area to show
     */
    public GraphPanel(GraphModel model, Camera camera) {
        this.model = model;
        this.camera = camera;

        pcs = new PropertyChangeSupport(this);
    }

    public void addInputController(InputController controller) {
        this.controller = controller;
    }

    /**
     * Performs all drawing of graphics done
     * @param g A Graphics object used for drawing in java swing.
     */
    private void drawSelf(Graphics g) {
        setBackground(Color.WHITE);

        if (headlessMode) {
            drawHeadless(g);
        } else {
            drawNetwork(g);
        }


    }

    /**
     * Draws the placeholder text for headless mode
     * @param g
     */
    private void drawHeadless(Graphics g) {
        Font font = getFont().deriveFont(30.0f);
        g.setFont(font);
        g.setColor(Color.BLACK);

        g.drawString("Running in headless mode :)\n", 50, 50);

        font = getFont().deriveFont(40.0f);
        g.setFont(font);

        String[] robot =  { "       .- - -.     ",
                            "     } n n {    ",
                            "      \\_ - _/    ",
                            "      /| []|\\    ",
                            "   ()/|___|\\()  ",
                            "       /| |\\     ",
                            "     (0) (0)"};

        int y = 100;

        for (String line : robot) {
            g.drawString(line, 120, y);
            y += 50;
        }

        font = getFont().deriveFont(20.0f);
        g.setFont(font);
        g.drawString("Running a model with " + model.getNodes().size() + " nodes", 600, 50);
        g.drawString("Current model epoch:   " + model.getEpoch(), 600, 75);

    }


    /**
     * Draws the entire network
     * @param g A Graphics object used for drawing in java swing.
     */
    private void drawNetwork(Graphics g) {
        // Retrieve all nodes that are in the camera area:
        ArrayList<Node> visible = new ArrayList<>();

        Node selected = null;
        int lastClicked = -1;

        //Retrieve which, if any, node is clicked
        if (controller != null) {
            selected = controller.getSelectedNode();
            lastClicked = controller.getLastClicked();
        }

        if (selected != null && lastClicked == MouseEvent.BUTTON3) {
            visible.add(selected);
        } else {
            visible = getVisibleNodes();
        }

        drawEdges(g, visible, selected);

        if (selected != null && lastClicked == MouseEvent.BUTTON3) {
            visible.addAll(selected.getNeighbours());
        }

        drawNodes(g, visible, selected);

        if (selected != null) {
            drawInfoPanel(g, selected.toString());
        }

    }


    /**
     * Draws in all edges that originate from a visible node
     * @param g             A Graphics object used for drawing in java swing.
     * @param visibleNodes  All the nodes that are currently visible
     * @param selected      Give the value of a node if there is a selected node, null otherwise
     */

    private void drawEdges(Graphics g, ArrayList<Node> visibleNodes, Node selected) {
        g.setColor(Color.BLACK);

        for (Node n : visibleNodes) {
            int x1 = (int)((n.getX() - camera.getX()) * camera.getScale());
            int y1 = (int)((n.getY() - camera.getY()) * camera.getScale());

            for (Node n2 : n.getNeighbours()) {

                if ((n.getBelief() > 0.5) && (n2.getBelief() > 0.5)) {
                    g.setColor((selected == null ? Color.RED : GraphPanel.RED_TRANS));
                } else if ((n.getBelief() < 0.5) && (n2.getBelief() < 0.5)) {
                    g.setColor((selected == null ? Color.BLUE : GraphPanel.BLUE_TRANS));
                } else {
                    g.setColor((selected == null ? Color.BLACK : GraphPanel.BLACK_TRANS));
                }

                int x2 = (int)((n2.getX() - camera.getX()) * camera.getScale());
                int y2 = (int)((n2.getY() - camera.getY()) * camera.getScale());
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }

    /**
     * Draws in all nodes that are visible
     * @param g             A Graphics object used for drawing in java swing.
     * @param visibleNodes  All the nodes that are currently visible
     * @param selected      Give the value of a node if there is a selected node, null otherwise
     */

    private void drawNodes(Graphics g, ArrayList<Node> visibleNodes, Node selected) {
        //nodes
        Font font = getFont().deriveFont(15.0f * camera.getScale());
        g.setFont(font);

        for (Node n : visibleNodes) {

            int screenX = (int)((n.getX() - camera.getX()) * camera.getScale());
            int screenY = (int)((n.getY() - camera.getY()) * camera.getScale());

            int size = (int)(30 * camera.getScale());

            if (drawBelief) {
                g.setColor(n.getColorBelief(selected != null));
            } else {
                g.setColor(n.getColorDissonance(selected != null));
            }


            if (n == selected) {
                g.setColor(Color.BLACK);
            }

            g.fillOval((int)(screenX - size/2), (int)(screenY - size / 2), size, size);

            if (drawNodeIDs) {
                g.setColor(Color.WHITE);
                g.drawString(new String("" + n.getId()), (int) (screenX - 5 * camera.getScale()),
                        (int) (screenY + 5 * camera.getScale()));
            }
        }
    }

    /**
     * Get all nodes within the camera view
     * @return all the nodes within the camera view
     */
    private ArrayList<Node> getVisibleNodes() {

        ArrayList<Node> visible = new ArrayList<>();

        for (Node n : model.getNodes()) {

            int size = (int)(n.getSize() * camera.getScale());

            Vector2D pos = n.getPosition();

            if ((pos.getX() >= (camera.getX() - size) && pos.getX() <= (camera.getWidthScaled() + camera.getX() + size)) &&
                    (pos.getY() >= (camera.getY() - size) && camera.getY() <= (camera.getHeightScaled() + camera.getY() + size))) {
                visible.add(n);
            }
        }

        return visible;
    }

    private void drawInfoPanel(Graphics g, String message) {
        int widthSpacing = 10;
        int heightSpacing = 26;
        int bevel = 30;

        String[] lines = message.split("\n");

        //get info on longest width:
        int mostChars = -1;
        for (String line : lines) {
            if (line.length() > mostChars) {
                mostChars = line.length();
            }
        }

        int x = getWidth() - widthSpacing * mostChars - 3*bevel;
        int y = bevel;

        //draw background box:
        g.setColor(GraphPanel.GRAY_TRANS);
        g.fillRect(x, y, widthSpacing * mostChars + 2 * bevel, heightSpacing * lines.length + bevel);

        g.setColor(Color.BLACK);
        g.drawRect(x, y, widthSpacing * mostChars + 2 * bevel, heightSpacing * lines.length + bevel);

        g.setFont(getFont().deriveFont(22.0f));
        for (int i = 0; i < lines.length; i++) {
            g.drawString(lines[i], x + bevel, y + bevel + i * heightSpacing);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawSelf(g);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();

        if (headlessMode) {
            switch (name) {
                case "modelChange": this.repaint();
                break;
            }
        } else {
            switch (name) {
                case "physicsUpdate", "cameraChange", "modelChange", "nodeSelected": this.repaint();
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    public void toggleHeadless() {
        setHeadless(!headlessMode);
    }

    public void toggleDrawIDs() {
        drawNodeIDs = !drawNodeIDs;
        repaint();
    }

    public void setHeadless(boolean val) {
        headlessMode = val;
        repaint();
    }

    public void setDrawBelief(boolean val) {
        boolean oldVal = drawBelief;
        drawBelief = val;
        repaint();
        pcs.firePropertyChange(new PropertyChangeEvent(this, "changedDrawBelief", oldVal, drawBelief));
    }

    public boolean isHeadless() {
        return headlessMode;
    }

    public boolean isDrawingBelief() {
        return drawBelief;
    }
}
