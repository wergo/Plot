package at.ofai.music.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Creates a plot object into an axes.
 *
 * @author Werner Goebl, Aug. 2005
 * @see Figure
 * @see Axes
 * @see PlottableObject
 */
public class Plot extends PlottableObject {

    private double[] uX, uY; // uX, uY = user data
    private boolean useLinkedLists = false;
    private LinkedList<Double> uXL, uYL; // user data as linked list (1-May-2014) 
    //private int[] sX, sY;    // sX, sY = screen data
    private int I1, I2; // data index start and end (only for array version)
    private static final String defaultMarker = "."; //
    private static final double defaultMarkerSize = 6.0;
    private String marker = defaultMarker; // 
    private double markerSize = defaultMarkerSize;
    private Color markerEdgeColor = super.edgeColor;
    private Color markerFaceColor = super.faceColor;

    public Plot(double x, double y) {
        this(x, y, "", Figure.gcf().gca());
    } // at.ofai.music.plot(double, double)

    public Plot(double x, double y, String lineSpecs) {
        this(x, y, lineSpecs, Figure.gcf().gca());
    }

    public Plot(double x, double y, String lineSpecs, Axes a) {
        this(doubleToArray(x), doubleToArray(y), "", a);
    } // at.ofai.music.plot.Plot(double, double, Axes)

    /**
     * Creates a Plot object with a linkedList that can be extended by separate
     * commands TODO (1-May-2014)
     *
     * @param y
     */
    public Plot(LinkedList<Double> y) {
        this(y, "", Figure.gcf().gca());
    } // Plot(LinkedList<Double>

    public Plot(LinkedList<Double> y, String lineSpecs) {
        this(y, lineSpecs, Figure.gcf().gca());
    } // 

    public Plot(LinkedList<Double> y, String lineSpecs, Axes a) {
        this(createDefaultXLinkedList(y.size()), y, lineSpecs, a);
        // this(lili2arr(y), lineSpecs, a);
    } // at.ofai.music.plot(linkedList<Double>, lineSpecs, Axes)

    public Plot(LinkedList<Double> x, LinkedList<Double> y) {
        this(x, y, "", Figure.gcf().gca());
    }  // at.ofai.music.Plot(linkedList<Double>, linkedList<Double>)

    public Plot(LinkedList<Double> x, LinkedList<Double> y, String lineSpecs) {
        this(x, y, lineSpecs, Figure.gcf().gca());
    }

    /**
     *
     * @param x
     * @param y
     * @param lineSpecs
     * @param a
     */
    public Plot(LinkedList<Double> x, LinkedList<Double> y, String lineSpecs, Axes a) {
        useLinkedLists = true;
        // this(lili2arr(x), lili2arr(y), lineSpecs, a);
        ax = a; // parent object
        if (ax.v) {
            out("\n\nPlot() constructor: ");
        }
        init(lineSpecs);

        // check input on equal length etc
        if (x.size() != y.size()) {
            err("Plot at.ofai.music.plot(x,y): "
                    + "LinkedLists must have the same lenght.");
        }

        // create internal LinkedLists for user data 
        uXL = new LinkedList<>();
        uYL = new LinkedList<>();

        // just write data into the plot arrays and renew ax.minX etc. if
        // the xLimMode.equals("auto"), otherwise don't check data boundaries.
        double minx = Double.MAX_VALUE;
        double maxx = Double.MIN_VALUE;
        double miny = Double.MAX_VALUE;
        double maxy = Double.MIN_VALUE;
        for (Double ux : x) {
            if (ux < minx) {
                minx = ux;
            }
            if (ux > maxx) {
                maxx = ux;
            }
            if (ax.getXScale().equals("log") && ux <= 0) {
                ux = Double.NaN;
                out("at.ofai.music.Plot [xlog scaling]: ");
                err("uX data zero or negative: " + ux);
            } else {
                uXL.add(ux);
            }
        }
        for (Double uy : y) {
            if (uy < minx) {
                minx = uy;
            }
            if (uy > maxx) {
                maxx = uy;
            }
            if (ax.getXScale().equals("log") && uy <= 0) {
                uy = Double.NaN;
                out("at.ofai.music.Plot [ylog scaling]: ");
                err("uY data zero or negative: " + uy);
            } else {
                uYL.add(uy);
            }
        }

        if (ax.getXLimMode().equals("auto")) {
            if (minx < ax.minX) {
                ax.minX = minx;
                if (ax.v) {
                    out(" minX set to: " + minx);
                }
            }
            if (maxx > ax.maxX) {
                ax.maxX = maxx;
                if (ax.v) {
                    out(", maxX set to: " + maxx);
                }
            }
        }
        if (ax.getYLimMode().equals("auto")) {
            if (miny < ax.minY) {
                ax.minY = miny;
                if (ax.v) {
                    out(", minY set to: " + miny);
                }
            }
            if (maxy > ax.maxY) {
                ax.maxY = maxy;
                if (ax.v) {
                    out(", maxY set to: " + maxy);
                }
            }
        }
        if (ax.v) {
            outn(".");
        }
        //synchronized(ax.plottableObjects) {
        ax.plottableObjects.add(this);
        //}
        ax.validate();

    } // at.ofai.music.plot(LinkedList<Double>, LinkedList<Double>, Axes)

    public Plot(double[] y) {
        this(y, "", Figure.gcf().gca());
    } // Plot(y[])

    public Plot(double[] y, String lineSpecs) {
        this(y, lineSpecs, Figure.gcf().gca());
    }

    public Plot(double[] y, String lineSpecs, Axes a) {
        this(y, (y.length - 1 < 0 ? 0 : y.length - 1), lineSpecs, a);
    } // Plot(y[], Axes)

    public Plot(double[] y, int i2) {
        this(y, i2, "", Figure.gcf().gca());
    } // Plot(y[], i2)

    public Plot(double[] y, int i2, String lineSpecs) {
        this(y, i2, lineSpecs, Figure.gcf().gca());
    }

    public Plot(double[] y, int i2, String lineSpecs, Axes a) {
        this(y, 0, i2, lineSpecs, a);
    } // Plot(y[], i2, Axes)

    public Plot(double[] y, int i1, int i2) {
        this(y, i1, i2, "", Figure.gcf().gca());
    } // Plot(y[], i1, i2)

    public Plot(double[] y, int i1, int i2, String lineSpecs) {
        this(y, i1, i2, lineSpecs, Figure.gcf().gca());
    }

    public Plot(double[] y, int i1, int i2, String lineSpecs, Axes a) {
        this(defaultX(y.length, i1, i2), y, i1, i2, lineSpecs, a);
    } // Plot(y[], i1, i2, axes)

    public Plot(double[] x, double[] y) {
        this(x, y, "", Figure.gcf().gca());
    } // Plot(x[], y[])

    public Plot(double[] x, double[] y, String lineSpecs) {
        this(x, y, lineSpecs, Figure.gcf().gca());
    }

    public Plot(double[] x, double[] y, String lineSpecs, Axes a) {
        this(x, y, (x.length - 1 < 0 ? 0 : x.length - 1), lineSpecs, a);
    } // Plot(x[], y[], Axes)

    public Plot(double[] x, double[] y, int i2) {
        this(x, y, i2, "", Figure.gcf().gca());
    } // Plot(x[], y[], i2)

    public Plot(double[] x, double[] y, int i2, String lineSpecs) {
        this(x, y, i2, lineSpecs, Figure.gcf().gca());
    }

    public Plot(double[] x, double[] y, int i2, String lineSpecs, Axes a) {
        this(x, y, 0, i2, lineSpecs, a);
    } // Plot(x[], y[], i2, Axes)

    public Plot(double[] x, double[] y, int i1, int i2) {
        this(x, y, i1, i2, "", Figure.gcf().gca());
    } // Plot(x[], y[], i1, i2

    public Plot(double[] x, double[] y, int i1, int i2, String lineSpecs) {
        this(x, y, i1, i2, lineSpecs, Figure.gcf().gca());
    }

    public Plot(double[] x, double[] y, int i1, int i2, String lineSpecs, Axes a) {
        useLinkedLists = false;
        ax = a; // parent object
        if (ax.v) {
            out("\n\nPlot() constructor: ");
        }
        init(lineSpecs);

        // check input on equal length etc
        if (x.length != y.length) {
            err("Plot at.ofai.music.plot(x,y): "
                    + "Arrays must have the same lenght.");
        }
        if (x.length <= 0 || y.length <= 0) {
            return;
        }
        if (i1 < 0 || i2 > x.length || i2 < i1) {
            err("Plot at.ofai.music.plot(): "
                    + "Indices out of range (i1/2:" + i1 + "/" + i2 + ")");
        }

        // create internal arrays for user data and screen data
        uX = new double[i2 - i1 + 1];
        uY = new double[i2 - i1 + 1]; // user data

        // just write data into the plot arrays and renew ax.minX etc. if
        // the xLimMode.equals("auto"), otherwise don't check data boundaries.
        double minx = Double.MAX_VALUE;
        double maxx = Double.MIN_VALUE;
        double miny = Double.MAX_VALUE;
        double maxy = Double.MIN_VALUE;
        for (int i = i1; i <= i2; i++) {
            if (x[i] < minx) {
                minx = x[i];
            }
            if (x[i] > maxx) {
                maxx = x[i];
            }
            if (y[i] < miny) {
                miny = y[i];
            }
            if (y[i] > maxy) {
                maxy = y[i];
            }
            if (ax.getXScale().equals("log") && x[i] <= 0) {
                uX[i - i1] = Double.NaN;
                out("at.ofai.music.Plot [xlog scaling]: ");
                err("uX data zero or negative: " + x[i]);
            } else {
                uX[i - i1] = x[i];
            }
            if (ax.getYScale().equals("log") && y[i] <= 0) {
                uY[i - i1] = Double.NaN;
                out("at.ofai.music.Plot [ylog scaling]: ");
                err("uY data zero or negative: " + y[i]);
            } else {
                uY[i - i1] = y[i];
            }
        }
        I1 = 0;
        I2 = i2 - i1;
        if (ax.getXLimMode().equals("auto")) {
            if (minx < ax.minX) {
                ax.minX = minx;
                if (ax.v) {
                    out(" minX set to: " + minx);
                }
            }
            if (maxx > ax.maxX) {
                ax.maxX = maxx;
                if (ax.v) {
                    out(", maxX set to: " + maxx);
                }
            }
        }
        if (ax.getYLimMode().equals("auto")) {
            if (miny < ax.minY) {
                ax.minY = miny;
                if (ax.v) {
                    out(", minY set to: " + miny);
                }
            }
            if (maxy > ax.maxY) {
                ax.maxY = maxy;
                if (ax.v) {
                    out(", maxY set to: " + maxy);
                }
            }
        }
        if (ax.v) {
            outn(".");
        }
        //synchronized(ax.plottableObjects) {
        ax.plottableObjects.add(this);
        //}
        ax.validate();
    } // at.ofai.music.Plot(double[], double[]) constructor

    private void init(String lineSpecs) {
        
        if (!lineSpecs.equals("")) {
            evalLineSpecs(lineSpecs, this);
        }

        if (!ax.isHold()) {
            ax.plottableObjects.clear();
            ax.clearXAxes();
            ax.clearYAxes();
            ax.minX = Double.MAX_VALUE;
            ax.maxX = Double.MIN_VALUE;
            ax.minY = Double.MAX_VALUE;
            ax.maxY = Double.MIN_VALUE;
        }
    }

    /**
     * Renders just the particular x/y value into the g2 (faster than normal
     * rendering). To be used like this:      <code> plot.addXYValue(...);
     * plot.renderPoint(ax.getAxImageGraphics(), x, y);
     * ax.repaint(); </code>
     *
     * @param g2
     * @param x
     * @param y
     */
    public void renderPoint(Graphics2D g2, Double x, Double y) {
        double xFact = ax.getXFact();
        double xShift = ax.getXShift();
        double yFact = ax.getYFact();
        double yShift = ax.getYShift();
        double sx, sy; // screen value integer
        double ox = uXL.getLast(); // old user x value
        double oy = uYL.getLast(); // old user y value
        if (isXLg()) {
            sx = Math.log10(x) * xFact + xShift;
            ox = Math.log10(ox) * xFact + xShift;
        } else {
            sx = x * xFact + xShift;
            ox = ox * xFact + xShift;
        }
        if (isYLg()) {
            sy = Math.log10(y) * yFact + yShift;
            oy = Math.log10(oy) * yFact + yShift;
        } else {
            sy = y * yFact + yShift;
            oy = oy * yFact + yShift;
        }
        drawScreenDataPoint(g2, sx, sy, ox, oy);
    }

    @Override
    public void render(Graphics2D g2) {
        double xFact = ax.getXFact();
        double xShift = ax.getXShift();
        double yFact = ax.getYFact();
        double yShift = ax.getYShift();
        g2.setColor(color);

        double ux, uy; // user value
        double sx, sy; // screen value integer
        double ox = Double.MIN_VALUE; // old screen x value
        double oy = Double.MIN_VALUE; // old y value

        if (useLinkedLists) {
            Iterator<Double> xit = uXL.iterator();
            Iterator<Double> yit = uYL.iterator();
            while (xit.hasNext()) {
                ux = xit.next();
                if (Double.isNaN(ux)) {
                    sx = Double.NaN;
                    ox = Double.MIN_VALUE;
                    continue; // not really well solved
                }
                uy = yit.next();
                if (Double.isNaN(uy)) {
                    sy = Double.NaN;
                    oy = Double.MIN_VALUE;
                    continue; // not really well solved
                }

                // (re)calc screen data in any case
                if (isXLg()) {
                    sx = Math.log10(ux) * xFact + xShift;
                } else {
                    sx = ux * xFact + xShift;
                }
                if (isYLg()) {
                    sy = Math.log10(uy) * yFact + yShift;
                } else {
                    sy = uy * yFact + yShift;
                }
                drawScreenDataPoint(g2, sx, sy, ox, oy);
                ox = sx;
                oy = sy;
            }

        } else { // use arrays       
            // go through user data, compute screen data, & plot it
            for (int i = I1; i <= I2; i++) {
                if (Double.isNaN(uX[i])) {
                    sx = Double.NaN;
                    ox = Double.MIN_VALUE;
                    continue; // not really well solved
                }
                if (Double.isNaN(uY[i])) {
                    sx = Double.NaN;
                    oy = Double.MIN_VALUE;
                    continue; // not really well solved
                }

                // (re)calc screen data in any case
                if (isXLg()) {
                    sx = Math.log10(uX[i]) * xFact + xShift;
                } else {
                    sx = uX[i] * xFact + xShift;
                }
                if (isYLg()) {
                    sy = Math.log10(uY[i]) * yFact + yShift;
                } else {
                    sy = uY[i] * yFact + yShift;
                }
                drawScreenDataPoint(g2, sx, sy, ox, oy);
                ox = sx;
                oy = sy;
            } // for loop through data
        }
    } // paint()

    private void drawScreenDataPoint(Graphics2D g, double x_, double y_, double ox_, double oy_) {
        double x = x_;
        double y = y_;
        double ox = ox_;
        double oy = oy_;
        double mScale = markerSize;

        // draw lines
        if (!lineStyle.equals("none")
                && ox != Double.MIN_VALUE && oy != Double.MIN_VALUE
                && (ax.axClip.contains(x, y) || ax.axClip.contains(ox, oy)
                || ((Rectangle2D.Double) (ax.axClip))
                .intersectsLine(x, y, ox, oy))) {
            //if (ax.doStroke) {
            switch (lineStyle) {
                case "-":
                    g.setStroke(solid(lineWidth));
                    break;
                case ":":
                    g.setStroke(PlottableObject.dotted(lineWidth));
                    break;
                case "--":
                    g.setStroke(PlottableObject.dashed(lineWidth));
                    break;
                case "-.":
                    g.setStroke(PlottableObject.dashdotted(lineWidth));
                    break;
            }
            g.setColor(color);
            g.draw(new Line2D.Double(x, y, ox, oy));
        } // if a line

        if (!marker.equals("none")) { // draw markers
            boolean changeClip = false;
            if (ax.doStroke) {
                g.setStroke(solid(lineWidth));
            }
            double X1 = ax.X1;
            double X2 = ax.X2;
            double Y1 = ax.Y1;
            double Y2 = ax.Y2;
            if (x < X1 || x > X2 || y < Y1 || y > Y2) {
                return;
            }
            if (ax.isScaleAxes()) {
                mScale = markerSize * ax.getScaleRef();
            }
            if (x - Math.ceil(mScale) <= X1 + 1
                    || x + Math.ceil(mScale) >= X2 - 1
                    || y - Math.ceil(mScale) <= Y1 + 1
                    || y + Math.ceil(mScale) >= Y2 - 1) {
                changeClip = true; //      change clip to figure for markers 
                g.setClip(ax.figClip); //                 at the border area
            }

            // FILL -- draw MarkerFaces
            if (markerFaceColor != null) {
                g.setColor(markerFaceColor);
                fillMarkerFaces(g, x, y, mScale);
            } // draw marker fills

            // draw Marker Frames
            g.setColor(color);
            if (markerEdgeColor != null) {
                g.setColor(markerEdgeColor);
            }
            drawMarkerFrames(g, x, y, mScale);

            if (changeClip) { // change clip back to axis clip
                g.setClip(ax.axClip);
            }
        } // if marker
    }

    private void fillMarkerFaces(Graphics2D g, double x, double y, double scale) {
        if (marker.equals("*")) ;//fillOval(g, xx, yy, mScale); 
        if (marker.equals(".")) ;// nothing to fill 
        if (marker.equals("+")) {
            //fillOval(g, x, y, scale);
        }
        if (marker.equals("x")) {
            //fillOval(g, x, y, scale);
        }
        if (marker.equals("s")) {
            fillSquare(g, x, y, scale);
        }
        if (marker.equals("d")) {
            fillDiamond(g, x, y, scale);
        }
        if (marker.equals("o")) {
            fillOval(g, x, y, scale);
        }
        if (marker.equals("^")) {
            drawTriangle(g, x, y, scale, true, "up");
        }
        if (marker.equals("v")) {
            drawTriangle(g, x, y, scale, true, "down");
        }
        if (marker.equals("<")) {
            drawTriangle(g, x, y, scale, true, "left");
        }
        if (marker.equals(">")) {
            drawTriangle(g, x, y, scale, true, "right");
        }
    }

    private void drawMarkerFrames(Graphics2D g, double x, double y, double scale) {
        if (marker.equals("*")) {
            drawAsterisk(g, x, y, scale);
        }
        if (marker.equals(".")) {
            drawDot(g, x, y, scale);
        }
        if (marker.equals("+")) {
            drawPlus(g, x, y, scale);
        }
        if (marker.equals("x")) {
            drawCross(g, x, y, scale);
        }
        if (marker.equals("s")) {
            drawSquare(g, x, y, scale);
        }
        if (marker.equals("d")) {
            drawDiamond(g, x, y, scale);
        }
        if (marker.equals("o")) {
            int r = (int) Math.round(scale / 2);
            if (r < 2) {
                r = 2;
            }
            g.draw(new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
        }
        if (marker.equals("^")) {
            drawTriangle(g, x, y, scale, false, "up");
        }
        if (marker.equals("v")) {
            drawTriangle(g, x, y, scale, false, "down");
        }
        if (marker.equals("<")) {
            drawTriangle(g, x, y, scale, false, "left");
        }
        if (marker.equals(">")) {
            drawTriangle(g, x, y, scale, false, "right");
        }
    }

    /**
     * If one of the given values is within the limits return true.
     *
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     * @return
     */
    /*	private boolean isOnScreen(int x1, int x2, int y1, int y2) {
     //double xx0  = ax.getXLim()[0] * ax.getXFact() + ax.getXShift();
     //double xx1  = ax.getXLim()[1] * ax.getXFact() + ax.getXShift();
     //double yy0  = ax.getYLim()[0] * ax.getYFact() + ax.getYShift();
     //double yy1  = ax.getYLim()[1] * ax.getYFact() + ax.getYShift();
     double xx0 = ax.X1;
     double xx1 = ax.X2;
     double yy0 = ax.Y1;
     double yy1 = ax.Y2;
     if ((x1 >= xx0 && x1 <= xx1) || (x2 >= xx0 && x2 <= xx1) ||
     (y1 >= yy0 && y1 <= yy1) || (y2 >= yy0 && y2 <= yy1))
     return true;
     else 
     return false;
     } // isOnScreen()*/
    protected void drawAsterisk(Graphics2D g, double x, double y, double s) {
        double r = s / 2;
        if (r < 3) {
            r = 3;
        }
        g.draw(new Line2D.Double(x - r, y, x + r, y));
        g.draw(new Line2D.Double(x, y - r, x, y + r));
        int z = (int) Math.round(Math.sqrt(r * r / 2));
        g.draw(new Line2D.Double(x - z, y - z, x + z, y + z));
        g.draw(new Line2D.Double(x - z, y + z, x + z, y - z));
    } // drawAsterisk

    protected void drawPlus(Graphics2D g, double x, double y, double s) {
        double r = s / 2;
        if (r < 3) {
            r = 3;
        }
        g.draw(new Line2D.Double(x, y - r, x, y + r));
        g.draw(new Line2D.Double(x - r, y, x + r, y));
    } // drawPlus

    protected void drawSquare(Graphics2D g, double x, double y, double s) {
        double r = s / 2;
        if (r < 3) {
            r = 3;
        }
        g.draw(new Rectangle2D.Double(x - r, y - r, 2 * r, 2 * r));
    } // drawSquare

    protected void drawDiamond(Graphics2D g, double x, double y, double s) {
        double r = s / 2;
        if (r < 3) {
            r = 3;
        }
        double X[] = new double[]{x - r, x, x + r, x};
        double Y[] = new double[]{y, y - r, y, y + r};
        g.draw(createPolygon(X, Y));
        /*
         g.draw(new Line2D.Double(x - r, y, x, y - r));
         g.draw(new Line2D.Double(x, y - r, x + r, y));
         g.draw(new Line2D.Double(x + r, y, x, y + r));
         g.draw(new Line2D.Double(x, y + r, x - r, y));
         * */
    } // drawDiamond

    protected void drawCross(Graphics2D g, double x, double y, double s) {
        double r = s / 2;
        if (r < 3) {
            r = 3;
        }
        g.draw(new Line2D.Double(x - r, y - r, x + r, y + r));
        g.draw(new Line2D.Double(x - r, y + r, x + r, y - r));
    } // drawCross

    protected void drawDot(Graphics2D g, double x, double y, double s) {
        double r = s / 4;
        if (r < 1) {
            r = 1;
        }
        g.fill(new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
    } // drawDot

    protected void fillOval(Graphics2D g, double x, double y, double s) {
        double r = s / 2;
        if (r < 3) {
            r = 3;
        }
        g.fill(new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
    } // fillOval

    protected void fillDiamond(Graphics2D g, double x, double y, double s) {
        double r = s / 2.0;
        if (r < 3) {
            r = 3;
        }
        double X[] = new double[]{x - r, x, x + r, x};
        double Y[] = new double[]{y, y - r, y, y + r};
        Path2D polygon = createPolygon(X, Y);
        g.fill(polygon);
    } // fillDiamond

    protected Path2D createPolygon(double[] X, double[] Y) {
        Path2D polygon; //GeneralPath.WIND_EVEN_ODD, X.length); // .WIND_EVEN_ODD
        polygon = new Path2D.Double();
        polygon.moveTo(X[0], Y[0]);
        for (int index = 1; index < X.length; index++) {
            polygon.lineTo(X[index], Y[index]);
        }
        polygon.closePath();
        return polygon;
    } // fillDiamond

    protected void fillSquare(Graphics2D g, double x, double y, double s) {
        double r = s / 2;
        if (r < 3) {
            r = 3;
        }
        g.fill(new Rectangle2D.Double(x - r, y - r, 2 * r, 2 * r));
    } // fillSquare

    protected void drawTriangle(Graphics2D g, double _x, double _y, double _s,
            boolean fillIt, String dir) {
        double r = _s / 2.0;
        double x = _x;
        double y = _y;
        if (r < 3) {
            r = 3.0;
        }
        double tmp = Math.sqrt(r * r - r * r / 4);
        double X[] = new double[3];
        double Y[] = new double[3];
        if (dir.equals("up") || dir.equals("down")) {
            X[0] = x - tmp;
            X[1] = x;
            X[2] = x + tmp;
        }
        if (dir.equals("down")) {
            Y[0] = y - r / 2.0;
            Y[1] = y + r;
            Y[2] = y - r / 2.0;
        }
        if (dir.equals("up")) {
            Y[0] = (y + r / 2.0);
            Y[1] = (y - r);
            Y[2] = (y + r / 2.0);
        }
        if (dir.equals("right")) {
            X[0] = (x - r / 2.0);
            X[1] = (x - r / 2.0);
            X[2] = (x + r);
        }
        if (dir.equals("left")) {
            X[0] = (x + r / 2.0);
            X[1] = (x + r / 2.0);
            X[2] = (x - r);
        }
        if (dir.equals("left") || dir.equals("right")) {
            Y[0] = (y - tmp);
            Y[1] = (y + tmp);
            Y[2] = (y);
        }
        if (fillIt) {
            Path2D p;
            g.fill(createPolygon(X, Y));
        } else {
            Path2D p;
            g.draw(createPolygon(X, Y));
        }
    } // drawTriangle

    private static double[] defaultX(int l, int i1, int i2) {
        double[] x = new double[l];
        for (int i = i1; i <= i2; i++) {
            x[i] = i - i1 + 1;
        }
        return x;
    } // defaultX

    private static LinkedList<Double> createDefaultXLinkedList(int size) {
        LinkedList<Double> ll;
        ll = new LinkedList<>();
        for (Double i = 1.0; i <= size; i = i + 1.0) {
            ll.add(i);
        }
        return ll;
    }

    private static double[] doubleToArray(double x) {
        double[] xx = new double[1];
        xx[0] = x;
        return xx;
    } // doubleToArray

    protected static double[] lili2arr(LinkedList<Double> L) {
        double[] d = new double[L.size()];
        Iterator<Double> it = L.iterator();
        int i = 0;
        while (it.hasNext()) {
            d[i++] = it.next();
        }
        return d;
    } // lili2arr

    protected static void err(String str) {
        System.err.println(str);
        System.exit(0);
    } // err

    protected static void warn(String str) {
        System.out.println("Plot.class WARNING: " + str);
    } // warn	

    /**
     * @return Returns the marker.
     * @see #setMarker(String)
     */
    public String getMarker() {
        return marker;
    }

    /**
     * @param marker The marker string to set as in <code>Matlab</code>:<br>
     * <code>"+"</code> Plus sign<br> <code>"o"</code> Circle<br>
     * <code>"*"</code> Asterisk<br> <code>"."</code> Point<br> <code>"x"</code>
     * Cross<br> <code>"s"</code> Square<br> <code>"d"</code> Diamond<br>
     * <code>"^"</code> Upward-pointing triangle<br> <code>"v"</code>
     * Downward-pointing triangle<br> <code>">"</code> Left-pointing
     * triangle<br> <code>"<"</code> Right-pointing triangle<br>
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * @return Returns the markerEdgeColor.
     */
    public Color getMarkerEdgeColor() {
        return markerEdgeColor;
    }

    /**
     * @param markerEdgeColor The markerEdgeColor to set.
     */
    public void setMarkerEdgeColor(Color markerEdgeColor) {
        this.markerEdgeColor = markerEdgeColor;
    }

    /**
     * @return Returns the markerFaceColor.
     */
    public Color getMarkerFaceColor() {
        return markerFaceColor;
    }

    /**
     * @param markerFaceColor The markerFaceColor to set.
     */
    public void setMarkerFaceColor(Color markerFaceColor) {
        this.markerFaceColor = markerFaceColor;
    }

    /**
     * @return Returns the markerSize.
     * @see #setMarkerSize(double)
     */
    public double getMarkerSize() {
        return markerSize;
    }

    /**
     * @param markerSize The markerSize to set (default value is 6.0)
     */
    public void setMarkerSize(double markerSize) {
        this.markerSize = markerSize;
    }

    protected static void evalLineSpecs(String str, Plot p) {
        //p.setLineStyle("none");
        //p.setMarker("none");
        //p.setColor(defaultColor);

        // LINESTYLE
        if (str.contains("-")) {
            p.setLineStyle("-");
        }
        if (str.contains("--")) {
            p.setLineStyle("--");
        }
        if (str.contains("-.")) {
            p.setLineStyle("-.");
        }
        if (str.contains(":")) {
            p.setLineStyle(":");
        }

        // COLOR
        if (str.contains("r")) {
            p.setColor(Color.RED);
        }
        if (str.contains("g")) {
            p.setColor(Color.GREEN);
        }
        if (str.contains("b")) {
            p.setColor(Color.BLUE);
        }
        if (str.contains("c")) {
            p.setColor(Color.CYAN);
        }
        if (str.contains("m")) {
            p.setColor(Color.MAGENTA);
        }
        if (str.contains("y")) {
            p.setColor(Color.YELLOW);
        }
        if (str.contains("k")) {
            p.setColor(Color.BLACK);
        }
        if (str.contains("w")) {
            p.setColor(Color.WHITE);
        }
        if (str.contains("l")) {
            p.setColor(Color.LIGHT_GRAY);
        }

        // MARKER 
        if (str.contains("+")) {
            p.setMarker("+");
        }
        if (str.contains("o")) {
            p.setMarker("o");
        }
        if (str.contains("*")) {
            p.setMarker("*");
        }
        if (str.contains(".")) {
            p.setMarker(".");
        }
        if (str.contains("x")) {
            p.setMarker("x");
        }
        if (str.contains("s")) {
            p.setMarker("s");
        }
        if (str.contains("d")) {
            p.setMarker("d");
        }
        if (str.contains("^")) {
            p.setMarker("^");
        }
        if (str.contains("v")) {
            p.setMarker("v");
        }
        if (str.contains("<")) {
            p.setMarker("<");
        }
        if (str.contains(">")) {
            p.setMarker(">");
        }
    } // evalLineSpecs

    private boolean isXLg() {
        return ax.getXScale().equals("log");
    }

    private boolean isYLg() {
        return ax.getYScale().equals("log");
    }

    private static void out(String str) {
        System.out.print(str);
    } // out

    private static void outn(String str) {
        System.out.println(str);
    } // outn

    public int getI1() {
        return I1;
    }

    public void setI1(int i1) {
        I1 = i1;
        //if (I1 < 0) I1 = 0;
    }

    public int getI2() {
        return I2;
    }

    public void setI2(int i2) {
        I2 = i2;
    }

    /**
     * In case a plot is constructed via a LinkedList, a value pair can be added
     * easily and quickly.
     *
     * @param x
     * @param y
     */
    public void addXYValue(Double x, Double y) {
        if (!useLinkedLists) {
            outn("Plot WARNING: this method only when constructed with LinkedLists.");
            return;
        }
        if (ax.getXLimMode().equals("auto")) {
            if (x < ax.minX) {
                ax.minX = x;
                if (ax.v) {
                    out(", minX set to: " + x);
                }
            }
            if (x > ax.maxX) {
                ax.maxX = x;
                if (ax.v) {
                    out(", maxX set to: " + x);
                }
            }
        }
        uXL.add(x);
        addYValue(y);
    }

    /**
     * In case a plot is constructed via a LinkedList, a value can be added
     * easily and quickly.
     *
     * @param y
     */
    public void addYValue(Double y) {
        if (!useLinkedLists) {
            outn("Plot WARNING: this method only when constructed with LinkedLists.");
            return;
        }
        if (ax.getYLimMode().equals("auto")) {
            if (y < ax.minY) {
                ax.minY = y;
                if (ax.v) {
                    out(", minY set to: " + y);
                }
            }
            if (y > ax.maxY) {
                ax.maxY = y;
                if (ax.v) {
                    out(", maxY set to: " + y);
                }
            }
        }
        uYL.add(y);
    }
} // Plot
