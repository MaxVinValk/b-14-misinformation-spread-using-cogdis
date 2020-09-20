package com.b14.view;

import com.b14.model.GraphModel;
import com.b14.model.Node;
import com.b14.model.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 *  The panel is responsible for actually drawing the network
 *  on the screen.
 */

public class GraphPanel extends JPanel {

    private GraphModel model;
    private Camera camera;

    private final Random random = new Random(0);

    private boolean headlessMode = true;

    /**
     * Creates a new graph panel
     * @param model     The model that has to be displayed
     * @param camera    The camera that decides what area to show
     */
    public GraphPanel(GraphModel model, Camera camera) {
        this.model = model;
        this.camera = camera;
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
        Font font = getFont().deriveFont(30.0f*camera.getScale());
        g.setFont(font);
        g.setColor(Color.BLACK);

        g.drawString("Running in headless mode :)\n", (int)(100 - camera.getX()), (int)(50 - camera.getY()));

        font = getFont().deriveFont(40.0f * camera.getScale());
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
            g.drawString(line, (int)(200 - camera.getX()), (int)(y - camera.getY()));
            y += 50 * camera.getScale();
        }
    }


    /**
     * Draws the entire network
     * @param g A Graphics object used for drawing in java swing.
     */
    private void drawNetwork(Graphics g) {
        // Retrieve all nodes that are in the camera area:
        ArrayList<Node> visible = getVisibleNodes();

        drawEdges(g, visible);
        drawNodes(g, visible);
    }


    /**
     * Draws in all edges that originate from a visible node
     * @param g             A Graphics object used for drawing in java swing.
     * @param visibleNodes  All the nodes that are currently visible
     */

    private void drawEdges(Graphics g, ArrayList<Node> visibleNodes) {
        g.setColor(Color.BLACK);

        for (Node n : visibleNodes) {
            int x1 = (int)((n.getX() - camera.getX()) * camera.getScale());
            int y1 = (int)((n.getY() - camera.getY()) * camera.getScale());

            for (Node n2 : n.getNeighbours()) {

                if ((n.getBelief() > 0.5) && (n2.getBelief() > 0.5)) {
                    g.setColor(Color.RED);
                } else if ((n.getBelief() < 0.5) && (n2.getBelief() < 0.5)) {
                    g.setColor(Color.BLUE);
                } else {
                    g.setColor(Color.BLACK);
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
     */

    private void drawNodes(Graphics g, ArrayList<Node> visibleNodes) {
        //nodes
        Font font = getFont().deriveFont(15.0f * camera.getScale());
        g.setFont(font);

        for (Node n : visibleNodes) {

            int screenX = (int)((n.getX() - camera.getX()) * camera.getScale());
            int screenY = (int)((n.getY() - camera.getY()) * camera.getScale());

            int size = (int)(30 * camera.getScale());

            // Coloring based on dissonance and belief.
            float[] dissonance = n.getDissonance();
            if (n.getBelief() > 0.5) {
                if(dissonance[0] > dissonance[1]) {
                    g.setColor(Color.PINK);
                } else {
                    g.setColor(Color.RED);
                }
            } else {
                if(dissonance[0] > dissonance[1]) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.BLUE);
                }
            }

            g.fillOval((int)(screenX - size/2), (int)(screenY - size / 2), size, size);
            g.setColor(Color.WHITE);
            g.drawString(new String("" + n.getId()), (int)(screenX - 5 * camera.getScale()) ,
                    (int)(screenY + 5 * camera.getScale()));
        }
    }

    /**
     * Get all nodes within the camera view
     * @return all the nodes within the camera view
     */
    private ArrayList<Node> getVisibleNodes() {

        int size = (int)(30 * camera.getScale()); //TODO: Make this dependent on the node itself

        ArrayList<Node> visible = new ArrayList<>();

        for (Node n : model.getNodes()) {
            Vector2D pos = n.getPosition();

            if ((pos.getX() >= (camera.getX() - size) && pos.getX() <= (camera.getWidthScaled() + camera.getX() + size)) &&
                    (pos.getY() >= (camera.getY() - size) && camera.getY() <= (camera.getHeightScaled() + camera.getY() + size))) {
                visible.add(n);
            }
        }

        return visible;
    }

    public void toggleHeadless() {
        headlessMode = !headlessMode;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawSelf(g);
    }

}
