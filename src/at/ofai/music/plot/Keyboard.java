package at.ofai.music.plot;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Draws a keyboard onto the axes with typical patterns of black and white keys
 * linked to MIDI note numbers. They range typically between 21 (subkontra A or
 * A0) to 108 (c''''' or C8). An exception is the Boesendorfer Imperial which
 * starts with the subkontra C C0 or Midi pitch 12. <p> E.g., new
 * <code>Keyboard("left",true);</code> <p>
 *
 * @author Werner Goebl, Nov. 2005
 * @see PlottableObject
 * @see Axes
 * @see Figure
 */
public class Keyboard extends PlottableObject {

    private String position = "left";
    protected int[] bKeys = {2, 4, 7, 9, 11};
    private boolean blackKeysTopLeft = true;
    private double depth = -1.0;
    protected static final Color defaultKeyboardColor = new Color(200, 200, 200);
    protected static final Color defaultMiddleCColor = new Color(235, 235, 235);
    protected static final Color defaultAdditionalKeysColor =
            new Color(230, 230, 230);
    protected Color frameColor;
    protected Color middleCColor;
    protected Color additionalKeysColor;
    protected double[] midiNoteNumbers = {12.0, 24, 36, 48, 60, 72, 84, 96, 108.0};
    protected String[] englishNoteLabels = {"C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8"};
    protected String[] germanNoteLabels = {"C_{sk}", "C_k", "C", "c", "c^1", "c^2", "c^3", "c^4", "c^5"};

    /**
     * Default constructor. Adds to the axes a keyboard to the left side with
     * the black keys attached to the left axis frame.
     */
    public Keyboard() {
        this("left");
    } // constructor

    /**
     * Constructs a keyboard at the specified position.
     *
     * @param position ["left"] "right" "top" "bottom"
     */
    public Keyboard(String position) {
        this(position, true);
    } // constructor

    /**
     * Constructs a keyboard at the specified position ("left" "right" "top"
     * "bottom") with the black keys being attached to either the left or the
     * top side of the keyboard frame (or the reverse).
     *
     * @param position ["left"] "right" "top" "bottom" "leftfix" "rightfix"
     * @param blackKeysTopLeft if true (default) the black keys are attached to
     * either the left or the top side of the keyboard frame or the reverse
     * (false)
     */
    public Keyboard(String position, boolean blackKeysTopLeft) {
        this(position, blackKeysTopLeft, -1.0);
    } // constructor

    /**
     * Constructs a keyboard at the specified position ("left" "right" "top"
     * "bottom") with the black keys being attached to either the left or the
     * top side of the keyboard frame (or the reverse) and with the specified
     * depth (in plotting units of the axes).
     *
     * @param position ["left"] "right" "top" "bottom" "leftfix" "rightfix"
     * @param blackKeysTopLeft if true (default) the black keys are attached to
     * either the left or the top side of the keyboard frame or the reverse
     * (false)
     * @param depth the depth of the keyboard (length of the keys) in axes units
     */
    public Keyboard(String position, boolean blackKeysTopLeft, double depth) {
        this(position, blackKeysTopLeft, depth, Figure.gcf().gca());
    } // constructor

    /**
     * Constructs a keyboard at the specified position ("left" "right" "top"
     * "bottom") with the black keys being attached to either the left or the
     * top side of the keyboard frame (or the reverse) and with the specified
     * depth (in plotting units of the axes).
     *
     * @param position ["left"] "right" "top" "bottom" "leftfix" "rightfix"
     * @param blackKeysTopLeft if true (default) the black keys are attached to
     * either the left or the top side of the keyboard frame or the reverse
     * (false)
     * @param depth the depth of the keyboard (length of the keys) in axes units
     * @param axes the axes object the Keyboard is to be attached
     */
    public Keyboard(String position, boolean blackKeysTopLeft, double depth,
            Axes axes) {
        this.position = position;
        this.blackKeysTopLeft = blackKeysTopLeft;
        this.depth = depth;
        ax = axes;
        color = defaultKeyboardColor;
        frameColor = ax.getColor();
        middleCColor = defaultMiddleCColor;
        additionalKeysColor = defaultAdditionalKeysColor;
        if (!ax.isHold()) {
            ax.plottableObjects.clear();
        }
        ax.holdOn();
        boolean add = ax.plottableObjects.add(this);
        ax.validate();
    } // constructor

    /**
     * Paint method
     * @param g
     */
    @Override
    public void render(Graphics2D g) {
        boolean isHor = false;
        if (position.equalsIgnoreCase("left")
                || position.equalsIgnoreCase("right")
                || position.equalsIgnoreCase("rightfix")
                || position.equalsIgnoreCase("leftfix")) {
            isHor = false;
        } else if (position.equalsIgnoreCase("top")
                || position.equalsIgnoreCase("bottom")) {
            isHor = true;
        } else {
            error("Keyboard: String position must either be "
                    + "left, leftfix, right, rightfix, top, or bottom.");
        }
        double xFact = ax.getXFact();
        double xShift = ax.getXShift();
        double yFact = ax.getYFact();
        double yShift = ax.getYShift();
        double dFact = 0.0, wFact = 0.0, wShift = 0.0;
        int d1 = 0, w1 = 0, d2 = 0, w2 = 0;
        // TODO make also keyboard axis logarithmicable
/*			if (isXLg()) {
         xx1 = Math.log10(uX1[i]) * xFact + xShift;
         xx2 = Math.log10(uX2[i]) * xFact + xShift;
         } else {
         xx1 = uX1[i] * xFact + xShift;
         xx2 = uX2[i] * xFact + xShift;
         }*/
        if (isHor) {
            if (isXLg()) {
                error("Keyboard: x axis must not be logarithmic");
            }
            dFact = yFact;
            if (depth <= 0) {
                depth = (ax.getYLim()[1] - ax.getYLim()[0]) / 10;
            }
            wFact = xFact;
            wShift = xShift;
        } else {
            if (isYLg()) {
                error("Keyboard: y axis must not be logarithmic");
            }
            dFact = xFact;
            if (depth <= 0) {
                depth = (ax.getXLim()[1] - ax.getXLim()[0]) / 10;
            }
            wFact = yFact;
            wShift = yShift;
        }
        if (position.equalsIgnoreCase("left")) {
            d1 = ax.X1;
            d2 = d1 + (int) Math.round(depth * dFact);
            w1 = ax.Y1;
            w2 = ax.Y2;
        } else if (position.equalsIgnoreCase("leftfix")) {
            d1 = ax.X1;
            d2 = d1 + (int) Math.round((ax.getXZoom()[1] - ax.getXZoom()[0])
                    / depth * dFact);
            w1 = ax.Y1;
            w2 = ax.Y2;
        } else if (position.equalsIgnoreCase("right")) {
            d1 = ax.X2;
            d2 = d1 - (int) Math.round(depth * dFact);
            w1 = ax.Y1;
            w2 = ax.Y2;
        } else if (position.equalsIgnoreCase("rightfix")) {
            d1 = ax.X2;
            d2 = d1 - (int) Math.round((ax.getXZoom()[1] - ax.getXZoom()[0])
                    / depth * dFact);
            w1 = ax.Y1;
            w2 = ax.Y2;
        } else if (position.equalsIgnoreCase("bottom")) {
            d1 = ax.Y2;
            //System.out.println("DIFF: "+ ((int)Math.round(depth * dFact)));
            d2 = d1 + (int) Math.round(depth * dFact);
            w1 = ax.X1;
            w2 = ax.X2;
        } else if (position.equalsIgnoreCase("top")) {
            d1 = ax.Y1;
            d2 = d1 - (int) Math.round(depth * dFact);
            w1 = ax.X1;
            w2 = ax.X2;
        }
        if (d2 - d1 < 0) {
            int tmp = d1;
            d1 = d2;
            d2 = tmp;
        }
        if (w2 - w1 < 0) {
            int tmp = w1;
            w1 = w2;
            w2 = tmp;
        }
        if (ax.doStroke) {
            g.setStroke(solid(lineWidth));
        }
        g.setColor(ax.getBackgroundColor());
        if (isHor) {
            g.fillRect(w1 + 1, d1 + 1, w2 - w1 - 2, d2 - d1 - 2);
        } else {
            g.fillRect(d1 + 1, w1 + 1, d2 - d1 - 2, w2 - w1 - 2);
        }
        g.setColor(color);
        for (int octs = 11; octs <= 96; octs += 12) {
            if (octs == 11) { // draw additional IMPERIAL keys grey.
                int ww1 = (int) Math.round((octs + .5) * wFact + wShift);
                int ww2 = (int) Math.round((octs + 9) * wFact + wShift);
                g.setColor(additionalKeysColor);
                if (isHor) {
                    g.fillRect(ww1, d1, ww2 - ww1, d2 - d1);
                } else {
                    g.fillRect(d1, ww1, d2 - d1, ww2 - ww1);
                }
                g.setColor(color);
            }
            if (octs + 1 == 60) {
                int ww1 = (int) Math.round((octs + .5) * wFact + wShift);
                int ww2 = (int) Math.round((octs + 2.111) * wFact + wShift);
                if (ww2 - ww1 < 0) {
                    int tmp = ww1;
                    ww1 = ww2;
                    ww2 = tmp;
                }
                g.setColor(middleCColor);
                if (isHor) {
                    g.fillRect(ww1, d1, ww2 - ww1, d2 - d1);
                } else {
                    g.fillRect(d1, ww1, d2 - d1, ww2 - ww1);
                }
                g.setColor(color);
            }
            for (int i = 0; i <= 4; i++) { // draw black keys
                int w = (int) Math.round((octs + bKeys[i] + .45) * wFact + wShift);
                int keyWidth = (int) Math.round(Math.abs(.9 * wFact));
                int keyLength = (int) Math.round(.65 * (d2 - d1));
                if ((w + keyWidth) >= w1 && (w - keyWidth) <= w2) {
                    if (blackKeysTopLeft) {
                        if (isHor) {
                            g.fillRect(w, d1, keyWidth, keyLength);
                        } else {
                            g.fillRect(d1, w, keyLength, keyWidth);
                        }
                    } else {
                        if (isHor) {
                            g.fillRect(w, d2 - keyLength, keyWidth, keyLength);
                        } else {
                            g.fillRect(d2 - keyLength, w, keyLength, keyWidth);
                        }
                    }
                }
            }
            for (double i = octs + .5; i < octs + 5.5; i += 1.6667) {// white1
                int w = (int) Math.round(i * wFact + wShift);
                if (w > w1 && w < w2) {
                    if (isHor) {
                        g.drawLine(w, d1, w, d2);
                    } else {
                        g.drawLine(d1, w, d2 - 1, w);
                    }
                }
            }
            for (double i = octs + 5.5; i <= octs + 12.5; i += 1.75) {// white2
                int w = (int) Math.round(i * wFact + wShift);
                if (w > w1 && w < w2) {
                    if (isHor) {
                        g.drawLine(w, d1, w, d2);
                    } else {
                        g.drawLine(d1, w, d2 - 1, w);
                    }
                }
            }
        }
        g.setColor(frameColor);
        if (isHor) {
            g.drawRect(w1, d1, w2 - w1 - 1, d2 - d1);
        } else {
            g.drawRect(d1, w1, d2 - d1 - 1, w2 - w1);
        }
    } // paint()

    private void error(String str) {
        System.out.println(str);
        System.exit(0);
    } // error

    private boolean isXLg() {
        return ax.getXScale().equals("log");
    }

    private boolean isYLg() {
        return ax.getYScale().equals("log");
    }

    /**
     * @return Returns the depth (length of the keys).
     */
    public double getDepth() {
        return depth;
    }

    /**
     * @param depth The depth to set (length of the keys).
     */
    public void setDepth(double depth) {
        this.depth = depth;
    }

    /**
     * @return Returns the frameColor.
     */
    public Color getFrameColor() {
        return frameColor;
    }

    /**
     * @param frameColor The frameColor to set.
     */
    public void setFrameColor(Color frameColor) {
        this.frameColor = frameColor;
    }

    /**
     * Shows octave numbers (Cs) as pitch labels.
     */
    public void setShowOctaveNoteNumbers() {
        if (position.equalsIgnoreCase("left")
                || position.equalsIgnoreCase("right")) {
            ax.setYTick(midiNoteNumbers);
        } else if (position.equalsIgnoreCase("top")
                || position.equalsIgnoreCase("bottom")) {
            ax.setXTick(midiNoteNumbers);
        }
    } // setShowOctaveNoteNumbers

    /**
     * Shows German note labels (c_sk ... c5).
     */
    public void setShowGerNoteLabels() {
        if (position.equalsIgnoreCase("left")
                || position.equalsIgnoreCase("right")) {
            ax.setYTick(midiNoteNumbers);
            ax.setYTickLabel(germanNoteLabels);
        } else if (position.equalsIgnoreCase("top")
                || position.equalsIgnoreCase("bottom")) {
            ax.setXTick(midiNoteNumbers);
            ax.setXTickLabel(germanNoteLabels);
        }
    } // setShowGerNoteLabels

    /**
     * Shows English note labels (C0 ... C8).
     */
    public void setShowEngNoteLabels() {
        if (position.equalsIgnoreCase("left")
                || position.equalsIgnoreCase("right")) {
            ax.setYTick(midiNoteNumbers);
            ax.setYTickLabel(englishNoteLabels);
        } else if (position.equalsIgnoreCase("top")
                || position.equalsIgnoreCase("bottom")) {
            ax.setXTick(midiNoteNumbers);
            ax.setXTickLabel(englishNoteLabels);
        }
    } // setShowEngNoteLables
} // class Keyboard