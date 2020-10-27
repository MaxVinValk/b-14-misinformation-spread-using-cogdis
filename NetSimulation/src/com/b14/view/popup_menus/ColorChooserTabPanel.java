package com.b14.view.popup_menus;

import com.b14.view.GraphPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * The panel for colour selection
 */

public class ColorChooserTabPanel extends JPanel implements ChangeListener {

    private final JTabbedPane tabbedPane;
    private final ColorChooserPreviewPanel previewPanel;
    private final GraphPanel panel;

    /**
     * Sets up the panels for colour picking, as well as the preview panel
     *
     * @param panel the graphPanel the colours are picked for
     */
    public ColorChooserTabPanel(GraphPanel panel) {
        super(new GridLayout(2, 1));

        this.panel = panel;

        tabbedPane = new JTabbedPane();

        //Create the panels
        ColorChooserPanel zbc = new ColorChooserPanel(panel.getZeroBeliefColor(), 0);
        ColorChooserPanel obc = new ColorChooserPanel(panel.getOneBeliefColor(), 1);
        ColorChooserPanel cec = new ColorChooserPanel(panel.getConflictingEdgeColor(), 2);
        ColorChooserPanel dc = new ColorChooserPanel(panel.getDistressColor(), 3);
        ColorChooserPanel ndc = new ColorChooserPanel(panel.getNoDistressColor(), 4);
        ColorChooserPanel bc = new ColorChooserPanel(panel.getBackgroundColor(), 5);

        //Start listening to them
        zbc.getColorChooser().getSelectionModel().addChangeListener(this);
        obc.getColorChooser().getSelectionModel().addChangeListener(this);
        cec.getColorChooser().getSelectionModel().addChangeListener(this);
        dc.getColorChooser().getSelectionModel().addChangeListener(this);
        ndc.getColorChooser().getSelectionModel().addChangeListener(this);
        bc.getColorChooser().getSelectionModel().addChangeListener(this);

        //Add them to the tabbedPane
        tabbedPane.addTab("Zero Belief", null, zbc, "The colour for a node/edge when the belief is 0");
        tabbedPane.addTab("One Belief", null, obc, "The colour for a node/edge when the belief is 1");
        tabbedPane.addTab("Conflict edge", null, cec, "The colour for an edge when the nodes that it connects differ in side from 0.5");
        tabbedPane.addTab("Distress", null, dc, "The colour for a node when dissonance is drawn, when a node experiences dissonance");
        tabbedPane.addTab("No Distress", null, ndc, "The colour for a node when dissonance is drawn, when a node experiences no dissonance");
        tabbedPane.addTab("Background", null, bc, "The colour for the background");

        add(tabbedPane);
        previewPanel = new ColorChooserPreviewPanel(panel);

        add(previewPanel);
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        if (tabbedPane.getSelectedComponent() instanceof ColorChooserPanel) {
            ColorChooserPanel selected = (ColorChooserPanel) tabbedPane.getSelectedComponent();

            Color newColor = selected.getColorChooser().getSelectionModel().getSelectedColor();

            //Not the most elegant, but does the job
            switch (selected.getID()) {
                case 0:
                    panel.setZeroBeliefColor(newColor);
                    break;
                case 1:
                    panel.setOneBeliefColor(newColor);
                    break;
                case 2:
                    panel.setConflictingEdgeColor(newColor);
                    break;
                case 3:
                    panel.setDistressColor(newColor);
                    break;
                case 4:
                    panel.setNoDistressColor(newColor);
                    break;
                case 5:
                    panel.setBackgroundColor(newColor);
                    break;
            }

            previewPanel.repaint();
            panel.repaint();
        }

    }
}
