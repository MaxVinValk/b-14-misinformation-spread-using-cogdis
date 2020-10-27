package com.b14.view.popup_menus;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ColorChooserPanel extends JPanel {

    private JColorChooser tcc;
    private int id;

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
