package com.b14;

import java.io.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//TODO: Allow for toggling of functionality, so that function calls do not need to be removed in functions

public class NetworkExporter {

    private Network network;
    private StringBuilder preamble;         //Contains default functionality such as layout
    private StringBuilder setup;
    private StringBuilder animationCreator;
    private StringBuilder GUI;

    private int currentTick = 0;

    public NetworkExporter(Network network) {
        this.network = network;

        preamble = new StringBuilder();
        setup = new StringBuilder();
        animationCreator = new StringBuilder();
        GUI = new StringBuilder();
    }

    public void initNetLogoExport() {
        //TODO: Create a file for layout function.
        preamble.append("; EXPORT OF JAVA-SIM\n" +
                        "; Created on: " + LocalTime.now() + "\n" +
                        "; With " + Main.VERSION + "\n\n\n" +
                        "breed [nodes node]\n" +
                        "breed [sources source]\n\n" +
                        "to layout\n" +
                        "\tlet combinedSet (turtle-set nodes sources)\n" +
                        "\tif (mouse-down? and mouse-inside?)[\n" +
                        "\t\tlet closest combinedSet with-min [distancexy mouse-xcor mouse-ycor]\n" +
                        "\t\task closest [setxy mouse-xcor mouse-ycor]\n" +
                        "\t]\n\n" +
                        "\tlayout-spring combinedSet links .5 1 1\n" +
                        "end\n\n\n"
        );

        animationCreator.append("to nextAnimationStep\n");
    }

    public void initialStateToNetLogo() {

        List<User> users = network.getUsers();
        List<Source> sources = network.getSources();

        //add the nodes and sources themselves
        setup.append(  "to setup\n" +
                "\tca\n" +
                "\task patches [set pcolor white]\n\n" +
                "\tcreate-nodes " + users.size() + " [\n" +
                "\t\tset shape \"circle\"\n" +
                "\t\tset color blue\n" +
                "\t\tsetxy random-pxcor random-pycor\n" +
                "\t]\n\n\n" +
                "\tcreate-sources " + sources.size() + " [\n" +
                "\t\t set shape \"square\"\n" +
                "\t\t set color green\n" +
                "\t\tsetxy random-pxcor random-pycor\n" +
                "\t\tset size 2\n" +
                "\t]\n\n\n"
        );

        //now we have to add the connections

        //this has to be used a lot, so storing it is a bit neater imo
        int numUsers = users.size();


        //First, sources
        for (int i = 0; i < sources.size(); i++) {
            List<Node> neighbours = sources.get(i).getNeighbours();

            for (Node n : neighbours) {
                //Referring to sources requires them to be indexed from number of nodes
                setup.append(  "\task source " + (i+numUsers) + " [\n" +
                        "\t\tcreate-link-with node " + users.indexOf(n) + "\n" +
                        "\t]\n\n"
                );
            }
        }

        //Then, users

        //In this java simulation, edges are monodirectional. In NetLogo, Bidirectional.
        //Storing the already included mappings here fixes this, so that we do not add duplicates
        boolean[][] usermap = new boolean[numUsers][numUsers];

        for (int i = 0; i < numUsers; i++) {
            List<Node> neighbours = users.get(i).getNeighbours();

            for (Node n : neighbours) {
                //so as not to add connections back to the sources. In NetLogo connections are bidirectional
                if (n instanceof User) {
                    int otherIdx = users.indexOf(n);
                    if (!usermap[otherIdx][i]) {

                        setup.append(  "\task node " + i + " [\n" +
                                "\t\tcreate-link-with node " + otherIdx + "\n" +
                                "\t]\n\n"
                        );

                        usermap[i][otherIdx] = true;
                    }

                }
            }

        }

        //Wrap-up
        setup.append("\treset-ticks\nend\n\n");
    }

    public void addAnimationFrame(HashMap<User, List<User>> toBePruned) {
        animationCreator.append("\t if (ticks = " + currentTick + ") [\n");
        List<User> users = network.getUsers();

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getHasShared()) {
                animationCreator.append("\t\task node " + i + " [\n" +
                                        "\t\t\tset color red\n" +
                                        "\t\t]\n"
                );
            }
        }

        if (toBePruned != null) {
            for (User u : toBePruned.keySet()) {
                int uid = u.getEntityID();

                for (User n : toBePruned.get(u)) {
                    int nid = n.getEntityID();
                    //Safety check to prevent double-deletions is included
                    animationCreator.append("\t\task node " + uid + " [\n" +
                                            "\t\t\tlet l link-with node " + nid + "\n" +
                                            "\t\t\tif (l != NOBODY) [\n" +
                                            "\t\t\t\task l [die]\n" +
                                            "\t\t\t]\n" +
                                            "\t\t]"
                    );
                }

            }
        }

        animationCreator.append("\t]\n");
        currentTick++;
    }

    public void finalizeAnimation() {
        animationCreator.append("\ttick\n" +
                                "end\n"
        );
    }

    public void createGUI() {

        try {
            BufferedReader br = new BufferedReader(new FileReader("NetLogoScripts/gui.nlogo"));
            int read;
            do {
                read = br.read();

                if (read != -1) {
                    GUI.append((char)read);
                }
            } while (read != -1);

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find and/or read pre-defined GUI file.");
        }

    }

    public void saveSimulation(String fileName) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(preamble.toString());
            writer.write(setup.toString());
            writer.write(animationCreator.toString());
            writer.write(GUI.toString());

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not export NetLogo file");
        }


    }

}
