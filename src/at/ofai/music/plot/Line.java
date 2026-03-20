package at.ofai.music.plot;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

/**
 * Creates a line into an axes.
 *
 * @author Werner Goebl, Aug. 2005
 * @see Figure
 * @see Axes
 * @see PlottableObject
 */
public class Line extends PlottableObject {

    private double[] uX1, uY1, uX2, uY2; // user data 

    /**
     * Creates a Line object with the specified coordinates into the current
     * axes of the current figure.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public Line(double x1, double y1, double x2, double y2) {
        this(x1, y1, x2, y2, Figure.gcf().gca());
    } // Line

    /**
     * Creates a Line object with the specified coordinates into the specified
     * axes object.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param a
     */
    public Line(double x1, double y1, double x2, double y2, Axes a) {
        this(d2a(x1), d2a(y1), d2a(x2), d2a(y2), a);
    } // Line

    /**
     * Creates Line objects with the specified coordinate arrays into the
     * current axes of the current figure. Arrays must have the same length.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public Line(double[] x1, double[] y1, double[] x2, double[] y2) {
        this(x1, y1, x2, y2, Figure.gcf().gca());
    } // Line

    /**
     * Creates Line objects with the specified coordinate arrays into the
     * specified axes object. Arrays must have the same length.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param a
     */
    public Line(double[] x1, double[] y1, double[] x2, double[] y2, Axes a) {
        ax = a; // parent object
        lineStyle = defaultLineStyle;
        lineWidth = defaultLineWidth;
        if (!ax.isHold()) {
            ax.plottableObjects.clear();
        }
        ax.plottableObjects.add(this);
        if (x1.length != x2.length || x1.length != y1.length
                || x1.length != y2.length) {
            System.err.print("Line: Arrays must have the same length.");
        }
        uX1 = new double[x1.length];
        uX2 = new double[x1.length];
        uY1 = new double[x1.length];
        uY2 = new double[x1.length];
        for (int i = 0; i < x1.length; i++) {
            uX1[i] = x1[i];
            uX2[i] = x2[i];
            uY1[i] = y1[i];
            uY2[i] = y2[i];
            // TODO if auto lim, change the x/y lims of the axis if I want this		
        }
        ax.validate();
    } // at.ofai.music.plot.Line Constructor

    @Override
    public void render(Graphics2D g) {
        g.setColor(color);
        for (int i = 0; i < uX1.length; i++) {
            double xFact = ax.getXFact();
            double xShift = ax.getXShift();
            double yFact = ax.getYFact();
            double yShift = ax.getYShift();
            double xx1, yy1, xx2, yy2;
            if (isXLg()) {
                xx1 = Math.log10(uX1[i]) * xFact + xShift;
                xx2 = Math.log10(uX2[i]) * xFact + xShift;
            } else {
                xx1 = uX1[i] * xFact + xShift;
                xx2 = uX2[i] * xFact + xShift;
            }
            if (isYLg()) {
                yy1 = Math.log10(uY1[i]) * yFact + yShift;
                yy2 = Math.log10(uY2[i]) * yFact + yShift;
            } else {
                yy1 = uY1[i] * yFact + yShift;
                yy2 = uY2[i] * yFact + yShift;
            }

            if (ax.doStroke) {
                switch (lineStyle) {
                    case "-":
                        g.setStroke(solid(lineWidth * ax.getScaleFactor()));
                        break;
                    case ":":
                        g.setStroke(PlottableObject.dotted(lineWidth * ax.getScaleFactor()));
                        break;
                    case "--":
                        g.setStroke(PlottableObject.dashed(lineWidth * ax.getScaleFactor()));
                        break;
                    case "-.":
                        g.setStroke(PlottableObject.dashdotted(lineWidth * ax.getScaleFactor()));
                        break;
                }
                //if (lineWidth <= 0.5 || true)
                g.draw(new Line2D.Double(xx1, yy1, xx2, yy2));
            } else {
                g.drawLine((int) Math.round(xx1), (int) Math.round(yy1),
                        (int) Math.round(xx2), (int) Math.round(yy2));
            }
            /*else { // old code for drawing thicker lines
             double a = xx2 - xx1;
             double b = yy2 - yy1;
             double c = Math.sqrt(a*a + b*b);
             double d = lineWidth * 2;
             if (c != 0) {
             double alph = Math.acos(a/c);
             //outn("a="+a+", b="+b+", c="+c+", alpha="+alph+", a/c="+(a/c));
             //int q = 20; g.drawOval(xx1-q,yy1-q,2*q,2*q); 
             //g.drawOval(xx2-q,yy2-q,2*q,2*q);
             int s = (int)(b/Math.abs(b));
             g.rotate(s*alph,xx1,yy1);
             double d2 = d / 2;
             g.fill(new RoundRectangle2D.Double(xx1-d2,yy1-d2,c+d,d,d,d));
             //g.fillRoundRect(xx1-d2,yy1-d2,(int)Math.round(c+d),d,d,d);
             g.rotate(s*-alph,xx1,yy1);
             }
             }*/

        } // for loop through data
    } // paint

    /**
     * Redefines the data of a line object.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void setData(double[] x1, double[] y1, double[] x2, double[] y2) {
        if (x1.length != x2.length || x1.length != y1.length
                || x1.length != y2.length) {
            System.err.print("Line: Arrays must have the same length.");
        }
        uX1 = new double[x1.length];
        uX2 = new double[x1.length];
        uY1 = new double[x1.length];
        uY2 = new double[x1.length];
        for (int i = 0; i < x1.length; i++) {
            uX1[i] = x1[i];
            uX2[i] = x2[i];
            uY1[i] = y1[i];
            uY2[i] = y2[i];
        }
        ax.validate();
    }

    /**
     * Redefines the data of a line object.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void setData(double x1, double y1, double x2, double y2) {
        uX1 = new double[1];
        uX2 = new double[1];
        uY1 = new double[1];
        uY2 = new double[1];
        int i = 0;
        uX1[i] = x1;
        uX2[i] = x2;
        uY1[i] = y1;
        uY2[i] = y2;
        ax.validate();
    } // setData(x1,y1,x2,y2)

    private static double[] d2a(double x) {
        double[] xx = new double[1];
        xx[0] = x;
        return xx;
    } // d2a

    private boolean isXLg() {
        return ax.getXScale().equals("log");
    }

    private boolean isYLg() {
        return ax.getYScale().equals("log");
    }
    //private static void out(String str)  { System.out.print(str);  } // out
    //private static void outn(String str) { System.out.println(str);} // outn
} // Line
