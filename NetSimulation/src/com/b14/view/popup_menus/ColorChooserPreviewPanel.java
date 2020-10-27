package com.b14.view.popup_menus;

import com.b14.view.GraphPanel;

import javax.swing.*;
import java.awt.*;

public class ColorChooserPreviewPanel extends JPanel {

    private final GraphPanel panel;

    public ColorChooserPreviewPanel(GraphPanel panel) {
        this.panel = panel;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawSelf(g);
    }

    private void drawSelf(Graphics g) {
        setBackground(panel.getBackgroundColor());
        g.setFont(getFont().deriveFont(15.0f));

        g.setColor(Color.BLACK);
        g.drawString("Zero belief", 25, 55);
        g.drawString("Conflict edge", 25, 130);
        g.drawString("One belief", 25, 205);

        g.setColor(panel.getConflictingEdgeColor());
        g.drawLine(175, 50, 175, 200);

        g.setColor(panel.getZeroBeliefColor());
        g.fillOval(150, 25, 50, 50);

        g.setColor(panel.getOneBeliefColor());
        g.fillOval(150, 175, 50, 50);

        g.setColor(Color.BLACK);
        g.drawString("Distress", 403, 93);
        g.drawString("No distress", 403, 168);

        g.setColor(panel.getDistressColor());
        g.fillOval(528, 60, 50, 50);

        g.setColor(panel.getNoDistressColor());
        g.fillOval(528, 135, 50, 50);


    }
}
