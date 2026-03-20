# Plot
Simple and versatile 2D plot package for JAVA, inspired by the Matlab 2D plot functionality.

<p align="center">
  <img src="Werner's Testplot.png" width="50%" alt="Testplot"/>
</p>

## How to use

Place the plot.jar file in your classpath and import the package:

```java
import goebl.plot.*;
```

To load the test plot, just double click the ```plot.jar``` file and the test plot will be displayed. You can also run the test plot from the command line:

```bash
java -jar plot.jar
```

Having the test plot displayed, you may use the mouse or keyboard to zoom, pan or print the figure (see below).

## Package Description

The complete Java documentation is available at https://iwk.mdw.ac.at/goebl/plot/javadoc/. 

Provides JAVA classes for various basic 2D plot functions, similar to the Matlab plot implementation (JAVA 1.5.0 or higher required).

It implemented a similar hierarchy and very similar property commands as the Matlab implementation:

<div align="center">
<p>Figure</br>
|</br>
Axes</br>
|</br>
PlottableObject</br>
/ | | | \</br>
Plot, Text, Rectangle, Line, WormPlot.</p>
</div>

The package is aimed to give JAVA programmers the comfort of the very good Matlab plot packages. Most 2D plotting features including plots, lines, text fields, rectangle or ellipse shapes are implemented. Nevertheless, many functionalities are missing or only coarsly implemented. Please send suggestions, bug reports, etc to me (werner dot goebl at ofai dot at).

Methods to create all possible PlottableObjects (Plot, Line, etc.) are implemented in either a static way, e.g.:

```java
Figure.splot(new double[]{2, 3.4, 6, .1},"r:o").repaint();
```

or in a non-static way, e.g.:

```java
Figure f = new Figure("My Test Figure");
Plot p = f.plot(new double[]{2, 3.4, 6, .1},"k-.*");
p.setMarkerFaceColor(Color.GREEN.brighter());
p.setMarkerEdgeColor(Color.GREEN);
f.repaint(); // to finally draw the whole thing to the screen
```

[I decided to leave the repaint() command to the programmer so you can plot a large number of data and redraw it once.] The methods provide all sensible combinations of input data (either only y or x and y) and start and end indices of the arrays.

The string argument refers to Matlab's LineSpec properties which specify
```lineStyle``` ("--" for dashed, ":" for dotted, see PlottableObject.setLineStyle(String)),
```marker``` (e.g., "*", "o", "s", "." etc, see Plot.```setMarker(String)```, and
```color``` (e.g., "r" for red etc, see Plot).

[Remark: If you change lineStyle to other than solid or change lineWidth the graphics will invoke the stroke functions from Graphics2D which might be slower than the normal Graphics plotting without calling any stroke properties.]

You can also create different subplots within a figure (see Figure.subplot(int, int, int)) by calling

```java
fig.subplot(r, c, n);
```

### Interactive Zoom Functions
Press "Z" to change into the zoom mode and use the mouse for zooming (left button for zooming in; right mouse button for resetting to original view).
Press "H" to change to pan mode (indicated by a hand cursor). You can now drag the display as you like.
You might also use the four arrow keys and CTRL to translate or zoom the individual axes or CTRL and "+" and "-" to zoom both axes (and "0" for setting the axes back to the original bounds); see Axes.

### Exporting Printing
There are currently two options for printing/exporting:
SHIFT + P will open a print window, where you can choose a printer (and an output file). (DISABLED IN THIS VERSION!)
CTRL + P will save the figure as an EPS file into a default file (that consist of the Figure's name) [this feature makes use of the org.jibble.epsgraphics package by Paul Mutton].

[Remark: printing subplots with CTRL + P is problematic, because currently it prints only the active subplot; use SHIFT + P instead.]

### Version:
    July 2014; (Nov. 18, 2005)

