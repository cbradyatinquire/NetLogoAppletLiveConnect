#!/bin/sh
mkdir -p target
javac -classpath ./vendor/plugin.jar:./Example/NetLogoLite.jar ./src/wisenetlogo/TalkativeApplet.java -d target
jar -cf Example/wisenetlogo.jar -C target wisenetlogo
