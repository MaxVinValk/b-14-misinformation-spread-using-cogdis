package com.b14.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Class responsible for all datalogging
 */

public class DataLogger {

    private final static String ROOT_FOLDER = "data_out/";
    private File currentOutput = null;
    private boolean allowOutput = true;

    private GraphModel model;

    private String headers =    "epoch,nodeID,belief,disLeftToThreshold,dissonance,numNeighbours,avgNeighbourBelief," +
                                "numConfidants,avgConfidantBelief,numberOfContacts,numberOfConflicts\n";

    public DataLogger(GraphModel model) {
        this.model = model;

        File rootFolder = new File(ROOT_FOLDER);

        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
    }

    /**
     * Creates a new document, and a new logging session, only if output is allowed
     */

    public void startNewSession() {

        if (!allowOutput) {
            return;
        }

        DateFormat df = new SimpleDateFormat("MM_dd_HH_mm");


        currentOutput = new File(ROOT_FOLDER, df.format(Calendar.getInstance().getTime()) + ".csv");

        try {
            FileWriter fw = new FileWriter(currentOutput);
            fw.write(headers);
            fw.close();
        } catch (IOException e) {
            System.out.println("Failed to write headers to output file!");
        }
    }

    /**
     * Takes the data currently in the model and logs data of relevance
     * @param epoch the epoch this information belongs to.
     */
    public void logData(int epoch) {

        if (!allowOutput) {
            return;
        }

        if (currentOutput == null) {
            System.out.println("Attempted a log WITHOUT a created log file. Data is lost!");
            return;
        }

        try {
            FileWriter fw = new FileWriter(currentOutput, true);

            for (Node n : model.getNodes()) {
                String result = epoch + "," + n.getId() + "," + n.getBelief() + "," +
                        (n.getDissonanceThreshold() - n.getCurrentDissonance()) + "," + n.getCurrentDissonance() + "," +
                        n.getNeighbours().size() + "," + getAvgBelief(n.getNeighbours()) + "," +
                        n.getConfidenceSet().size() + "," + getAvgBelief(n.getConfidenceSet()) + "," +
                        n.getNumberOfContacts() + "," + n.getNumberOfConflicts() + "\n";

                        fw.write(result);
            }

            fw.close();
        } catch (IOException e) {
            System.out.println("Could not output the data for epoch: " + epoch + "!\nData is lost!");
        }

    }

    /**
     * Sets whether or not the logger should generate output.
     * @param value Allow or disallow output
     */

    public void setAllowOutput(boolean value) {
        allowOutput = value;
    }

    /**
     * Toggles the allow output variable.
     */
    public void toggleAllowOutput() {
        allowOutput = !allowOutput;
    }

    // Utility function
    private float getAvgBelief(ArrayList<Node> nodes) {
        float res = 0.0f;

        if (nodes.size() == 0) {    //Perhaps not technically correct?
            return res;
        }

        for (Node n : nodes) {
            res += n.getBelief();
        }

        return res / nodes.size();
    }




}
