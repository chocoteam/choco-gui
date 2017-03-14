choco-gui
=========

GUI is an extension for Choco4.
It provides a Graphical User Interface with various views which can be simply plugged on any Choco Solver object.

Usage
-----

Simply add the GUI-4.0.2.jar file to your classpath, together with choco-solver-4.0.2.jar file.
Then, before solving a problme, add GUI as a new monitor:


```solver.plugMonitor(new GUI(solver));```


This extension is a work-in-progress project, so any suggestions or contributions are welcome.

Available on MCR:

``` xml
<dependency>
   <groupId>org.choco-solver</groupId>
   <artifactId>choco-gui</artifactId>
   <version>4.0.2</version>
</dependency>
```
