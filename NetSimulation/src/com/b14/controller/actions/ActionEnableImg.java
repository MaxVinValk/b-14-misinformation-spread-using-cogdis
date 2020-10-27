package com.b14.controller.actions;

import com.b14.model.DataLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ActionEnableImg extends AbstractAction {

    private final DataLogger dataLogger;

    public ActionEnableImg(DataLogger dataLogger) {
        super("Enable/configure image output");
        this.dataLogger = dataLogger;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        int num;
        String input;

        do {
            try {
                input = JOptionPane.showInputDialog("How often should a frame be captured? (>= 1)", 100);

                if (input == null) {
                    return;
                }

                num = Integer.parseInt(input);
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
