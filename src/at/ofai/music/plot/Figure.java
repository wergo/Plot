package at.ofai.music.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Field;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

//import at.ofai.music.util.PSPrinter;
/**
 * Figure object that contains axes and its plottable objects.
 * <p>
 * The whole <code>plot</code> package is designed similar to the Matlab plot
 * implementation. Methods to create all possible {@link PlottableObject}s
 * (Plot, Line, etc.) are implemented in either a static way, e.g.:
 * <pre>
 * Figure.splot(new double[]{2, 3.4, 6, .1},"r:o");
 * </pre> or in a non-static way, e.g.:
 * <pre>
 * Figure f = new Figure("My Test Figure");
 * f.plot(new double[]{2, 3.4, 6, .1},"k-.*");
 * f.repaint(); // to finally draw the whole thing to the screen
 * </pre> The methods provide all sensible combinations of input data (either
 * only y or x and y) and start and end indices of the arrays.
 * <p>
 * The string argument refers to <code>Matlab</code>'s <b>LineSpec</b>
 * properties which specify<br>
 * <i>lineStyle</i> ("--" for dashed, ":" for dotted, see
 * {@link PlottableObject#setLineStyle(String)}), <br> <i>marker</i> (e.g., "*",
 * "o", "s", "." etc, see {@link Plot#setMarker(String)}, and <br> <i>color</i>
 * (e.g., "r" for red etc, see {@link Plot}).<br> [<b><i>Remark</i></b>: If you
 * change lineStyle to other than solid or change lineWidth the graphics will
 * invoke the stroke functions from {@link java.awt.Graphics2D} which are a lot
 * slower than the normal {@link java.awt.Graphics} plotting.]
 * <p>
 * <b>Interactive zoom functions</b><br> Press <code>Z</code> to change into the
 * zoom mode and use the mouse for zooming (left button for zooming in; right
 * mouse button for resetting to original view). <br> Independently of the "Z"
 * mode, you can also use the four arrow keys to shift the axes left/right and
 * up/down or to zoom in and out the individual axes (
 * <code>arrow keys + CTRL</code>); <code>CTRL + "+"</code> and
 * <code>CTRL + "-"</code> will zoom in and out both axes simultaneously; use
 * <code>"0"</code> or <code>CTRL + "0"</code> to set the axes limits back to
 * the original.
 * <p>
 * <b>Exporting Printing</b><br> There are currently two options for
 * printing/exporting:<br>
 * <code>SHIFT + P</code> will open a print window, where you can choose a
 * printer (and an output file).<br>
 * <code>CTRL + P</code> will save the figure as an EPS file into a default file
 * (that consist of the Figure's name) [this feature makes use of the
 * org.jibble.epsgraphics package by Paul Mutton].
 * <p>
 *
 * @author Werner Goebl
 * @see Axes
 * @see PlottableObject
 */
@SuppressWarnings("serial")
public class Figure extends JFrame {

    protected static Figure currentFigure = null;
    protected static LinkedList<Figure> figures = new LinkedList<>();
    protected Axes currentAxes = null;
    protected LinkedList<Axes> axes = new LinkedList<>();
    protected GridLayout gl;
    protected JPanel[] paneList;
    protected FigureListener fl;
    protected boolean printFig = false;
    private final int[] defaultPosition = {232, 58, 560, 420};
    private int[] position = {232, 58, 560, 420};	// on screen (x y w h)
    private static final Color defaultBackgroundColor = Color.LIGHT_GRAY; // background color
    //private final Color defaultColor = Color.WHITE; // background color
    private Color color = Figure.defaultBackgroundColor;
    private String title;

    public Figure() {
        this("Figure " + String.format("%d", figures.size() + 1));
    } // constructor 

    public Figure(String figureTitle) {
        title = figureTitle;
        currentFigure = this;
        setLocation(defaultPosition[0], defaultPosition[1]);
        setSize(defaultPosition[2], defaultPosition[3]);
        gl = new GridLayout(1, 1); // default layout with (1,1)
        setLayout(gl);
        createContainery();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setIconImage(getToolkit().getImage(URLClassLoader.getSystemResource("at/ofai/music/plot/plot.jpg")));
        setIconImage(new ImageIcon(getClass().getClassLoader().getResource("at/ofai/music/plot/plot.jpg")).getImage());
        setTitle(title);
        setVisible(true);
        figures.add(currentFigure);
        fl = new FigureListener(this);

        /*		// new axes ??
         currentAxes = new Axes(this);
         currentAxes.addFocusListener(fl);
         paneList[0].add(currentAxes);
         axes.add(currentAxes);
         currentAxes.requestFocusInWindow(); 
         */
        /*KeyboardFocusManager focusManager =
         KeyboardFocusManager.getCurrentKeyboardFocusManager();
         focusManager.addPropertyChangeListener(
         new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent e) {
         String prop = e.getPropertyName();
         //M.outn("FocussedProperty="+prop);
         if (("focusOwner".equals(prop)) &&
         (e.getNewValue() != null) &&
         ((e.getNewValue()) instanceof Axes)) {
	
         Axes ax = (Axes)e.getNewValue();
         if (ax != null) {
         Title t = ax.getTitle();
         String str = "";
         if (t != null) str = t.getString();
         M.outn("ComonentName="+str);
         //ax.setColor(Color.RED);
         }
         }
         }
         }
         ); // PropertyChangeListener*/
        validate();
    } // constructor

    private void createContainery() {
        int n = gl.getColumns() * gl.getRows();
        paneList = new JPanel[n];
        for (int i = 0; i < n; i++) {
            JPanel jp = new JPanel();
            jp.setLayout(new BorderLayout());
            add(jp);
            paneList[i] = jp;
        }
    } // createContainery()

    /**
     * Creates a test plot for demonstration purpose. Just call Figure without
     * any arguments.
     *
     * @param args (unused)
     */
    public static void main(String[] args) {
//        JFrame frame = new JFrame("Werners JFrame Test");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setPreferredSize(new Dimension(500, 300));
//        //frame.getContentPane().add(emptyLabel , BorderLayout.CENTER);
//        Axes ax = new Axes(frame);
//        ax.plot(new double[]{2, 3, 2, 14, 4, 3, 2, .032, 3, 2, 1, 2}, "b.-");
//        ax.setXLabel("xData^2");
//        ax.setSize(frame.getSize());
//        frame.add(ax);
//        frame.pack();
//        frame.setVisible(true);
//        frame.repaint();
//        System.out.println("Ax size: " + ax.getSize());
        
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = env.getScreenDevices();
        float scaleFactor = 1.0f;
        try {
            Field field = devices[0].getClass().getDeclaredField("scale");
            if (field != null) {
                field.setAccessible(true);
                Object scale = field.get(devices[0]);
                if (scale instanceof Integer) {
                    scaleFactor = (Integer) scale;
                }
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Figure f = new Figure("Werner's Testplot");
        Axes ax = f.gca();
        ax.setAntialiasing(true);
        ax.setScaleFactor(scaleFactor);
        f.boxOff();
        f.holdOn();
        f.gridOn();

        Plot qq = f.plot(new double[]{-1, 2.3, 4, 11.6}, new double[]{1, 2, .5, 6}, "g--d");
        qq.setMarkerSize(20.0);
        qq.setMarkerFaceColor(Color.RED);

        f.plot(new double[]{2, 3, 2, 14, 4, 3, 2, .032, 3, 2, 1, 2}, "b.-");
        f.plot(new double[]{-2, 2, -3, -14, -4, -3, -2, Double.NaN, -3, -2, -1, -2}, "k*:");

        f.plot(new double[]{1, 2, 3, 2, 1}, new double[]{5, 4, 3, 2, 1}, "r.-");
        double[] x = new double[1000];
        double[] y = new double[1000];
        for (int i = 0; i < y.length; i++) {
            x[i] = i / 100.0;
            y[i] = Math.sin(i / 100.0) * 10.0;
        }
        f.plot(x, y, "r-");

        f.plot(new double[]{10, 9, 8}, new double[]{10, 9, 8}, "y^-");
        f.plot(new double[]{9, 8, 7}, new double[]{10, 9, 8}, "r<-");
        f.plot(new double[]{8, 7, 6}, new double[]{10, 9, 8}, "g>-");
        f.plot(new double[]{7, 6, 5}, new double[]{10, 9, 8}, "bv-");
        Plot ps = f.plot(new double[]{5, 4, 3}, new double[]{10, 9, 8}, "ms-");
        ps.setMarkerFaceColor(Color.GREEN.brighter());
        ps.setLineStyle(":");

        f.plot(new double[]{3, 2, 1}, new double[]{10, 9, 8}, "c+-");
        f.plot(new double[]{1, 0, -1}, new double[]{10, 9, 8}, "lx-");

        Plot p = f.plot(new double[]{.234, 0, .123}, new double[]{1, 2, 3});
        p.setMarker("d");
        p.setMarkerEdgeColor(Color.GREEN);
        p.setMarkerFaceColor(Color.BLUE);

        Text txt = f.text(-1, -10, "Textfield: a^2 + b^2 = c^2");
        txt.setLineStyle("--");
        txt.setFaceColor(new Color(240, 240, 240));
        txt.setMargin(5);

        Rectangle r = f.rect(7, -10, 2.5, 5, 1, 1);
        r.setLineStyle(":");
        r.setLineWidth(3f);
        r.setEdgeColor(Color.RED);
        r.setFaceColor(new Color(255, 190, 190));

        f.gca().setXLabel("xData^2");
        f.gca().setYLabel("yData_{jk}");
        f.gca().setTitle("Werner's Testplot");
        f.repaint();
    }

    /*public void paint(Graphics g) {
     Graphics2D g2 = (Graphics2D) g;
     if (printFig) { // TODO move that to Figure class and override the paint(GRaphics g) method
     File outputFile = new File(getTitle()+".eps");
     outn("outputFile: "+ outputFile);
     setColor(Color.WHITE);
     for (JPanel jp : paneList) {
     Axes ax = (Axes) jp.getComponents()[0];
     if (ax != null) ax.setAxesLineWidth(.5f);
     }
     try {
     g2 = new EpsGraphics2D(getTitle(),outputFile,
     0, 0, getWidth(), getHeight());
     } catch (IOException e) {
     e.printStackTrace();
     }
     } else {
     setColor(getDefaultColor());
     for (JPanel jp : paneList) {
     Axes ax = (Axes) jp.getComponents()[0];
     if (ax != null) 
     ax.setAxesLineWidth(ax.getDefaultAxesLineWidth());
     }
     }

     for (JPanel jp : paneList) {
     Axes ax = (Axes) jp.getComponents()[0];
     if (ax != null) ax.paintComponent(g2);
     //ax.repaint();
     }
     if (printFig) {
     try {
     ((EpsGraphics2D) g2).flush();
     ((EpsGraphics2D) g2).close();
     } catch (IOException e) {
     e.printStackTrace();
     }
     printFig = false;
     outn("Eps file written to standard output");
     return;
     }

     } // paint() */
    public static Plot splot(double x, double y) {
        return gcf().plot(x, y);
    } // splot(x, y)

    public static Plot splot(double x, double y, String lineSpecs) {
        return gcf().plot(x, y, lineSpecs);
    } // splot(x, y)

    public Plot plot(double x, double y) {
        return gca().plot(x, y);
    } // at.ofai.music.plot(x, y)

    public Plot plot(double x, double y, String lineSpecs) {
        return gca().plot(x, y, lineSpecs);
    } // at.ofai.music.plot(x, y)

    public static Plot splot(double[] y) {
        return gcf().plot(y);
    } // splot(y[])

    public static Plot splot(double[] y, String lineSpecs) {
        return gcf().plot(y, lineSpecs);
    } // splot(y[])

    public Plot plot(double[] y) {
        return gca().plot(y);
    } // at.ofai.music.plot(y[])

    public Plot plot(double[] y, String lineSpecs) {
        return gca().plot(y, lineSpecs);
    } // at.ofai.music.plot(y[])

    public static Plot splot(double[] y, int i2) {
        return gcf().plot(y, i2);
    } // splot(y[], i2)

    public static Plot splot(double[] y, int i2, String lineSpecs) {
        return gcf().plot(y, i2, lineSpecs);
    } // splot(y[], i2)

    public Plot plot(double[] y, int i2) {
        return gca().plot(y, i2);
    } // at.ofai.music.plot(y[], i2)

    public Plot plot(double[] y, int i2, String lineSpecs) {
        return gca().plot(y, i2, lineSpecs);
    } // at.ofai.music.plot(y[], i2)

    public static Plot splot(double[] y, int i1, int i2) {
        return gcf().plot(y, i1, i2);
    } // splot(y[], i1, i2)

    public static Plot splot(double[] y, int i1, int i2, String lineSpecs) {
        return gcf().plot(y, i1, i2, lineSpecs);
    } // splot(y[], i1, i2)

    public Plot plot(double[] y, int i1, int i2) {
        return gca().plot(y, i1, i2);
    } // at.ofai.music.plot(y[], i1, i2)

    public Plot plot(double[] y, int i1, int i2, String lineSpecs) {
        return gca().plot(y, i1, i2, lineSpecs);
    } // at.ofai.music.plot(y[], i1, i2)

    public static Plot splot(double[] x, double[] y) {
        return gcf().plot(x, y);
    } // splot(x[], y[])

    public static Plot splot(double[] x, double[] y, String lineSpecs) {
        return gcf().plot(x, y, lineSpecs);
    } // splot(x[], y[])

    public Plot plot(double[] x, double[] y) {
        return gca().plot(x, y);
    } // at.ofai.music.plot(x[], y[])

    public Plot plot(double[] x, double[] y, String lineSpecs) {
        return gca().plot(x, y, lineSpecs);
    } // at.ofai.music.plot(x[], y[])

    public static Plot splot(double[] x, double[] y, int i2) {
        return gcf().plot(x, y, i2);
    } // splot(x[], y[], i2)

    public static Plot splot(double[] x, double[] y, int i2, String lineSpecs) {
        return gcf().plot(x, y, i2, lineSpecs);
    } // splot(x[], y[], i2)

    public Plot plot(double[] x, double[] y, int i2) {
        return gca().plot(x, y, i2);
    } // at.ofai.music.plot(x[], y[], i2)

    public Plot plot(double[] x, double[] y, int i2, String lineSpecs) {
        return gca().plot(x, y, i2, lineSpecs);
    } // at.ofai.music.plot(x[], y[], i2)

    public static Plot splot(double[] x, double[] y, int i1, int i2) {
        return gcf().plot(x, y, i1, i2);
    } // splot(x[], y[], i1, i2)

    public static Plot splot(double[] x, double[] y, int i1, int i2,
            String lineSpecs) {
        return gcf().plot(x, y, i1, i2, lineSpecs);
    } // splot(x[], y[], i1, i2)

    public Plot plot(double[] x, double[] y, int i1, int i2) {
        return gca().plot(x, y, i1, i2);
    } // at.ofai.music.plot(x[], y[], i1, i2)

    public Plot plot(double[] x, double[] y, int i1, int i2, String lineSpecs) {
        return gca().plot(x, y, i1, i2, lineSpecs);
    } // at.ofai.music.plot(x[], y[], i1, i2)

    public static Plot splot(LinkedList<Double> y) {
        return gcf().plot(y);
    }  // splot(LinkedList<Double>)

    public static Plot splot(LinkedList<Double> y, String lineSpecs) {
        return gcf().plot(y, lineSpecs);
    }  // splot(LinkedList<Double>)

    public Plot plot(LinkedList<Double> y) {
        return gca().plot(y);
    }  // at.ofai.music.plot(LinkedList<Double>)

    public Plot plot(LinkedList<Double> y, String lineSpecs) {
        return gca().plot(y, lineSpecs);
    }  // at.ofai.music.plot(LinkedList<Double>)

    public static Plot splot(LinkedList<Double> x, LinkedList<Double> y) {
        return gcf().plot(x, y);
    }  // splot(LinkedList<Double>, LinkedList<Double>)

    public static Plot splot(LinkedList<Double> x, LinkedList<Double> y,
            String lineSpecs) {
        return gcf().plot(x, y, lineSpecs);
    }  // splot(LinkedList<Double>, LinkedList<Double>)

    public Plot plot(LinkedList<Double> x, LinkedList<Double> y) {
        return gca().plot(x, y);
    }  // at.ofai.music.plot(LinkedList<Double>, LinkedList<Double>)

    public Plot plot(LinkedList<Double> x, LinkedList<Double> y,
            String lineSpecs) {
        return gca().plot(x, y, lineSpecs);
    }  // at.ofai.music.plot(LinkedList<Double>, LinkedList<Double>)

    public static Text stext(double x, double y, String s) {
        return gcf().text(x, y, s);
    } // stext(double, double, String)

    public Text text(double x, double y, String s) {
        return gca().text(x, y, s);
    } // text(double, double, String)

    public static Line sline(double x1, double y1, double x2, double y2) {
        return gcf().line(x1, y1, x2, y2);
    } // sline(x1, y1, x2, y2)

    public Line line(double x1, double y1, double x2, double y2) {
        return gca().line(x1, y1, x2, y2);
    } // line(x1, y1, x2, y2)

    public static Rectangle srect() {
        return gcf().rect();
    } // srect()

    public Rectangle rect() {
        return gca().rect();
    } // rect()

    public static Rectangle srect(double x, double y, double w, double h) {
        return gcf().rect(x, y, w, h);
    } // srect (x, y, w, h)

    public Rectangle rect(double x, double y, double w, double h) {
        return gca().rect(x, y, w, h);
    } // rect (x, y, w, h)

    public static Rectangle srect(double x, double y, double w, double h,
            double xc, double yc) {
        return gcf().rect(x, y, w, h, xc, yc);
    } // srect (x, y, w, h, xc, yc)

    public Rectangle rect(double x, double y, double w, double h,
            double xc, double yc) {
        return gca().rect(x, y, w, h, xc, yc);
    } // rect (x, y, w, h, xc, yc)

    /**
     * Creates a WormPlot into the current axes
     *
     * @return WormPlot
     */
    public WormPlot wormPlot() {
        return gca().wormPlot();
    } // WormPlot()

    /**
     * Creates a WormPlot into the current axes with a specified wormLength
     *
     * @param wormLength length of the visible worm
     * @return WormPlot
     */
    public WormPlot wormPlot(int wormLength) {
        return gca().wormPlot(wormLength);
    } // WormPlot(wormLength)

    public void holdOn() {
        gca().holdOn();
    } // holdOn()

    public void holdOff() {
        gca().holdOff();
    } // holdOff()

    public void gridOn() {
        gca().gridOn();
    } // gridOn()

    public void gridOff() {
        gca().gridOff();
    } // gridOff()

    public void boxOn() {
        gca().boxOn();
    } // boxOn()

    public void boxOff() {
        gca().boxOff();
    } // boxOff()

    public void clear() {
        gca().clear();
    } // clear() 

    /*public static Axes newAxes() {
     Axes ax = new Axes(currentFigure);
     currentFigure.add(ax);
     return ax;
     } // newAxes()*/
    /*public static Axes newAxes(Figure f) {
     Axes ax = new Axes(f);
     this.containerList[0].add(ax);
     return ax;
     } // newAxes(Figure)*/
    /**
     * Get current figure. Returns the current figure, if there is no, it
     * creates one.
     *
     * @return currentFigure
     */
    public static Figure gcf() {
        if (currentFigure == null) {
            currentFigure = new Figure();
            figures.add(currentFigure);
        }
        return currentFigure;
    } // gcf

    /**
     * Get current axes. Returns the current axes object of the current figure.
     * If the current figure doesn't have one, it will be created.
     *
     * @return currentAxes
     */
    public Axes gca() {
        if (currentAxes == null) {
            currentAxes = new Axes(this);
            currentAxes.addFocusListener(fl);
            paneList[0].add(currentAxes);
            axes.add(currentAxes);
            currentAxes.requestFocusInWindow();
            validate();
        }
        return currentAxes;
    } // gca

    /**
     * Sets the figure's position on the screen (in pixels).
     *
     * @param position [x y w h] while x y indicates the upper left corner of
     * the screen.
     */
    public void setPosition(int[] position) {
        currentFigure.setLocation(position[0], position[1]);
        currentFigure.setSize(position[2], position[3]);
        this.position[0] = position[0];
        this.position[1] = position[1];
        this.position[2] = position[2];
        this.position[3] = position[3];
    } // setPosition

    /**
     * Returns the current position of the figure.
     *
     * @return position [x y w h] while x y indicates the upper left corner
     */
    public int[] getPosition() {
        return position;
    } // getPosition

    /**
     * Sets the Figure's (JFrame's) title string.
     *
     * @param title
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the Figure's title string.
     *
     * @return title
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Sets the Figure's background color.
     *
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Returns the Figure's background color.
     *
     * @return color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Creates the <code>n</code><sup>th</sup> subplot in a <code>r</code> rows
     * by <code>c</code> column layout (using a {@link java.awt.GridLayout}).
     *
     * <p>
     * <b>Example</b>
     * <pre>
     * f = new Figure("My Plot");
     * f.subplot(2,3,1);
     * f.plot(new double[]{1, 2, .5},"b-*");
     * Plot sp2 = f.subplot(2,3,2);
     * sp2.plot(...);
     * sp2.gridOn();
     * f.subplot(2,3,5);
     * f.line(...);
     * ...
     * f.repaint();
     * </pre> <b>Note:</b><br> To interactively zoom the different subplots,
     * press <code>TAB</code> to move the focus the the next subplot.
     *
     * @param r number of rows
     * @param c number of columns
     * @param n which of the r * c subplot to draw (starting from 1 to r*c at
     * the top-left corner continuing rowwise).
     * @return Axes object
     * @see Axes
     */
    public Axes subplot(int r, int c, int n) {
        n--; // here: n = 0 to length-1;
        if (n >= r * c || n < 0) {
            System.err.println("Figure.subplot() n out of range.");
        }
        if (gl.getRows() != r || gl.getColumns() != c) {
            for (int i = 0; i < gl.getRows() * gl.getColumns(); i++) {
                this.remove(paneList[i]);
            }
            paneList = null;
            gl.setRows(r);
            gl.setColumns(c);
            createContainery();
        }
        Component[] cps = (paneList[n].getComponents());
        if (cps.length == 0) { // if no axes present in JPanel grid paneList
            Axes ax = new Axes();
            ax.setFocusable(true);
            ax.addFocusListener(fl);
            paneList[n].add(ax);
            currentAxes = ax;
            validate();
        } else if (cps.length == 1) {
            currentAxes = (Axes) cps[0];
        } else {
            System.out.println("cps too long" + cps.length);
        }
        currentAxes.requestFocusInWindow();
        return currentAxes;
    } // subplot(r, c, n)

    /*public void print(int res) {
		
     PSPrinter.print(this.getContentPane(), res);
     } // print() */
    /**
     * @return Returns the defaultColor.
     */
    public Color getDefaultColor() {
        return defaultBackgroundColor;
    }

    /**
     * <b>Not implemented yet</b> (use CTRL + P to write out an EPS instead)
     *
     * @param fileName
     * @return boolean whether writing was succesful
     */
    public boolean saveEps(String fileName) {
        return false;
    } // saveEps()

    public static Color getDefaultBackgroundColor() {
        return defaultBackgroundColor;
    }
    //private static void out(String str)  { System.out.print(str);  } // out
    //private static void outn(String str) { System.out.println(str);} // outn
} // class Figure

/**
 * Listens to the subplots and sets the focus on them if clicked.
 *
 * @author wernerg
 * @see Figure
 */
class FigureListener implements FocusListener {

    Figure fig;

    public FigureListener(Figure f) {
        fig = f;
    } // constructor

    @Override
    public void focusGained(FocusEvent e) {
        //System.out.println("focusGained: "+ e);
        //System.out.println("oppEvent: " + e.getOppositeComponent());
        if (!e.isTemporary()) {
            //fig.currentAxes = (Axes) e.getComponent();
            //fig.currentAxes.requestFocusInWindow();
            //System.out.println("focus set for " + 
            //		fig.currentAxes.getTitle().getString());
        }
    } // focusGained

    @Override
    public void focusLost(FocusEvent e) {
        //System.out.println("focusLost: "+ e);
    } // focusLost
} // FigureListener
