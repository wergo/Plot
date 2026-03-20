package at.ofai.music.plot;

import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Creates and adds a (rounded) rectangle object to the axes object.
 * <p>
 * Is also able to create ellipses and circles using
 * {@link Rectangle#setXCurvature(double)} or
 * {@link Rectangle#setYCurvature(double)}.
 * <p>
 *
 * @author Werner Goebl, Aug. 2005
 */
public class Rectangle extends PlottableObject {

    protected double[] position; // x y w h
    protected double xCurvature, yCurvature;
    protected double rotation;   // rotation of the 

    /**
     * Creates a rectangle at (0,0) with (1,1) size and no curvature.
     *
     * @param a Reference to the Axes object.
     */
    public Rectangle(Axes a) {
        this(0.0, 0.0, 1.0, 1.0, 0.0, 0.0, a);
    }

    /**
     * Creates a rectangle object with the parameters x, y, w, h at the current
     * axes of the current figure with no curvature.
     *
     * @param x x position (from lower left)
     * @param y y position (from lower left)
     * @param w width
     * @param h height
     */
    public Rectangle(double x, double y, double w, double h) {
        this(x, y, w, h, Figure.gcf().gca());
    }

    /**
     * Creates a rectangle object with the parameters x, y, w, h with no
     * curvature.
     *
     * @param x x position (from lower left)
     * @param y y position (from lower left)
     * @param w width
     * @param h height
     * @param a Axes to which the objects gets added
     */
    public Rectangle(double x, double y, double w, double h, Axes a) {
        this(x, y, w, h, 0.0, 0.0, a);
    }

    /**
     * Creates a rectangle object with curvature at the current axes of the
     * current figure.
     *
     * @param x x position (from lower left)
     * @param y y position (from lower left)
     * @param w width
     * @param h height
     * @param xc xCurvature (from 0 = no to 1 = full ellipse)
     * @param yc yCurvature (from 0 = no to 1 = full ellipse)
     */
    public Rectangle(double x, double y, double w, double h,
            double xc, double yc) {
        this(x, y, w, h, xc, yc, Figure.gcf().gca());
    }

    /**
     * Creates a rectangle object with curvature.
     *
     * @param x x position (from lower left)
     * @param y y position (from lower left)
     * @param w width
     * @param h height
     * @param xc xCurvature (from 0 = no to 1 = full ellipse)
     * @param yc yCurvature (from 0 = no to 1 = full ellipse)
     * @param a Axes to which rectangle gets added
     */
    public Rectangle(double x, double y, double w, double h,
            double xc, double yc, Axes a) {
        ax = a; // parent object
        position = new double[4];
        lineStyle = defaultLineStyle; // none [-] -. -- :
        lineWidth = defaultLineWidth; // default is 1f
        edgeColor = defaultColor;
        faceColor = null;
        if (!ax.isHold()) {
            ax.plottableObjects.clear();
        }
        ax.plottableObjects.add(this);
        position[0] = x;
        position[1] = y;
        position[2] = w;
        position[3] = h;
        xCurvature = xc;
        yCurvature = yc;
        ax.validate();
    }

    /**
     *
     * @param g
     */
    @Override
    public void render(Graphics2D g) {
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
        }
        if (xCurvature < 0 || xCurvature > 1) {
            System.err.println(
                    "Rectangle(): xCurvature must be between 0 and 1.");
        }
        if (yCurvature < 0 || yCurvature > 1) {
            System.err.println(
                    "Rectangle(): yCurvature must be between 0 and 1.");
        }

        double x, y, w, h, arcW, arcH;
        double xFact = ax.getXFact();
        double xShift = ax.getXShift();
        double yFact = ax.getYFact();
        double yShift = ax.getYShift();

        if (isXLg()) {
            x = Math.log10(position[0]) * xFact + xShift;
            w = Math.log10(position[0] + position[2]) * xFact + xShift - x;
        } else {
            x = position[0] * xFact + xShift;
            w = position[2] * xFact;
        }
        if (isYLg()) {
            y = Math.log10(position[1] + position[3]) * yFact + yShift;
            h = Math.abs(Math.log10(position[1]) * yFact + yShift - y);
        } else {
            y = (position[1] + position[3]) * yFact + yShift;
            h = Math.abs(Math.round(position[3] * yFact));
        }
        arcW = xCurvature * w;
        arcH = yCurvature * h;

        // draw the rectangle
        g.rotate(rotation / 180.0 * Math.PI, x, y + h);
        if (faceColor != null) {
            g.setColor(faceColor);
            if (ax.doStroke) {
                g.fill(new RoundRectangle2D.Double(x, y, w, h, arcW, arcH));
            } else {
                g.fillRoundRect((int) Math.round(x), (int) Math.round(y),
                        (int) Math.round(w), (int) Math.round(h),
                        (int) Math.round(arcW), (int) Math.round(arcH));
            }
        }
        if ((!lineStyle.equals("none")) && lineWidth > 0 && edgeColor != null) {
            g.setColor(edgeColor);
            if (ax.doStroke) {
                g.draw(new RoundRectangle2D.Double(x, y, w, h, arcW, arcH));
            } else {
                g.drawRoundRect((int) Math.round(x), (int) Math.round(y),
                        (int) Math.round(w), (int) Math.round(h),
                        (int) Math.round(arcW), (int) Math.round(arcH));
            }

        }
        g.rotate(-rotation / 180.0 * Math.PI, x, y + h);
    } // drawRectangle

    private boolean isXLg() {
        return ax.getXScale().equals("log");
    }

    private boolean isYLg() {
        return ax.getYScale().equals("log");
    }

    /**
     * @return Returns the position.
     */
    public double[] getPosition() {
        return position;
    }

    /**
     * @param position The position to set.
     */
    public void setPosition(double[] position) {
        this.position = position;
    }

    /**
     * @return Returns the xCurvature. From 0 = no curvature to 1 = full
     * curvature (=ellipse, circle)
     */
    public double getXCurvature() {
        return xCurvature;
    }

    /**
     * @param curvature The xCurvature to set. From 0 = no curvature to 1 = full
     * curvature (=ellipse, circle)
     */
    public void setXCurvature(double curvature) {
        xCurvature = curvature;
    }

    /**
     * @return Returns the yCurvature. From 0 = no curvature to 1 = full
     * curvature (=ellipse, circle)
     */
    public double getYCurvature() {
        return yCurvature;
    }

    /**
     * @param curvature The yCurvature to set. From 0 = no curvature to 1 = full
     * curvature (=ellipse, circle)
     */
    public void setYCurvature(double curvature) {
        yCurvature = curvature;
    }

    /**
     * @return Returns the rotation.
     * @see #setRotation(double)
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation of the rectangle (in degrees from 0 to 360 clockwise).
     * Rotation point is the lower-left corner of the rectangle object.
     *
     * @param rotation The rotation to set.
     *
     */
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
    //private static void out(String str)  { System.out.print(str);  } // out
    //private static void outn(String str) { System.out.println(str);} // outn
} // Rectangle
