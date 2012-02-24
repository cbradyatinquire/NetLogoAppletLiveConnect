===
Purpose:  Add javascript communications to the NetLogo Applet
===

Project Includes:  Simple subclass of org.nlogo.lite.Applet
HTML file example.
Referenced .logo file (Fire)


Limitations:  Currently, the released (5.0) NetLogoLite.jar does not provide access to necessary classes.  Therefore you have to build against NetLogo.jar

This also means that your HTML page may to include all of the jars in the /lib file

Eventually, this can be corrected by altering the way that the NetLogoLite.jar is optimized.

To run Example/testCommunications.html first build the wisenetlogo.jar

    $ ./build.sh

This will create `Example/wisenetlogo.jar`

Then open `Example/testCommunications.html` in a browswer.
