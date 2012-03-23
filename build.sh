#!/bin/sh
mkdir -p target
javac -classpath ./Example/nlogo/NetLogo.jar ./src/wisenetlogo/TalkativeApplet.java -d target
jar -cf Example/wisenetlogo.jar -C target wisenetlogo
