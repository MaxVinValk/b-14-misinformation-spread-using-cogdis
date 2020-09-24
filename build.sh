#!/bin/bash

# First, we compile the java files to class files
find ./NetSimulation/src -type f -name "*.java" > javaFiles.txt
javac @javaFiles.txt
rm javaFiles.txt

#Then we build the jar
cd ./NetSimulation/src
echo "Main-Class: com.b14.Main" > MANIFEST.MF
find . -type f -name "*.class" > classes.txt
jar cvmf MANIFEST.MF graph.jar @classes.txt
mv graph.jar ../../graph.jar

#Finally, cleanup
rm MANIFEST.MF
find . -name "*.class" -type f -delete
rm classes.txt
