GUI
=========

GUI is an extension for Choco3.
It provides a Graphical User Interface with various views which can be simply plugged on any Choco Solver object.

Usage
-----

Simply add the GUI-3.2.1-SNAPSHOT.jar file to your classpath, together with choco-solver-3.2.1-SNAPSHOT.jar file.
Then, before solving a problme, add GUI as a new monitor:


```solver.plugMonitor(new GUI(solver));```


This extension is a work-in-progress project, so any suggestions or contributions are welcome.
