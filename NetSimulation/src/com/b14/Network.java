package com.b14;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Network {

    private List<Source> sources;
    private List<User> users;
    private List<Node> nodes;       // To perform actions on each node, no matter if it is a source or user

    private NetworkExporter exporter; // Exporter turns action within a network into a NetLogo animation

    private int nextFreeID = 0; // To keep track of the label for each entity

    public Network(int numUsers, int numSources) {

        users = new ArrayList<>();
        sources = new ArrayList<>();
        nodes = new ArrayList<>();
        exporter = new NetworkExporter(this);

        initNetworkRandom(numUsers, numSources);

    }

    public void simulateSpread(Message message) {

        for (Node n : nodes) {
            n.reset();
        }

        exporter.initNetLogoExport();
        exporter.initialStateToNetLogo();

        //A list of nodes that have been changed, to see who we need to update
        List<Node> toBeUpdated = new ArrayList<>();

        //Insert the message
        for (Source s : sources) {
            List<Node> recipients = s.broadcast(message);

            if (recipients != null) {
                toBeUpdated.addAll(recipients);
            }
        }

        //store the initial nodes that received the message
        //exporter.addAnimationFrame(null);

        List<Node> nextToBeUpdated = new ArrayList<>();

        int epoch = 0;

        while(toBeUpdated.size() > 0)
        {
            for (Node n : toBeUpdated) {
                if (n instanceof User) {
                    User u = (User)n;
                    List<Node> recipients = u.shareMessage();

                    if (recipients != null) {
                        nextToBeUpdated.addAll(recipients);
                    }
                }
            }

            exporter.addAnimationFrame(null);
            toBeUpdated = new ArrayList<>(nextToBeUpdated);
            nextToBeUpdated.clear();
            epoch++;
        }

        HashMap<User, List<User>> toBePruned = new HashMap<>();

        System.out.println("Information spread stopped after " + epoch + " epochs");

        //See if we can prune dissidents:
        for (User u : users) {
            List<User> dissidents= u.pruneDissidents();

            if (dissidents != null) {
                toBePruned.put(u, dissidents);
            }
        }

        if (toBePruned.size() != 0) {
            System.out.println("Performing network pruning");
            exporter.addAnimationFrame(toBePruned);
        }

        exporter.finalizeAnimation();
        exporter.createGUI();

        exporter.saveSimulation(LocalTime.now() + "_V_" + Main.VERSION +"_NetLogoSim.nlogo");

    }

    // Getters
    public List<User> getUsers() {
        return users;
    }

    public List<Source> getSources() {
        return sources;
    }

    private void initNetworkRandom(int numUsers, int numSources) {
        createRandomUsers(numUsers);
        connectUsersProportionate();
        createLoops();
        createRandomSources(numSources);
        attachSources();
    }

    private void createRandomUsers(int numUsers) {
        for (int i = 0; i < numUsers; i++) {
            User user = new User(nextFreeID++);
            users.add(user);
            nodes.add(user);
        }
    }

    private void createRandomSources(int numSources) {
        for (int i = 0; i < numSources; i++) {
            Source source = new Source(nextFreeID++);
            sources.add(source);
            nodes.add(source);
        }
    }

    private void connectUsersProportionate() {

        Random random = new Random();
        int numUsers = users.size();

        // used temporarily for initialisation only
        List <User> unassigned = new ArrayList<>();
        unassigned.addAll(users);

        // first connection has to be entered on its own, as no user has any connections
        // Note that if the users are initialized at random, then selecting users 0,1 always is the same as
        // selecting random ones
        User outgoing = unassigned.get(0);
        User ingoing = unassigned.get(1);

        // and set the users as each others neighbours & remove them from the list of unassigned users
        outgoing.addNeighbour(ingoing);
        ingoing.addNeighbour(outgoing);

        unassigned.remove(outgoing);
        unassigned.remove(ingoing);

        //this will be used in proportionate selection.
        int totalConnections = 2;

        // connect up all users that are not connected
        // Observation: we can get the index here as well by using a for-loop
        for (User unconnected : unassigned) {
            // gets the index of the user (from the unassigned list) in the users list
            int unconnectedIdx = users.indexOf(unconnected);

            //select an index proportionately to the amount of already available connections on a user
            int selected = random.nextInt(totalConnections);
            int selectedIdx = 0;

            // Find the randomly selected user
            // Observation: we can also keep track of a list of the users that already have a neighbour.
            // However, as the time complexity here is linear and this has to run only once, this probably suffices
            for (int i = 0; i < numUsers; i++) {
                selected -= users.get(i).getNeighbours().size();

                if (selected < 0) {
                    selectedIdx = i;
                    break;
                }

            }

            Node newNeighbour = users.get(selectedIdx);
            newNeighbour.addNeighbour(unconnected);
            unconnected.addNeighbour(newNeighbour);

            totalConnections += 2;
        }
    }

    private void createLoops() {

        Random random = new Random();

        // Collect nodes with only 1 connection
        List<User> onlyOneConnection = new ArrayList<>();

        for (User n : users) {
            if (n.getNeighbours().size() == 1) {
                onlyOneConnection.add(n);
            }
        }

        //we need at least a pair to connect up
        while (onlyOneConnection.size() >= 2) {

            // Select 2 elements
            User firstToConnect = onlyOneConnection.get(random.nextInt(onlyOneConnection.size()));
            User secondToConnect = null;

            do {
                secondToConnect = onlyOneConnection.get(random.nextInt(onlyOneConnection.size()));
            } while (firstToConnect == secondToConnect);

            firstToConnect.addNeighbour(secondToConnect);
            secondToConnect.addNeighbour(firstToConnect);

            onlyOneConnection.remove(firstToConnect);
            onlyOneConnection.remove(secondToConnect);
        }
    }

    private void attachSources() {

        Random random = new Random();

        for (Source s : sources) {
            User user = users.get(random.nextInt(users.size()));
            s.addNeighbour(user);
            user.addNeighbour(s);
        }

    }
}
