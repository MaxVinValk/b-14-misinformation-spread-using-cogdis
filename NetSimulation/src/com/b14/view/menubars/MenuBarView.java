package com.b14.view.menubars;

import com.b14.controller.actions.*;
import com.b14.model.GraphModel;
import com.b14.view.Camera;
import com.b14.view.GraphPanel;

import javax.swing.*;

/**
 * Implements a menu bar that holds view controls
 */

public class MenuBarView extends JMenu {

    /**
     * @param model  The main model used in the simulation
     * @param camera The camera that is used to determine what is in view (and what not)
     */
    public MenuBarView(GraphModel model, Camera camera, GraphPanel panel) {
        super("View");

        add(new JMenuItem(new ActionToggleHeadless(panel)));

        addSeparator();

        add(new JMenuItem(new ActionToggleLegend(panel)));

        addSeparator();

        ActionShowBelief asb = new ActionShowBelief(panel);
        ActionShowDissonance asd = new ActionShowDissonance(panel);

        panel.addPropertyChangeListener(asb);
        panel.addPropertyChangeListener(asd);

        add(new JMenuItem(asb));
        add(new JMenuItem(asd));

        addSeparator();
        add(new JMenuItem(new ActionResetZoom(camera)));
        add(new JMenuItem(new ActionCameraToNode(model, camera)));

        addSeparator();
        add(new JMenuItem(new ActionLaunchColorMenu(panel)));
        add(new JMenuItem(new ActionResetColors(panel)));

    }
}
