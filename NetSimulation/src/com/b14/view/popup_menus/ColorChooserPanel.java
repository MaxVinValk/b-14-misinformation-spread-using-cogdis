package com.b14.view.popup_menus;

import javax.swing.*;
import java.awt.*;

/**
 * The panel which contains 1 colour picker menu
 */

public class ColorChooserPanel extends JPanel {

    private final JColorChooser tcc;
    private final int id;

    /**
     * Sets up 1 colour picker
     *
     * @param selectedColour The initially selected colour
     * @param id             An ID number to track which colour is being set with this picker
     */
    public ColorChooserPanel(Color selectedColour, int id) {
        super(new BorderLayout());
        this.id = id;

        tcc = new JColorChooser(selectedColour);
        tcc.setPreviewPanel(new JPanel()); // Removes the default preview panel

        tcc.setBorder(BorderFactory.createTitledBorder("Choose colour"));

        add(tcc, BorderLayout.CENTER);


    }

    public JColorChooser getColorChooser() {
        return tcc;
    }

    public int getID() {
        return id;
    }
}
