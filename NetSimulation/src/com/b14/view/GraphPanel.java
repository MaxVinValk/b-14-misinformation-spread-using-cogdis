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
 * The panel is responsible for actually drawing the network
 * on the screen.
 */

public class GraphPanel extends JPanel implements PropertyChangeListener {

    //Menu Background Color
    private static final Color GRAY_TRANS = new Color(141, 141, 141, 230);

    //Default colours
    private static final Color DEFAULT_CONFLICTING_EDGE_COLOR = Color.BLACK;
    private static final Color DEFAULT_ZERO_BELIEF_COLOR = Color.BLUE;
    private static final Color DEFAULT_ONE_BELIEF_COLOR = Color.RED;
    private static final Color DEFAULT_DISTRESS_COLOR = Color.GREEN;
    private static final Color DEFAULT_NO_DISTRESS_COLOR = Color.BLACK;
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private final Random random = new Random(0);

    // Colours used to determine node colour
    //Edge Colour
    private Color conflictingEdgeColor;
    //Colours for showing belief
    private Color zeroBeliefColor;
    private Color oneBeliefColor;
    //Colours for showing distress
    private Color distressColor;
    private Color noDistressColor;
    private Color backgroundColor;
    private final GraphModel model;
    private final Camera camera;
    private InputController controller = null;
    private boolean headlessMode = true;
    private boolean drawBelief = true;
    private boolean drawNodeIDs = false;

    private final PropertyChangeSupport pcs;


    /**
     * Creates a new graph panel
     *
     * @param model  The model that has to be displayed
     * @param camera The camera that decides what area to show
     */
    public GraphPanel(GraphModel model, Camera camera) {
        this.model = model;
        this.camera = camera;

        resetColors();

        pcs = new PropertyChangeSupport(this);
    }

    public void resetColors() {
        conflictingEdgeColor = DEFAULT_CONFLICTING_EDGE_COLOR;
        zeroBeliefColor = DEFAULT_ZERO_BELIEF_COLOR;
        oneBeliefColor = DEFAULT_ONE_BELIEF_COLOR;
        distressColor = DEFAULT_DISTRESS_COLOR;
        noDistressColor = DEFAULT_NO_DISTRESS_COLOR;
        backgroundColor = DEFAULT_BACKGROUND_COLOR;
    }

    public void addInputController(InputController controller) {
        this.controller = controller;
    }

    /**
     * Performs all drawing of graphics done
     *
     * @param g A Graphics object used for drawing in java swing.
     */
    private void drawSelf(Graphics g) {
        if (headlessMode) {
            drawHeadless(g);
        } else {
            drawNetwork(g);
        }
    }

    /**
     * Draws the placeholder text for headless mode
     *
     * @param g
     */
    private void drawHeadless(Graphics g) {
        setBackground(Color.WHITE);

        Font font = getFont().deriveFont(30.0f);
        g.setFont(font);
        g.setColor(Color.BLACK);

        g.drawString("Running in headless mode\n", 50, 50);

        font = getFont().deriveFont(40.0f);
        g.setFont(font);

        String[] robot = {"       .- - -.     ",
                "     } n n {    ",
                "      \\_ - _/    ",
                "      /| []|\\    ",
                "   ()/|___|\\()  ",
                "       /| |\\     ",
                "     (0) (0)"};

        int y = 100;

        for (String line : robot) {
            g.drawString(line, 650, y);
            y += 50;
        }

        font = getFont().deriveFont(20.0f);
        g.setFont(font);
        g.drawString("Running a model with   " + model.getNodes().size() + " nodes", 60, 100);
        g.drawString("Current model epoch:   " + model.getEpoch(), 60, 125);

        g.drawString("Using recommendation strategy:   " + model.getRecommendationStrategy(), 60, 175);
        g.drawString("With recommendation set size:      " + model.getRecommendationSize(), 60, 200);

    }


    /**
     * Draws the entire network
     *
     * @param g A Graphics object used for drawing in java swing.
     */
    private void drawNetwork(Graphics g) {
        setBackground(backgroundColor);

        assert (controller != null) : "Panel tried to draw wihout an initialized controller reference";


        ArrayList<Node> visible = new ArrayList<>();

        Node selected = controller.getSelectedNode();
        int lastClicked = controller.getLastClicked();

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
     *
     * @param g            A Graphics object used for drawing in java swing.
     * @param visibleNodes All the nodes that are currently visible
     * @param selected     Give the value of a node if there is a selected node, null otherwise
     */

    private void drawEdges(Graphics g, ArrayList<Node> visibleNodes, Node selected) {
        g.setColor(Color.BLACK);

        for (Node n : visibleNodes) {
            int x1 = (int) ((n.getX() - camera.getX()) * camera.getScale());
            int y1 = (int) ((n.getY() - camera.getY()) * camera.getScale());

            for (Node n2 : n.getNeighbours()) {

                /*if ((n.getBelief() > 0.5) && (n2.getBelief() > 0.5)) {
                    g.setColor((selected == null) ? oneBeliefColor : getTransparent(oneBeliefColor));
                } else if ((n.getBelief() < 0.5) && (n2.getBelief() < 0.5)) {
                    g.setColor((selected == null) ? zeroBeliefColor : getTransparent(zeroBeliefColor));
                } else {
                    g.setColor((selected == null) ? conflictingEdgeColor : getTransparent(conflictingEdgeColor));
                }*/

                float avgBelief = (n.getBelief() + n2.getBelief()) / 2;
                g.setColor(Node.getColorBelief(zeroBeliefColor, oneBeliefColor, avgBelief, selected != null));

                int x2 = (int) ((n2.getX() - camera.getX()) * camera.getScale());
                int y2 = (int) ((n2.getY() - camera.getY()) * camera.getScale());
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }

    /**
     * Draws in all nodes that are visible
     *
     * @param g            A Graphics object used for drawing in java swing.
     * @param visibleNodes All the nodes that are currently visible
     * @param selected     Give the value of a node if there is a selected node, null otherwise
     */

    private void drawNodes(Graphics g, ArrayList<Node> visibleNodes, Node selected) {
        Font font = getFont().deriveFont(15.0f * camera.getScale());
        g.setFont(font);

        for (Node n : visibleNodes) {

            int screenX = (int) ((n.getX() - camera.getX()) * camera.getScale());
            int screenY = (int) ((n.getY() - camera.getY()) * camera.getScale());

            int size = (int) (n.getSize() * camera.getScale());

            if (drawBelief) {
                g.setColor(n.getColorBelief(zeroBeliefColor, oneBeliefColor, selected != null));
            } else {
                g.setColor(n.getColorDissonance(distressColor, noDistressColor, selected != null));
            }

            if (n == selected) {
                g.setColor(Color.BLACK);
            }

            g.fillOval((screenX - size / 2), (screenY - size / 2), size, size);

            if (drawNodeIDs) {
                g.setColor(Color.WHITE);
                g.drawString("" + n.getId(), (int) (screenX - 5 * camera.getScale()),
                        (int) (screenY + 5 * camera.getScale()));
            }
        }
    }

    /**
     * Get all nodes within the camera view
     *
     * @return all the nodes within the camera view
     */
    private ArrayList<Node> getVisibleNodes() {

        ArrayList<Node> visible = new ArrayList<>();

        for (Node n : model.getNodes()) {

            int size = (int) (n.getSize() * camera.getScale());

            Vector2D pos = n.getPosition();

            if ((pos.getX() >= (camera.getX() - size) && pos.getX() <= (camera.getWidthScaled() + camera.getX() + size)) &&
                    (pos.getY() >= (camera.getY() - size) && camera.getY() <= (camera.getHeightScaled() + camera.getY() + size))) {
                visible.add(n);
            }
        }

        return visible;
    }

    /**
     * Draws an information panel on the screen in the top-right corner, with the message that is provided
     *
     * @param g       The graphics objec to draw on
     * @param message The message to be displayed
     */
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

        int x = getWidth() - widthSpacing * mostChars - 3 * bevel;
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
                case "modelChange":
                case "recommendSettingsChange":
                    this.repaint();
                    break;
            }
        } else {
            switch (name) {
                case "physicsUpdate":
                case "cameraChange":
                case "modelChange":
                case "nodeSelected":
                    this.repaint();
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    //util
    private Color getTransparent(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 127);
    }

    /*
            From hereon we only have toggles, setters and getters
     */

    public void toggleHeadless() {
        setHeadless(!headlessMode);
    }

    public void toggleDrawIDs() {
        drawNodeIDs = !drawNodeIDs;
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

    public void setHeadless(boolean val) {
        headlessMode = val;
        repaint();
    }

    public boolean isDrawingBelief() {
        return drawBelief;
    }

    public Color getConflictingEdgeColor() {
        return conflictingEdgeColor;
    }

    public void setConflictingEdgeColor(Color c) {
        conflictingEdgeColor = c;
    }

    public Color getZeroBeliefColor() {
        return zeroBeliefColor;
    }

    public void setZeroBeliefColor(Color c) {
        zeroBeliefColor = c;
    }

    public Color getOneBeliefColor() {
        return oneBeliefColor;
    }

    public void setOneBeliefColor(Color c) {
        oneBeliefColor = c;
    }

    public Color getDistressColor() {
        return distressColor;
    }

    public void setDistressColor(Color c) {
        distressColor = c;
    }

    public Color getNoDistressColor() {
        return noDistressColor;
    }

    public void setNoDistressColor(Color c) {
        noDistressColor = c;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color c) {
        backgroundColor = c;
    }
}
