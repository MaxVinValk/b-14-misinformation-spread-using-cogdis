# Running the program

The easiest way of running the program is opening it up in an IDE. IntelliJ is recommended. After having installed IntelliJ, choose File->Open and select the NetSimulation folder. In the IDE, on the top right there is a field labelled "Edit configurations", right of a green hammer icon. Click it, and then click "Edit configurations" once more. Click the + symbol on the top left of the pop-up that has appeared, then choose "Application" from the drop-down menu. Give it a name, select the main class (Click the (...) behind the field for the class to find the right one). Click okay, and now the application can be executed using the green play button.

# Files
The project has been split up by the Java convention of separating the [controller](#controller), [model](#model) and [view](#view). Of these, for the course the model is the most relevant.

## Controller

Files in the controller are the bridge between the user interaction and the model itself. The files here are mostly for executing specified commands, specified with the GUI.

### Action[...] Files
Each class that starts with action is the action that is executed when a specific GUI element is clicked. These GUI elements are initialized within the view.

### InputController
The input controller is responsible for capturing user input (currently, only mouse input is utilized) and keeping track of which elements the user interacted with last. This information is used by, for example, the graphics drawing, to highlight a node if it is selected.

## Model
In the model all relevant simulation operations are performed. The highest level here is the [ModelManager](#modelmanager), who manages an instance of the [GraphModel](#graphmodel), which has [Nodes](nodes). There are some additional classes here that facilitate the simulations necessary to view this model as well.

### ModelManager
The model manager is responsible for setting up everything for the simulation to run, as well as the main program loop that keeps the logic going.

### GraphModel
One of the two "hearts" of the simulation. It tracks all nodes in a simulation, as well as code for spacing them out such that they can be displayed nicely.

### Node
The other heart of the simulation. All relevant logic with regards to the flow of information through a network takes place here. The functions for the logic of the nodes forming and disengaging from links can be found in here.

### Physics2DObject

A class in use to keep track of some physics properties, such as velocity and acceleration. This is used to allow for physics updates to create some space for nodes within a network.

### Vector2D

A utility class used for some vector operations, for instance getting a vector from one point to another (needed for application of spring force, gravity).

## View

Houses the classes with regards to menus and the general display itself.

### Camera
The camera allows the user to pan around the screen, separating screen coordinates from world coordinates.

### GraphFrame

The surrounding frame of the application, on which the graph panel is drawn.

### GraphPanel

Houses all logic w.r.t. drawing the model on the screen itself.

### MenuBar*

The menubar class holds the main menubar the user sees on the top of the screen. The classes with prefix MenuBar specify menubar entries.
