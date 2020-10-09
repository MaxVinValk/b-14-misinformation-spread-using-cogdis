package com.b14.controller;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FileChooser extends JFileChooser {

    public FileChooser(int dialogType) {
        setAcceptAllFileFilterUsed(false);
        setDialogType(dialogType);
        setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File file) {
                // TODO Auto-generated method stub
                return (file.getName().toUpperCase().endsWith(".CSVAGENTS") || file.isDirectory());
            }

            @Override
            public String getDescription() {
                return ".csvagents";
            }
        });
    }

    @Override
    public void approveSelection() {
        if (!getSelectedFile().getAbsolutePath().toUpperCase().endsWith(".CSVAGENTS")) {

            if (getSelectedFile().getAbsolutePath().contains(".")) {
                JOptionPane.showMessageDialog(this, "File names must end with .csvagents!", "Invalid file name", JOptionPane.ERROR_MESSAGE);
                return;
            }
            setSelectedFile(new File(getSelectedFile() + ".csvagents"));
        }

        File f = getSelectedFile();
        super.approveSelection();
    }
}
