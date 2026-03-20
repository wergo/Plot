package at.ofai.music.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;

/**
 * A plottable object that displays a typical OFAI Performance Worm (with its
 * characteristic layout: red circles with black margin fainting with time).
 * <p>
 * <code>add</code> functions write worm data to a circular buffer of the length
 * <code>wormLength</code> (see {@link #add(double, double, double, int, int)}).
 * After adding, the parent Axes must be repainted (<code>ax.repaint()</code>).
 *
 * @author Werner Goebl, Aug. 2005
 * @see Figure
 * @see Axes
 * @see PlottableObject
 */
public class WormPlot extends PlottableObject {

    public static final int TRACK = 1, BEAT = 2, BAR = 4, SEG1 = 8, SEG2 = 16, SEG3 = 32, SEG4 = 64;

    protected int wormLength; // length of the worm (e.g., 200)
    protected int bufferLength;
    protected double[] x, y;   // x and y Data
    protected int count;      // total no. of data points received
    protected int[] flags;
    protected Color[] reds, blacks;
    protected int colours;	  // number of different colours (max 255)
    protected double rad;
    protected double t;       // current time in seconds
    protected int barNumber;
    protected Text barText = null, timeText = null;
    protected String fontName;
    protected float fontStyle;
    protected float fontSize;

    /**
     * Creates a WormPlot onto the Axes object ax
     *
     * @param ax Axes object
     * @see Axes#wormPlot()
     */
    public WormPlot(Axes ax) {
        this(ax, 200);
    } // constructor

    /**
     * Creates a WormPlot onto the Axes object ax with the specified length.
     *
     * @param ax Axes object
     * @param wormLength lenght of the visible worm
     * @see Axes#wormPlot(int)
     */
    public WormPlot(Axes ax, int wormLength) {
        this.ax = ax;
        this.wormLength = wormLength;
        bufferLength = wormLength + 50;
        count = 0;
        x = new double[bufferLength];
        y = new double[bufferLength];
        flags = new int[bufferLength];
        reds = fadeRed(wormLength, -0.0125);
        blacks = fadeBlack(wormLength, -0.005);
        t = 0.0;
        rad = 16.0;
        fontName = ax.getFontName();
        fontStyle = TextAttribute.WEIGHT_BOLD;
        fontSize = ax.getFontSize() * 1.6f;
        if (!ax.isHold()) {
            ax.plottableObjects.clear();
        }
        ax.plottableObjects.add(this);
        ax.validate();
    } // constructor

    /**
     * Adds a new point (x, y) to the circular buffer of the length wormLength.
     *
     * @param xnew x data
     * @param ynew y data
     * @see WormPlot#WormPlot(Axes, int)
     */
    public void add(double xnew, double ynew) {
        add(-1.0, xnew, ynew);
    } // add()

    /**
     * Adds a new point (x, y) to the circular buffer and the current time (in
     * seconds) in the top-right corner.
     *
     * @param currTime current time in seconds
     * @param xnew x data
     * @param ynew y data
     */
    public void add(double currTime, double xnew, double ynew) {
        add(currTime, xnew, ynew, -1);
    } // add()

    /**
     * Adds a new point (x, y) to the circular buffer and the current time (in
     * seconds) in the top-right corner.
     *
     * @param currTime current time in seconds
     * @param xnew x data
     * @param ynew y data
     * @param flag int number specifying the hierarchical level of the current
     * data point ({@link #TRACK}, {@link #BEAT}, {@link #BAR},
     * {@link #SEG1} etc.).
     */
    public void add(double currTime, double xnew, double ynew, int flag) {
        add(currTime, xnew, ynew, -1, -1);
    } // add()

    /**
     * Adds a new point (x, y) to the circular buffer and the current time (in
     * seconds) in the top-right corner.
     *
     * @param currTime current time in seconds
     * @param xnew x data
     * @param ynew y data
     * @param flag int number specifying the hierarchical level of the current
     * data point ({@link #TRACK}, {@link #BEAT}, {@link #BAR},
     * {@link #SEG1} etc.).
     * @param barNumber barNumber specifies the current bar count as displayed
     * in the current worm slice.
     */
    public void add(double currTime, double xnew, double ynew, int flag,
            int barNumber) {
        x[count % bufferLength] = xnew;
        y[count % bufferLength] = ynew;
        flags[count % bufferLength] = flag;
        count++;
        this.barNumber = barNumber;
        t = currTime;
    } // add()

    /**
     * paint() routine.
     *
     * @param g
     */
    @Override
    public void render(Graphics2D g) {
        if (lineStyle.equals("-")) {
            g.setStroke(solid(lineWidth));
        } else if (lineStyle.equals(":")) {
            g.setStroke(PlottableObject.dotted(lineWidth));
        } else if (lineStyle.equals("--")) {
            g.setStroke(PlottableObject.dashed(lineWidth));
        } else if (lineStyle.equals("-.")) {
            g.setStroke(PlottableObject.dashdotted(lineWidth));
        }

        int i = 0;
        int myCount = count;
        int flag = 0;
        int start = 0;
        double xx = 0.0, yy = 0.0;
        //outn("count="+count+", wormLength="+wormLength+", ");
        for (start = myCount - wormLength; start < myCount; start++) {
            i++;
            if (start < 0) {
                continue;
            }
            int ind = start % bufferLength;
            xx = x[ind];
            yy = y[ind];
            flag = flags[ind];

            // size of disc (radius)
            double r = ax.getScaleRef()
                    * ((start - (myCount - wormLength)) * (rad - 1) / wormLength);
            if (flag > 0) {
                if ((flag & SEG1) != 0) {
                    r = r * 1.2;
                } else if ((flag & SEG1) != 0) {
                    r = r * 1.4;
                }
            }
			//outn("start="+start+", i="+i + "; st%wl="+(start%wormLength));

            Ellipse2D e = new Ellipse2D.Double(
                    xx * ax.getXFact() + ax.getXShift() - r,
                    yy * ax.getYFact() + ax.getYShift() - r,
                    2 * r, 2 * r);
            int index = wormLength - i;
			//outn("index="+index+", myCount="+myCount+", wormLength="+wormLength+
            //		", start="+start);
            //  if (index < 0) index = 0;
            if ((flag & SEG1) != 0 && flag > 0) {
                g.setColor(Color.BLACK);
            } else {
                g.setColor(reds[index]);
            }
            g.fill(e);
            if ((flag & SEG1) != 0 && flag > 0) {
                g.setColor(Color.BLACK);
            } else {
                g.setColor(blacks[index]);
            }
            g.draw(e);
        }

        if (barNumber > 0) { // barNumber in most recent oval
            String txt = Integer.toString(barNumber);
			//outn("barNumber updated: "+ barNumber + 
            //		"; x/y = "+ xx + "/"+ yy+ 
            //		", text="+ txt);
            if (barText == null) {
                barText = new Text(xx, yy, txt, ax);
                barText.setHorizontalAlignment("center");
                barText.setVerticalAlignment("middle");
                barText.setFontName(fontName);
                barText.setFontSize(fontSize);
                barText.setFontStyle(fontStyle);
            } else {
                barText.setString(txt);
                barText.setPosition(new double[]{xx, yy});
            }
            if ((flag & SEG1) != 0 && flag > 0) {
                barText.setColor(Color.WHITE);
            } else {
                barText.setColor(Color.BLACK);
            }
        }

        if (t > 0) { // time display
            String str = timeString(t);
            if (timeText == null) {
                timeText = new Text(.95, .95, str, ax);
                timeText.setNormalized(true);
                timeText.setHorizontalAlignment("right");
                timeText.setVerticalAlignment("top");
            } else {
                timeText.setString(str);
            }
        }
    } // paint()

    protected static Color[] fadeRed(int mapLength, double factor) {
        float comp;
        Color[] colorMap = new Color[mapLength];
        for (int j = 0; j < mapLength; j++) {
            comp = (float) (1 - Math.exp(factor * (double) j));
            //System.out.println("CompRed: " + comp);
            colorMap[j] = new Color(1.0f, comp, comp);
        }
        return colorMap;
    } // fadeRed

    protected static Color[] fadeBlack(int mapLength, double factor) {
        float comp;
        Color[] colorMap = new Color[mapLength];
        for (int j = 0; j < mapLength; j++) {
            comp = (float) (1 - Math.exp(factor * (double) j));
            //System.out.println("CompBlack: " + comp);
            colorMap[j] = new Color(comp, comp, comp);
        }
        return colorMap;
    } // fadeBlack

    public static String timeString(double time) {
        double seconds = time % 60.0;
        int minutes = (int) ((time / 60) % 60);
        int hours = (int) Math.floor(time / 3600.0);
        String str = "";
        if (time < 10) {
            str = String.format("%04.2f", seconds);
        } else {
            str = String.format("%05.2f", seconds);
        }
        if (minutes > 0) {
            if (hours <= 0) {
                str = String.format("%2d:", minutes) + str;
            } else {
                str = String.format("%02d:", minutes) + str;
            }
        }
        if (hours > 0) {
            str = String.format("%2d:", hours) + str;
        }
        return str;
    } // timeString()

    /*public static void main(String[] args) {
     MyWormFile wormFile = new MyWormFile();
     wormFile.read("C:/Documents and Settings/werner/My Documents/demos/JavaWorm/BeethovenFirstConcerto/Gould1958_Beethoven_op15_2_1-8.worm");
		
     Figure f = new Figure("Gould");
     WormPlot worm = f.wormPlot(wormFile.length);
     f.validate();
     f.repaint();

     for (int i = 0; i < wormFile.length; i++) {
     worm.add(wormFile.time[i],wormFile.inTempo[i],
     wormFile.inIntensity[i],wormFile.inFlags[i]);
     f.gca().repaint();
     }
     }*/
	//private static void out(String str)  { System.out.print(str);  } // out
    //private static void outn(String str) { System.out.println(str);} // outn
} // WormPlot
