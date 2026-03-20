package at.ofai.music.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * General plotable template object of a figure axes with basic properties
 * (abstract class).
 *
 * @author Werner Goebl, Aug. 2005
 * @see Figure
 * @see Axes
 * @see Plot
 * @see Text
 * @see Line
 * @see Rectangle
 * @see WormPlot
 */
public abstract class PlottableObject {

    protected static final String defaultLineStyle = "-";
    protected static final float defaultLineWidth = 1f;
    protected static final Color defaultColor = Color.BLUE;
    protected String lineStyle = defaultLineStyle;
    protected float lineWidth = defaultLineWidth;
    protected Color color = defaultColor;
    protected Color edgeColor = defaultColor;
    protected Color faceColor = null;
    protected Axes ax = null;

    public abstract void render(Graphics2D g);

    /**
     * Template stroke for a solid line with width lineWidth
     *
     * @param lineWidth width of the line
     * @return Stroke object
     */
    public static Stroke solid(float lineWidth) {
        return new BasicStroke(lineWidth, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_MITER, 10.0f);
    } // solid

    /**
     * Template stroke for a dashed line with width lineWidth
     *
     * @param lineWidth width of the line
     * @return Stroke object
     */
    public static Stroke dashed(float lineWidth) {
        return new BasicStroke(lineWidth, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f * lineWidth, 
                new float[]{10.0f * lineWidth, 5.0f * lineWidth}, 0.0f);
    } // dashed

    /**
     * Template stroke for a dotted line with width lineWidth.
     *
     * @param lineWidth width of the line
     * @return Stroke object
     */
    public static Stroke dotted(float lineWidth) {
        return new BasicStroke(lineWidth, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 10.0f * lineWidth,
                new float[]{0.0f, lineWidth * 2f}, 0.0f);
    } // dotted

    /**
     * Template stroke for a dashdotted line with width lineWidth
     *
     * @param lineWidth width of the line
     * @return Stroke object
     */
    public static Stroke dashdotted(float lineWidth) {
        return new BasicStroke(lineWidth, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 10.0f * lineWidth,
                new float[]{10.0f * lineWidth, lineWidth * 2f, 0.0f, lineWidth * 2f}, 0.0f);
    } // dashdotted

    /**
     * @return Returns the color of the PlottableObject.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color The color of the PlottableObject to set.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return Returns the edgeColor.
     */
    public Color getEdgeColor() {
        return edgeColor;
    }

    /**
     * @param edgeColor The edgeColor to set.
     */
    public void setEdgeColor(Color edgeColor) {
        this.edgeColor = edgeColor;
    }

    /**
     * @return Returns the faceColor (fill).
     */
    public Color getFaceColor() {
        return faceColor;
    }

    /**
     * @param faceColor The faceColor (fill) to set.
     */
    public void setFaceColor(Color faceColor) {
        this.faceColor = faceColor;
    }

    /**
     * @return Returns the lineStyle
     * @see #setLineStyle(String)
     */
    public String getLineStyle() {
        return lineStyle;
    }

    /**
     * The line style to set (as a string) according to the <code>Matlab</code>
     * LineSpecs:<br>
     * <code>"none"</code> no line<br>
     * <code>"-" </code> solid line<br>
     * <code>"--"</code> dashed line<br>
     * <code>":" </code> dotted line<br>
     * <code>"-."</code> dash-dot line<br> The default line style is a solid
     * line ("-"). Note: if you change this parameter, the paint methods invoke
     * stroke commands which might slow down redrawing.
     *
     * @param lineStyle The lineStyle to set (default solid "-").
     */
    public void setLineStyle(String lineStyle) {
        ax.doStroke = true;
        this.lineStyle = lineStyle;
    }

    /**
     * Returns the parent Axes object of the plottable object.
     *
     * @return Returns the ax.
     */
    public Axes getAxes() {
        return ax;
    }

    /**
     * @return Returns the lineWidth. Default value is 1.5f.
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets the line width of the PlottableObject. The default value is 1f.
     * Note: if you change this parameter, the paint methods invoke stroke
     * commands which might slow down redrawing.
     *
     * @param lineWidth The lineWidth to set. Default value is 1f.
     */
    public void setLineWidth(float lineWidth) {
        ax.doStroke = true;
        this.lineWidth = lineWidth;
    }
} // PlottableObject
