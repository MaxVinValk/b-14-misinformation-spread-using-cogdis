# Running the program

Details about the app itself can be found in the accompanying manual in the repository. If you want to use the project, this should be your starting point. This readme mostly serves as a quick explanation of the structure of the code itself.

A shell script has been added which allows for building the (executable) jar file, namely build.sh. Running this file will generate a jar file, graph.jar, which can be executed.

The second easiest way of running the program is opening it up in an IDE. IntelliJ is recommended. After having installed IntelliJ, choose File->Open and select the NetSimulation folder. In the IDE, on the top right there is a field labelled "Edit configurations", right of a green hammer icon. Click it, and then click "Edit configurations" once more. Click the + symbol on the top left of the pop-up that has appeared, then choose "Application" from the drop-down menu. Give it a name, select the main class (Click the (...) behind the field for the class to find the right one). Click okay, and now the application can be executed using the green play button.

# Files
The project has been split up by the Java convention of separating the [controller](#controller), [model](#model) and [view](#view). Of these, for the course the model is the most relevant. The main entry point of the program can be found, as expected,
in the main class and does not warrant further comment.

## ModelManager
The model manager, located in the main package (com.b14), is responsible for setting up everything for the simulation to run, as well as the main loop that keeps the logic going.

## Controller

Files in the controller are the bridge between the user interaction and the model itself. The files here are mostly for executing specified commands, specified with the GUI, as well including the mouse adapter and file picker.

### actions
Each class in the action package is an action that is executed when a specific GUI element is clicked, or a corresponding shortcut is used. They are initialized within the view and within the InputController (the latter for shortcut keys).

### InputController
The input controller is responsible for capturing user input and keeping track of which elements the user interacted with last. This information is used by, for example, the graphics drawing, to highlight a node if it is selected.

### FileChooser
Specifies a file picker for loading in agent parameter files, which can be specified in csv files. More information on loading such parameters can be found in the manual.

## Model
In the model all relevant simulation operations are performed. The most important class here is the [GraphModel](#graphmodel), which has [Nodes](#node). There are some additional classes here that facilitate the simulations necessary to view this model as well. Further, there are two classes that deal with capturing data from the program and storing it appropriately, the [DataLogger](#datalogger) and the [ImageCapture](#imagecapture) classes. The sub-package recommendationstrategies holds the [RecommendationStrategy](#recommendationstrategy) class, responsible for recommending new connections to agents.

### GraphModel
One of the two "hearts" of the simulation. It tracks all nodes in a simulation, and deals with any logic regarding information spread on a network level. It extends the GraphPhysicsModel, which is responsible for handling the physics updates throughout the network itself.

### GraphPhysicsModel
Handles all physics updates.

### Node
The other heart of the simulation. All relevant logic with regards to the flow of information through a network takes place here. The functions for the logic of the nodes forming and disengaging from links can be found in here.

### Physics2DObject

A class in use to keep track of some physics properties, such as velocity and acceleration. This is used to allow for physics updates to create some space for nodes within a network.

### Vector2D

A utility class used for some vector operations, for instance getting a vector from one point to another (needed for application of spring force, gravity).

### DataLogger

Responsible for all data output of the program, such that the results can be analysed with external tools such as python and R. It also manages the [ImageCapture](#imagecapture) class, deciding when it should be fired, and where the results should be stored.

### ImageCapture

Houses logic for taking the current state of the simulation and creating an image output, so that a visual inspection can be performed alongside the data if desired, after the simulation.

### RecommendationStrategy
The RecommendationStrategy class stores all logic with regards to how to select agents to propose as possible connections.

## View

Houses the classes with regards to menus and the general display itself.

### Camera
The camera allows the user to pan around the screen, separating screen coordinates from world coordinates.

### GraphFrame

The surrounding frame of the application, on which the graph panel is drawn.

### GraphPanel

Houses all logic w.r.t. drawing the model on the screen itself.

### menubars

The menubars package holds all menubars that can be seen on top of the program panel. The MenuBar class is the menu bar displayed at the top of the program, while the classes with prefix MenuBar specify menubar entries.

### popup_menus
This package holds panels and frames for menus that pop-up, such as the colour chooser and the information frame.
