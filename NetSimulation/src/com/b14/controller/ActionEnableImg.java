package com.b14.controller;

import com.b14.model.DataLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ActionEnableImg extends AbstractAction {

    private DataLogger dataLogger;

    public ActionEnableImg(DataLogger dataLogger) {
        super("Enable/configure image output");
        this.dataLogger = dataLogger;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        int num;
        do {
            try {
                num = Integer.parseInt(JOptionPane.showInputDialog("How often should a frame be captured? (>= 1)"));
            } catch (NumberFormatException e) {
                num = -1;
            }

            if (num < 1) {
                num = -1;
            }
        } while (num == -1);

        dataLogger.setImgsGenerated(true);
        dataLogger.setCaptureTimer(num);
    }
}
