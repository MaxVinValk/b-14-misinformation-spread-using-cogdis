package com.b14.view.popup_menus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * A frame with a link to the repository as well as a little background information
 */
public class InfoFrame extends JFrame {

    /**
     * Creates and pops up the frame
     */
    public InfoFrame() {
        super("About");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(640, 256));

        this.getContentPane().add(new InfoPanel(), BorderLayout.CENTER);

        setupFrame();
    }

    /**
     * Perform some setup on the frame itself
     */
    private void setupFrame() {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * inner class as the panel and frame are very small, so to reduce the amount of files that are needed.
     */
    static class InfoPanel extends JPanel {
        public InfoPanel() {
            super(new BorderLayout());

            JTextArea textArea = new JTextArea(10, 80);
            textArea.setEditable(false);
            textArea.append("This application was created in order to perform research on the formation of\n" +
                    "echo chambers in networks of nodes that can spread their belief, and the influence\n" +
                    "of different new connection recommendations on this formation. The colours for each mode\n" +
                    "are explained briefly by hovering over the colour selection tabs inside the colour picker,\n" +
                    "Which can be found in the view menu. For more extensive information about what this project\n" +
                    "is trying to do and how to use it, see the GitHub repository, which both includes a manual on\n" +
                    "how to use this software, and the research that was conducted using this simulation.\n\n" +
                    "The button below will take you there."
            );

            add(textArea, BorderLayout.LINE_START);

            JButton gotoPageButton = new JButton(new GotoPageAction());
            add(gotoPageButton, BorderLayout.PAGE_END);
        }

        /**
         * The action for going to the GitHub repository, also as anonymous inner class
         */
        static class GotoPageAction extends AbstractAction {
            public GotoPageAction() {
                super("More info");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop desktop = java.awt.Desktop.getDesktop();
                try {
                    URI oURL = new URI("https://github.com/MaxVinValk/b-14-misinformation-spread-using-cogdis");
                    desktop.browse(oURL);
                } catch (URISyntaxException | IOException uriSyntaxException) {

                    String message = "The repository can be found at: \n" +
                            "https://github.com/MaxVinValk/b-14-misinformation-spread-using-cogdis";

                    JOptionPane.showMessageDialog(null, message, "Could not open browser",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        }

    }

}