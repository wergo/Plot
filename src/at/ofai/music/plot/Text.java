package at.ofai.music.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;

/**
 * Text object of the WG at.ofai.music.plot package. Prints texts on the axes
 * either in axes coordinates or in relative position within the axes panel.
 *
 * @author Werner Goebl, Aug. 2005
 * @see Figure
 * @see Axes
 */
public class Text extends PlottableObject {

    protected double[] position = new double[2]; // x y position in the Axes
    protected String string;
    protected AttributedString attStr;
    protected String fontName;
    protected float fontStyle;
    protected float fontSize;
    protected String horizontalAlignment, verticalAlignment;
    protected double margin;
    protected double[] extent;
    protected double rotation;
    protected int isLabel; // whether that text object is a label and 
    // therefore plotted in the JPanel coordinates (rather than the 
    // axes coordinates).
    protected boolean isNormalized; // coordinates relative to axes

    /**
     * Creates a Text object containing the specified string at the user
     * coordinates x, y into the current Axes of the current Figure.
     *
     * @param x
     * @param y
     * @param string
     */
    public Text(double x, double y, String string) {
        this(x, y, string, Figure.gcf().gca());
    } // constructor

    /**
     * Creates a Text object containing the specified string at the user
     * coordinates x, y into the specified Axes object.
     *
     * @param x
     * @param y
     * @param string
     * @param ax
     */
    public Text(double x, double y, String string, Axes ax) {
        this(x, y, string, ax, 0);
    } // constructor

    /**
     * Creates a Text object containing the specified string at the user
     * coordinates x, y into the specified Axes object.
     *
     * @param x
     * @param y
     * @param string
     * @param ax
     * @param isLabel indicates whether the Text object is treated as a Label
     * ({@link Label#IS_XLABEL}, {@link Label#IS_YLABEL}) or as a Title
     * ({@link Title#IS_TITLE}) (used to create labels and title).
     */
    public Text(double x, double y, String string, Axes ax, int isLabel) {
        this.ax = ax; // parent object
        this.string = string;
        attStr = stringToAttString(string);
        this.isLabel = isLabel; // 1 = xlabel, 2 = ylabel, 3 = title
        position[0] = x;
        position[1] = y;
        color = ax.getColor();
        fontName = ax.getFontName();
        fontStyle = ax.getFontStyle();
        fontSize = ax.getFontSize();
        lineStyle = "none";
        lineWidth = defaultLineWidth;
        edgeColor = Color.BLACK;
        horizontalAlignment = "left";// {left} | center | right
        verticalAlignment = "baseline";	// top-middle-{baseline}-bottom (cap)
        margin = 3.0 + lineWidth;
        extent = new double[4];
        if (!ax.isHold() && isLabel == 0) {
            ax.plottableObjects.clear();
        }
        if (isLabel > 0) {
            ax.labelObjects.add(this);
        } else {
            ax.plottableObjects.add(this);
        }
        ax.validate();
        isNormalized = false;
    } // constructor

    /**
     * The paint method of Text object. Plots the attributedText in the
     * Graphics2D environment.
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
                    g.setStroke(PlottableObject.dotted( lineWidth * ax.getScaleFactor()));
                    break;
                case "--":
                    g.setStroke(PlottableObject.dashed( lineWidth * ax.getScaleFactor()));
                    break;
                case "-.":
                    g.setStroke(PlottableObject.dashdotted( lineWidth * ax.getScaleFactor()));
                    break;
            }
        }
        TextLayout tl;
        //FontRenderContext frc = g.getFontRenderContext();
        FontRenderContext frc = new FontRenderContext(null, true, true);
        float fs = fontSize;
        if (ax.isScaleAxes()) {
            fs *= ax.getScaleRef();
        }

        double xFact, xShift, yFact, yShift;
        float xx, yy;
        if (isLabel > 0) { // scale to fit frame limits (for labels and title)
            xx = (float) (position[0] * ax.getWidth() * ax.getScaleFactor());
            yy = (float) (position[1] * ax.getHeight() * ax.getScaleFactor());
        } else if (isNormalized) { // scale to plot texts relative to axes
            double[] axPos = ax.getPosition();
            xx = (float) (ax.getWidth() * ax.getScaleFactor() * (position[0] * axPos[2] + axPos[0]));
            yy = (float) (ax.getHeight() * ax.getScaleFactor()
                    * (1 - (position[1] * axPos[3] + axPos[1])));
        } else { // scale to fit axes limits (user space)
            xFact = ax.getXFact();
            xShift = ax.getXShift();
            yFact = ax.getYFact();
            yShift = ax.getYShift();
            if (isXLg()) {
                xx = (float) (Math.log10(position[0]) * xFact + xShift);
            } else {
                xx = (float) (position[0] * xFact + xShift);
            }
            if (isYLg()) {
                yy = (float) (Math.log10(position[1]) * yFact + yShift);
            } else {
                yy = (float) (position[1] * yFact + yShift);
            }
        }

        attStr.addAttribute(TextAttribute.SIZE, fs);
        attStr.addAttribute(TextAttribute.FAMILY, fontName);
        attStr.addAttribute(TextAttribute.WEIGHT, fontStyle);
        tl = new TextLayout(attStr.getIterator(), frc);
        Rectangle2D r = tl.getBounds();

        float X = xx;
        float Y = yy; // store original point for rotation center

        if (horizontalAlignment.equals("left")); // default (do nothing)
        if (horizontalAlignment.equals("center")) {
            xx -= r.getWidth() / 2;
        }
        if (horizontalAlignment.equals("right")) {
            xx -= r.getWidth();
        }

        if (verticalAlignment.equals("top")) {
            yy += tl.getAscent() - tl.getDescent();
        }
        if (verticalAlignment.equals("middle")) {
            yy = yy + (tl.getAscent() - tl.getDescent()) / 2;
        }
        if (verticalAlignment.equals("baseline")) {
        } // do nothing
        //if (t.verticalAlignment.equals("cap")) 	
        if (verticalAlignment.equals("bottom")) {
            yy -= tl.getDescent();
        }

        extent[0] = r.getX() + xx - (margin +  lineWidth * ax.getScaleFactor());
        extent[1] = r.getY() + yy - (margin +  lineWidth * ax.getScaleFactor());
        extent[2] = r.getWidth() + 2 * (margin +  lineWidth * ax.getScaleFactor());
        extent[3] = r.getHeight() + 2 * (margin +  lineWidth * ax.getScaleFactor());

        //if (isLabel == 3) { // title object
        //attStr.
        //}
        r.setRect(extent[0], extent[1], extent[2], extent[3]);

        g.rotate(rotation / 180.0 * Math.PI, X, Y);
        if (faceColor != null) {
            g.setColor(faceColor);
            if (ax.doStroke) {
                g.fill(r);
            } else {
                g.fillRect((int) Math.round(extent[0]),
                        (int) Math.round(extent[1]),
                        (int) Math.round(extent[2]),
                        (int) Math.round(extent[3]));
            }
        }
        if (edgeColor != null && !lineStyle.equals("none")) {
            g.setColor(edgeColor);
            if (ax.doStroke) {
                g.draw(r);
            } else {
                g.drawRect((int) Math.round(extent[0]),
                        (int) Math.round(extent[1]),
                        (int) Math.round(extent[2]),
                        (int) Math.round(extent[3]));
            }
        }
        g.setColor(color);
        tl.draw(g, xx, yy);
        g.rotate(-rotation / 180.0 * Math.PI, X, Y);
    } // drawText

    /**
     * Converts a String to an AttributedString by superscripting all characters
     * following a "^" and subscripting all after a "_", very much like in
     * LaTeX.
     * <p>
     * E.g.: <code>"10^3"</code> or to superscript multiple characters:
     * <code>"10^{100}", "X_{12}"</code>
     *
     * @param str input string.
     * @return AttributedString with superscript and subscript attributes set.
     */
    public static AttributedString stringToAttString(String str) {
        if (str == null) {
            return null;
        }
        AttributedString as = new AttributedString("");
        if (str.length() == 0) {
            return as;
        }
        int[] superScripts = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            String ss = str.substring(i, i + 1);
            int sup = 0;
            if (ss.equals("^") || ss.equals("_")) {
                if (ss.equals("^")) {
                    sup = TextAttribute.SUPERSCRIPT_SUPER;
                } else {
                    sup = TextAttribute.SUPERSCRIPT_SUB;
                }

                if (i + 2 > str.length()) { // nothing behind ^ or _
                    str = cutOut(str, i);
                    break;
                }
                if (str.substring(i + 1, i + 2).equals("{")) {
                    str = cutOut(str, i);
                    str = cutOut(str, i);
                    while (i < str.length()) {
                        if (str.substring(i, i + 1).equals("}")) {
                            str = cutOut(str, i);
                            break;
                        }
                        superScripts[i++] = sup;
                    }
                } else if (i - 1 >= 0) { // check previous
                    if (str.codePointAt(i - 1) == 0x5C) { // backslash (92)
                        str = cutOut(str, i - 1);
                        superScripts[i] = 0;
                    } else { // TODO write this in a nicer way...					
                        superScripts[i] = sup;
                        str = cutOut(str, i);
                    }
                } else {
                    superScripts[i] = sup;
                    str = cutOut(str, i);
                }
            } else {
                superScripts[i] = sup;
            }
        } // for

        as = new AttributedString(str);
        for (int i = 0; i < str.length(); i++) {
            if (superScripts[i] == TextAttribute.SUPERSCRIPT_SUPER) {
                as.addAttribute(TextAttribute.SUPERSCRIPT,
                        1, i, i + 1);
            } else if (superScripts[i] == TextAttribute.SUPERSCRIPT_SUB) {
                as.addAttribute(TextAttribute.SUPERSCRIPT,
                        -1, i, i + 1);
            }
        }
        return as;
    } // stringToAttString

    /**
     * General purpose routine: Returns a string with one element (at position
     * <code>index</code>) cut out.
     *
     * @param str input string.
     * @param index index of element to be removed.
     * @return String with str.length()-1.
     */
    public static String cutOut(String str, int index) {
        if (index == 0 && str.length() > 0) {
            return str.substring(index + 1, str.length());
        } else if (index > 0 && index < str.length() - 1) {
            return str.substring(0, index) + str.substring(index + 1, str.length());
        } else if (index == str.length() - 1) {
            return str.substring(0, index);
        } else {
            return null;
        }
    } // cutOut

    /**
     * Sets the string of the Text object.
     *
     * @param str
     */
    public void setString(String str) {
        string = str;
        attStr = stringToAttString(str);
    } // setString(String)

    /**
     * Returns string of text object.
     *
     * @return String of the text object.
     * @see #setString(String)
     */
    public String getString() {
        return string;
    } // getString()

    /**
     * Sets the position (double[2] x y) of the text.
     *
     * @param pos
     * @see #getHorizontalAlignment()
     * @see #setHorizontalAlignment(String)
     * @see #getVerticalAlignment()
     * @see #setVerticalAlignment(String)
     */
    public void setPosition(double[] pos) {
        if (pos.length != 2) {
            System.err.println("Text.setPosition: position is a double[2].");
        }
        position = pos;
    } // setPosition

    /**
     * @return Returns position of text (double[2]).
     * @see #setPosition(double[])
     */
    public double[] getPosition() {
        return position;
    } // getPosition

    /**
     * Sets the name of the font (e.g., "Helvethica").
     *
     * @param fontName
     */
    public void setFontName(String fontName) {
        this.fontName = fontName;
    } // setFontName

    /**
     * Returns the name of the font.
     *
     * @return returns the name of the font.
     */
    public String getFontName() {
        return fontName;
    } // getFontName

    /**
     * Sets the fontStyle of the text ({@link TextAttribute}).
     *
     * @param fontStyle The fontStyle to set.
     * @see #getFontStyle()
     */
    public void setFontStyle(float fontStyle) {
        this.fontStyle = fontStyle;
    }

    /**
     * @return Returns the fontStyle ({@link TextAttribute}).
     */
    public float getFontStyle() {
        return fontStyle;
    }

    /**
     * Sets the font size (default = 12f).
     *
     * @param fontSize
     */
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Returns the font size (float).
     *
     * @return returns the font size
     */
    public float getFontSize() {
        return fontSize;
    }

    /**
     * Sets the horizontalAlignment of the text object as String:
     * <code>left</code> (default) - <code>center</code> - <code>right</code>.
     *
     * @param horizontalAlignment String specifying the horizontal alignment of
     * text object.
     */
    public void setHorizontalAlignment(String horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    /**
     * Gets the horizontalAlignment of the text object as String:
     * <code>left</code> (default) - <code>center</code> - <code>right</code>.
     *
     * @return String specifying the horizontal alignment of text object.
     */
    public String getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Sets the horizontalAlignment of the text object as String:
     * <code>bottom</code> - <code>baseline</code> (default) -
     * <code>middle</code> - <code>top</code>.
     *
     * @param verticalAlignment String specifying the vertical alignment of text
     * object.
     */
    public void setVerticalAlignment(String verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    /**
     * Gets the horizontalAlignment of the text object as String:
     * <code>bottom</code> - <code>baseline</code> (default) -
     * <code>middle</code> - <code>top</code>.
     *
     * @return String specifying the vertical alignment of text object.
     */
    public String getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Sets the text object's rotation in degrees (0--360) counterclockwise.
     *
     * @param rotation The rotation to set.
     * @see #getRotation()
     */
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    /**
     * @return Returns the rotation.
     * @see #setRotation(double)
     */
    public double getRotation() {
        return rotation;
    }

    protected boolean isXLg() {
        return ax.getXScale().equals("log");
    }

    protected boolean isYLg() {
        return ax.getYScale().equals("log");
    }

    protected static void out(String str) {
        System.out.print(str);
    } // out

    protected static void outn(String str) {
        System.out.println(str);
    } // outn

    /**
     * @return Returns the isLabel.
     */
    protected int getIsLabel() {
        return isLabel;
    }

    /**
     * isLabel specifies what type of label the text object is:
     *
     * @param isLabel The isLabel to set.
     */
    protected void setIsLabel(int isLabel) {
        this.isLabel = isLabel;
    }

    /**
     * @return Returns the isNormalized.
     * @see #setNormalized(boolean)
     */
    public boolean isNormalized() {
        return isNormalized;
    }

    /**
     * Sets the x, y coordinates relative to axes. E.g.,
     * <code>ax.text(.1, .3, "qwer", ax);</code> creates the text at 10% left on
     * the x axis and 30% from the bottom of the y axis irrespective of axis
     * orientation ({@link Axes#setXDir(String)}, {@link Axes#setYDir(String)})
     * or location null null null null     {@link Axes#setXAxisLocation(String)}, 
	 * {@link Axes#setXAxisLocation(String)}).
     *
     * @param isNormalized The isNormalized to set.
     *
     */
    public void setNormalized(boolean isNormalized) {
        this.isNormalized = isNormalized;
    }

    /**
     * @return Returns the extent.
     */
    public double[] getExtent() {
        return extent;
    }

    /**
     * @param extent The extent to set.
     */
    public void setExtent(double[] extent) {
        this.extent = extent;
    }

    /**
     * @return Returns the margin.
     */
    public double getMargin() {
        return margin;
    }

    /**
     * @param margin The margin to set.
     */
    public void setMargin(double margin) {
        this.margin = margin;
    }
} // Text
