package at.ofai.music.plot;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

/**
 * Creates a figure axes object including axes labels, titles, ticks and scaling
 * functionalities.
 * <p>
 * <b>Interactive zoom functions</b><br> Press "Z" to change into the zoom mode
 * (indicated by a zoom cursor) and use the mouse for zooming (left button for
 * zooming in; right mouse button for resetting to original view).
 * <p>
 * Press "H" to change into the pan mode and use the mouse (now with a hand
 * cursor) to click and drag the display as you like.
 * <p>
 * Independently of the "Z" mode, you can also use the four arrow keys to shift
 * the axes left/right and up/down or to zoom in and out the individual axes (
 * <code>arrow keys + CTRL</code>); <code>CTRL + "+"</code> and
 * <code>CTRL + "-"</code> will zoom in and out both axes simultaneously; use
 * <code>"0"</code> or <code>CTRL + "0"</code> to set the axes limits back to
 * the original.
 * <p>
 *
 * @author Werner Goebl, Aug. 2005; 26 Aug 2007
 * @see Figure
 * @see PlottableObject
 */
@SuppressWarnings("serial")
public class Axes extends JPanel {

    protected double minX, maxX, minY, maxY; // boundaries of all plottable objects
    private double oldminX, oldmaxX, oldminY, oldmaxY;
    private Toolkit tk;
    protected boolean printScreen = false; // for printing the screen TODO
    // TODO introduce a paint method in Figure to print all subplots
    private double xLim0, xLim1, yLim0, yLim1; // boundaries of axes
    public MyConcurrentQueue<PlottableObject> plottableObjects;
    public MyConcurrentQueue<PlottableObject> labelObjects;
    protected boolean antialiasing = false;
    private double scaleRef = 1.0;
    protected boolean v = false; // verbose output (DEBUG)
    public final static int DEFAULT = 0;
    public final static int ZOOM = 1;
    public final static int PAN = 2;
    private static int guiMode = DEFAULT;
    private boolean xZoom = false, yZoom = false;// xLim0... are set externally
    private String epsName = "";
    private Rectangle2D selectionRect = null;
    private final float defaultAxesLineWidth = 1f;
    private float axesLineWidth = defaultAxesLineWidth;
    protected Shape figClip, axClip;
    protected boolean doStroke = false;
    private Title title = null;
    private Label xLabel = null;
    private Label yLabel = null;
    private double xFact, yFact, xShift, yShift;
    private boolean scaleAxes = true;
    private BufferedImage axImage; // temporary image object to draw screen
    private boolean forceRedraw = true; // if true, always redraw entire axes and its children
    private boolean forceRedrawNext = false; // only redraws everything next time once.
    private int width, height;
    private double x1Margin, x2Margin, y1Margin, y2Margin;
    protected int X1, X2, Y1, Y2; // edge points of plotting area
    private final int maxTicks = 11; // max number of ticks per axis
    private boolean xLog, yLog;
    private Figure fig = null;
    private JFrame figFrame = null;
    private boolean hold = false; // hold on/off
    private boolean box = true; // box on/off
    private String fontName = "Helvethica";
    private float fontStyle = TextAttribute.WEIGHT_REGULAR;
    private float fontSize = 12.0f;
    private double[] position = {.13, .11, .775, .815};
    private Color color = Color.BLACK; // the axes line colors
    private Color backgroundColor = Color.WHITE; // the at.ofai.music.plot
    private Color frameColor = Figure.getDefaultBackgroundColor();
    // area background
    private Color gridColor = Color.LIGHT_GRAY;
    private String xAxisLocation = "bottom"; // vs "top"
    private Color xColor = color; // color of xAxis + Label
    private String xDir = "normal"; // reverse
    private boolean xGrid = false; // plots xGrid (or not)
    private boolean xMinorGrid = false; // plots a minor xGrid (or not)
    private boolean xMinorTick = false; //
    private String xScale = "linear"; // vs "log"
    private double[] xLim = new double[2];
    private String xLimMode = "auto";
    private double[] xTick;
    private String xTickMode = "auto"; // auto vs manual
    private String[] xTickLabel;
    private String xTickLabelMode = "auto"; // auto vs manual
    private String yAxisLocation = "left"; // vs "right"
    private Color yColor = color;
    private String yDir = "normal"; // reverse
    private boolean yGrid = false;
    private boolean yMinorGrid = false; // plots a minor xGrid (or not)
    private boolean yMinorTick = false; //
    private String yScale = "linear"; // vs "log"
    private double[] yLim = new double[2];
    private String yLimMode = "auto";
    private double[] yTick;
    private String yTickMode = "auto"; // auto vs manual
    private String[] yTickLabel;
    private String yTickLabelMode = "auto"; // auto vs manual
    private final int minTickLabelGapPx = 8; // minimum gap of tick labels to axes, both x and y (in pixels)
    private float scaleFactor = 1f; // either 1 for normal screens, or 2 for retina screens.

    public Axes() {
        this(null);
    } // constructor

    /**
     * Constructs axes within a Figure object.
     *
     * @param f
     */
    public Axes(Figure f) { // constructor
        fig = f;
        init();
    } // constructor

    /**
     * Constructs an axes without a Figure object.
     *
     * @param f
     */
    public Axes(JFrame f) {
        figFrame = f;
        init();
    }

    private void init() {
        minX = Double.MAX_VALUE;
        maxX = Double.MIN_VALUE;
        minY = Double.MAX_VALUE;
        maxY = Double.MIN_VALUE;
        oldminX = minX;
        oldmaxX = maxX;
        oldminY = minY;
        oldmaxY = maxY;
        xLim0 = 0.0;
        xLim1 = 1.0;
        yLim0 = 0.0;
        yLim1 = 1.0;
        xTick = new double[maxTicks];
        yTick = new double[maxTicks];
        xTickLabel = new String[maxTicks];
        yTickLabel = new String[maxTicks];
        Arrays.fill(xTick, Double.NaN);
        Arrays.fill(yTick, Double.NaN);
        Arrays.fill(xTickLabel, null);
        Arrays.fill(yTickLabel, null);
        plottableObjects = new MyConcurrentQueue<>();
        labelObjects = new MyConcurrentQueue<>();
        addKeyListener(new AxesKeyListener(this));
        // setFocusable(true);
        AxesMouseListener axMouseListener = new AxesMouseListener(this);
        addMouseListener(axMouseListener);
        addMouseMotionListener(axMouseListener);
        tk = getToolkit();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (forceRedraw || axImage == null || forceRedrawNext) { // re-render everything into axImage
            if (width <= 0 || height <= 0) {
                rescale();
            }
            axImage = getGraphicsConfiguration().createCompatibleImage(width, height);
            Graphics2D gax = (Graphics2D) axImage.getGraphics();
            if (antialiasing) {
                gax.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
            render(gax);
            forceRedrawNext = false;
        }
        // only redraw axImage
        if (scaleFactor != 1) {
            g2.scale(1 / scaleFactor, 1 / scaleFactor);
        }
        g2.drawImage(axImage, 0, 0, null);
    }

    /**
     * Re-render entire axes object with all children into
     *
     * @param g2 Graphics2D
     */
    public void render(Graphics2D g2) {
        // long ct = System.currentTimeMillis();
        rescale();
        if (printScreen) {
            if ("".equals(epsName) && fig != null) {
                epsName = fig.getTitle() + ".eps";
            }
            if ("".equals(epsName) && figFrame != null) {
                epsName = figFrame.getTitle() + ".eps";
            }
            File outputFile = new File(epsName);
            out("epsFile: " + outputFile.getAbsolutePath() + " ...");
            if (fig != null) {
                fig.setColor(Color.WHITE);
            }
            if (figFrame != null) {
                figFrame.setBackground(Color.WHITE);
            }
            setAxesLineWidth(.5f);
            try {
                g2 = new EpsGraphics2D(epsName, outputFile, 0, 0, width, height);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (fig != null) {
                fig.setColor(fig.getDefaultColor());
            }
            if (figFrame != null) {
                figFrame.setBackground(Color.WHITE);
            }
            setAxesLineWidth(getDefaultAxesLineWidth());
        }
        if (antialiasing) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        if (v) {
            outn("\n\nPAINT() xLim0/1: " + xLim0 + "/" + xLim1);
        }
        if (v) {
            outn("PAINT() yLim0/1: " + yLim1 + "/" + yLim1);
        }

        drawAxes(g2);
        drawAxesLabels(g2);
        for (PlottableObject o : labelObjects) { // plot axes labels
            o.render(g2);
        }
        figClip = g2.getClip();
        g2.setClip(axClip);
        // synchronized(plottableObjects) {
        for (PlottableObject o : plottableObjects) {
            if (o instanceof Text) {
                g2.setClip(figClip);
                o.render(g2);
                g2.setClip(axClip);
            } else {
                o.render(g2);
            }
        }
        // }
        g2.setClip(figClip);
        if (printScreen) {
            try {
                ((EpsGraphics2D) g2).flush();
                ((EpsGraphics2D) g2).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            printScreen = false;
            outn(" written.");
            return;
        }
        if (selectionRect != null) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(PlottableObject.solid(.5f));
            g2.draw(selectionRect);
        }
        // outn("AXES paintComponent ("+fig.getTitle()+"): "+
        // (System.currentTimeMillis()-ct)+" ms.");
    } // paintComponent()

    private void rescale() {
        if (guiMode == ZOOM) {
            Image zoomIcon = null;
            Dimension d = tk.getBestCursorSize(16, 16);
            if (d.equals(new Dimension(32, 32))) {
                zoomIcon = tk
                        .getImage(URLClassLoader
                                .getSystemResource("at/ofai/music/plot/zoomInCursor32.gif"));
            } else if (d.equals(new Dimension(16, 16))) {
                zoomIcon = tk
                        .getImage(URLClassLoader
                                .getSystemResource("at/ofai/music/plot/zoomInCursor16.gif"));
            }
            Cursor zoomCursor = tk.createCustomCursor(zoomIcon, new Point(6, 6),
                    "Zoom Cursor");
            getParent().setCursor(zoomCursor);
        } else if (guiMode == PAN) {
            Image panIcon = null;
            Dimension d = tk.getBestCursorSize(16, 16);
            if (d.equals(new Dimension(32, 32))) {
                panIcon = tk.getImage(URLClassLoader
                        .getSystemResource("at/ofai/music/plot/pan32.gif"));
            } else if (d.equals(new Dimension(16, 16))) {
                panIcon = tk.getImage(URLClassLoader
                        .getSystemResource("at/ofai/music/plot/pan16.gif"));
            }
            Cursor zoomCursor = tk.createCustomCursor(panIcon, new Point(8, 8),
                    "Pan Cursor");
            getParent().setCursor(zoomCursor);
        } else {
            getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        width = getWidth() * (int) scaleFactor;
        height = getHeight() * (int) scaleFactor;
        switch (xAxisLocation) {
            case "bottom":
                // swap y Margins
                y1Margin = height * position[1];
                y2Margin = height * (1 - position[1] - position[3]);
                break;
            case "top":
                y2Margin = height * position[1];
                y1Margin = height * (1 - position[1] - position[3]);
                break;
        }
        switch (yAxisLocation) {
            case "left":
                // swap x Margins
                x1Margin = width * position[0];
                x2Margin = width * (1 - position[0] - position[2]);
                break;
            case "right":
                x2Margin = width * position[0];
                x1Margin = width * (1 - position[0] - position[2]);
                break;
        }
        X1 = (int) Math.round(x1Margin);
        X2 = (int) Math.round(width - x2Margin);
        Y1 = (int) Math.round(y2Margin);
        Y2 = (int) Math.round(height - y1Margin);

        axClip = new Rectangle2D.Double(X1, Y1, X2 - X1, Y2 - Y1);
        if ((oldminX != minX || oldmaxX != maxX) || (Double.isNaN(xTick[0]))) {
            // Check & calculate X LIMS
            if (v) {
                outn("XXX rescale(X): ");
            }
            double stSz;
            if (minX == maxX) {
                if (xLog) {
                    minX /= 5;
                    maxX *= 5;
                } else {
                    minX -= .5;
                    maxX += .5;
                }
            }
            if (!xLog) {
                stSz = calcStepSize(minX, maxX, xLog);
            } else {
                stSz = calcStepSize(Math.log10(minX), Math.log10(maxX), xLog);
            }
            int lg = (int) Math.floor(Math.log10(stSz));
            if (v) {
                outn(" lg=" + lg + ", stSz=" + stSz + ", isxLog=" + xLog);
            }
            if (xZoom) {
                stSz = calcStepSize(xLim0, xLim1, xLog);
                lg = (int) Math.floor(Math.log10(stSz));
            } else {
                switch (xLimMode) {
                    case "manual":
                        if (xLog) {
                            xLim0 = Math.log10(xLim[0]);
                            xLim1 = Math.log10(xLim[1]);
                        } else { // linear
                            xLim0 = xLim[0];
                            xLim1 = xLim[1];
                        }
                        stSz = calcStepSize(xLim0, xLim1, xLog);
                        lg = (int) Math.floor(Math.log10(stSz));
                        if (v) {
                            outn("rescale(X): xLims set manually to: " + xLim0
                                    + "/" + xLim1);
                        }
                        break;
                    case "auto":
                        if (xLog) {
                            xLim0 = Math.floor(Math.log10(minX) / stSz) * stSz;
                            xLim1 = Math.ceil(Math.log10(maxX) / stSz) * stSz;
                        } else { // linear
                            xLim0 = Math.floor(minX / stSz) * stSz;
                            xLim1 = Math.ceil(maxX / stSz) * stSz;
                        }
                        if (v) {
                            outn("rescale(X): xLims set automatically to: " + xLim0
                                    + "/" + xLim1);
                        }
                        break;
                    default:
                        err("at.ofai.music.Axes unknown xLimMode=" + xLimMode);
                        break;
                }
            }
            // Check & calculate X TICKS
            if (xTickMode.equals("auto")) {
                if (v) {
                    outn("calcTicks(X): xLim0:" + xLim0 + ", xLim1:" + xLim1
                            + ", stSz=" + stSz);
                }
                xTick = calcTicks(xLim0, xLim1, stSz, lg);
            }

            // Check & calculate X TICKLABELS
            if (xTickLabelMode.equals("auto")) {
                if (v) {
                    outn("calcAxisTickLabels(X): ");
                }
                xTickLabel = createAxisLabels(xTick, lg, xLog);
            }
        } else {
            if (v) {
                out("rescale(X): LIMS etc NOT RECALCULATED: ");
                outn("minX=" + minX + ", oldminX=" + oldminX + ", maxX=" + maxX
                        + ", oldmaxX=" + oldmaxX);
            }
        }
        oldminX = minX;
        oldmaxX = maxX;

        if ((oldminY != minY || oldmaxY != maxY) || (Double.isNaN(yTick[0]))) {
            // Check & calculate Y LIMS
            if (v) {
                outn("YYY rescale(Y): ");
            }
            if (minY == maxY) {
                if (yLog) {
                    minY /= 5;
                    maxY *= 5;
                } else {
                    minY -= .5;
                    maxY += .5;
                }
            }
            double stSz;
            if (!yLog) {
                stSz = calcStepSize(minY, maxY, yLog);
            } else {
                stSz = calcStepSize(Math.log10(minY), Math.log10(maxY), yLog);
            }
            int lg = (int) Math.floor(Math.log10(stSz));
            if (v) {
                outn("lg=" + lg + ", stSz=" + stSz);
            }
            if (yZoom) {
                stSz = calcStepSize(yLim0, yLim1, yLog);
                lg = (int) Math.floor(Math.log10(stSz));
            } else {
                switch (yLimMode) {
                    case "manual":
                        if (yLog) {
                            yLim0 = Math.log10(yLim[0]);
                            yLim1 = Math.log10(yLim[1]);
                        } else {
                            yLim0 = yLim[0];
                            yLim1 = yLim[1];
                        }
                        stSz = calcStepSize(yLim0, yLim1, yLog);
                        lg = (int) Math.floor(Math.log10(stSz));
                        if (v) {
                            outn("rescale(Y): yLims set manually to: " + yLim0
                                    + "/" + yLim1);
                        }
                        break;
                    case "auto":
                        if (yLog) {
                            if (minY == maxY) {
                                minY /= 10;
                                maxY *= 10;
                            }
                            yLim0 = Math.floor(Math.log10(minY) / stSz) * stSz;
                            yLim1 = Math.ceil(Math.log10(maxY) / stSz) * stSz;
                        } else {
                            if (minY == maxY) {
                                minY--;
                                maxY++;
                            }
                            yLim0 = Math.floor(minY / stSz) * stSz;
                            yLim1 = Math.ceil(maxY / stSz) * stSz;
                        }
                        if (v) {
                            outn("rescale(Y): yLims set automatically to: " + yLim0 + "/" + yLim1);
                        }
                        break;
                    default:
                        err("at.ofai.music.Axes unknown yLimMode=" + yLimMode);
                        break;
                }
            }
            // Check & calculate Y TICKS
            if (yTickMode.equals("auto")) {
                if (v) {
                    outn("calcTicks(Y): yLim0:" + yLim0 + ", yLim1:" + yLim1
                            + ", stSz=" + stSz);
                }
                yTick = calcTicks(yLim0, yLim1, stSz, lg);
            }
            // Check & calculate Y TICKLABELS
            if (yTickLabelMode.equals("auto")) {
                if (v) {
                    outn("calcAxisTickLabels(Y): calc yTL: ");
                }
                yTickLabel = createAxisLabels(yTick, lg, yLog);
            }
        } else {
            if (v) {
                out("rescale(Y): LIMS etc NOT RECALCULATED.");
                outn("minY=" + minY + ", oldminY=" + oldminY + ", maxY=" + maxY
                        + ", oldmaxY=" + oldmaxY);
            }
        }
        oldminY = minY;
        oldmaxY = maxY;
        // scaling factors between user space and screen space
        switch (xDir) {
            case "normal":
                xFact = ((double) (width) - x1Margin - x2Margin) / (xLim1 - xLim0);
                xShift = x1Margin - xLim0 * xFact;
                break;
            case "reverse":
                xFact = -((double) (width) - x1Margin - x2Margin) / (xLim1 - xLim0);
                xShift = width - x2Margin - xLim0 * xFact;
                break;
            default:
                System.err.println("xDir can only be normal or reverse");
                break;
        }
        switch (yDir) {
            case "normal":
                yFact = -((double) (height) - y1Margin - y2Margin) / (yLim1 - yLim0);
                yShift = height - y1Margin - yLim0 * yFact;
                break;
            case "reverse":
                yFact = ((double) (height) - y1Margin - y2Margin) / (yLim1 - yLim0);
                yShift = y2Margin - yLim0 * yFact;
                break;
            default:
                System.err.println("yDir can only be normal or reverse");
                break;
        }

        scaleRef = (width + height) / 944.0;

        if (color != Color.BLACK) {
            xColor = color;
            yColor = color;
        }
        switch (xScale) {
            case "linear":
                xLog = false;
                break;
            case "log":
                xLog = true;
                break;
            default:
                Plot.warn("at.ofai.music.plot.Axes: "
                        + "xScale either 'linear' or 'log'");
                break;
        }
        switch (yScale) {
            case "linear":
                yLog = false;
                break;
            case "log":
                yLog = true;
                break;
            default:
                Plot.warn("at.ofai.music.plot.Axes: "
                        + "yScale either 'linear' or 'log'");
                break;
        }
        if (v) {
            outn("xScale:" + xScale + ", yScale:" + yScale);
            outn("xLog:" + xLog + ", yLog:" + yLog);
        }
    } // rescale

    /**
     * Internal method to draw the box of the axes.
     *
     * @param g
     */
    private void drawAxes(Graphics2D g) {
        // g.clearRect(0,0,width,height);
        if (doStroke) {
            g.setStroke(PlottableObject.solid(axesLineWidth * scaleFactor));
        }
        g.setColor(frameColor); // Figure background frame around the actual graph area
        g.fillRect(0, 0, width, height);
        g.setColor(backgroundColor); // Plotting area background
        g.fillRect(X1, Y1, X2 - X1, Y2 - Y1);
        g.setColor(color); // set color for plotting Labels etc
    } // drawAxes

    /**
     * Internal method to draw the axes ticks and tick labels.
     *
     * @param g2
     */
    private void drawAxesLabels(Graphics2D g2) {
        FontRenderContext frc = g2.getFontRenderContext();

        // ############################# x labels #############################
        if (xMinorTick || xMinorGrid) {
            LinkedList<Double> minorTick = calcMinorTicks(true);
            // outn("xMinorTickLength="+minorTick.size());
            double xMH = .007 * (height - y1Margin - y2Margin);
            for (Double t : minorTick) {
                if (xLog) {
                    t = Math.log10(t);
                }
                if (t < xLim0 || t > xLim1) {
                    continue;
                }
                int x = (int) Math.round(t * xFact + xShift);
                if (xMinorGrid) { // draw xGrid
                    g2.setStroke(PlottableObject.dotted(axesLineWidth * scaleFactor));
                    g2.setColor(gridColor);
                    g2.drawLine(x, Y1, x, Y2);
                    g2.setColor(xColor);
                    g2.setStroke(PlottableObject.solid(axesLineWidth * scaleFactor));
                }
                switch (xAxisLocation) {
                    case "bottom":
                        // xMinorTicks
                        g2.drawLine(x, Y2, x, (int) Math.round(height - y1Margin
                                - xMH));
                        if (box) {
                            g2.drawLine(x, Y1, x, (int) Math.round(y2Margin + xMH));
                        }
                        break;
                    case "top":
                        // xMinorTicks
                        g2.drawLine(x, Y1, x, (int) Math.round(y2Margin + xMH));
                        if (box) {
                            g2.drawLine(x, Y2, x, (int) Math.round(height
                                    - y1Margin - xMH));
                        }
                        break;
                    default:
                        Plot.warn("Axes: xAxisLocation 'bottom' or 'top'");
                        break;
                }
            }
        } // if minorTick

        g2.setColor(xColor);
        double xH = Math.max(minTickLabelGapPx, .014 * (height - y1Margin - y2Margin));
        for (int i = 0; i < xTickLabel.length; i++) {
            if (xTickLabel[i] == null) {
                break;
            }
            if (xTickLabel[i].equals("")) {
                break;
            }
            double j = xTick[i];
            if (xTickMode.equals("manual") && xLog) {
                j = Math.log10(j);
            }
            if (j < xLim0 || j > xLim1) {
                continue;
            }
            int x = (int) Math.round(j * xFact + xShift);
            if (xGrid) { // draw xGrid
                g2.setStroke(PlottableObject.dotted(axesLineWidth * scaleFactor));
                g2.setColor(gridColor);
                g2.drawLine(x, Y1, x, Y2);
                g2.setColor(xColor);
                g2.setStroke(PlottableObject.solid(axesLineWidth * scaleFactor));
            }
            // xTickLabels
            AttributedString attStr = Text.stringToAttString(xTickLabel[i]);
            if (attStr == null) {
                break;
            }
            attStr.addAttribute(TextAttribute.FAMILY, fontName);
            if (scaleRef > 0 & scaleAxes) {
                attStr.addAttribute(TextAttribute.SIZE, fontSize * (float) scaleRef);
            } else {
                attStr.addAttribute(TextAttribute.SIZE, fontSize);
            }
            attStr.addAttribute(TextAttribute.WEIGHT, fontStyle);
            TextLayout tl = new TextLayout(attStr.getIterator(), frc);
            Rectangle2D r = tl.getBounds();

            // draw Labels and Ticks
            float textX = (float) (x - r.getWidth() / 2);
            float textY = 0f;
            switch (xAxisLocation) {
                case "bottom":
                    // xTicks
                    g2.drawLine(x, Y2, x, (int) Math.round(height - y1Margin - xH));
                    if (box) {
                        g2.drawLine(x, Y1, x, (int) Math.round(y2Margin + xH));
                    }
                    textY = (float) (height - y1Margin + xH + r.getHeight());
                    break;
                case "top":
                    // xTicks
                    g2.drawLine(x, Y1, x, (int) Math.round(y2Margin + xH));
                    if (box) {
                        g2.drawLine(x, Y2, x, (int) Math.round(height - y1Margin - xH));
                    }
                    textY = (float) (y2Margin - xH);
                    break;
                default:
                    Plot.warn("Axes: xAxisLocation 'bottom' or 'top'");
                    break;
            }
            tl.draw(g2, textX, textY);
        } // for

        // ############################# y labels #############################
        if (yMinorTick || yMinorGrid) {
            LinkedList<Double> minorTick = calcMinorTicks(false);
            double yMH = .005 * (width - x1Margin - x2Margin);
            for (Double t : minorTick) {
                // outn("minor tick = " + t);
                if (yLog) {
                    t = Math.log10(t);
                }
                if (t < yLim0 || t > yLim1) {
                    continue;
                }
                int y = (int) Math.round(t * yFact + yShift);
                if (yMinorGrid) { // y MinorGrids
                    g2.setStroke(PlottableObject.dotted(axesLineWidth * scaleFactor));
                    g2.setColor(gridColor);
                    g2.drawLine(X1, y, X2, y);
                    g2.setColor(yColor);
                    g2.setStroke(PlottableObject.solid(axesLineWidth * scaleFactor));
                }
                switch (yAxisLocation) {
                    case "left":
                        // yTicks left
                        g2.drawLine(X1, y, (int) Math.round(x1Margin + yMH), y);
                        if (box) {
                            g2.drawLine(X2, y, (int) Math.round(width - x2Margin
                                    - yMH), y);
                        }
                        break;
                    case "right":
                        // yTicks right
                        g2.drawLine(X2, y,
                                (int) Math.round(width - x2Margin - yMH), y);
                        if (box) {
                            g2.drawLine(X1, y, (int) Math.round(x1Margin + yMH), y);
                        }
                        break;
                    default:
                        Plot.warn("Axes: yAxisLocation 'left' or 'right'");
                        break;
                }
            }
        } // if minorTick

        g2.setColor(yColor);
        double yH = Math.max(minTickLabelGapPx, .01 * (width - x1Margin - x2Margin));
        for (int i = 0; i < yTickLabel.length; i++) {
            if (yTickLabel[i] == null) {
                break;
            }
            if (yTickLabel[i].equals("")) {
                break;
            }
            double j = yTick[i];
            if (yTickMode.equals("manual") && yLog) {
                j = Math.log10(j);
            }
            if (j < yLim0 || j > yLim1) {
                continue;
            }
            int y = (int) Math.round(j * yFact + yShift);
            if (yGrid) { // y Grids
                g2.setStroke(PlottableObject.dotted(axesLineWidth * scaleFactor));
                g2.setColor(gridColor);
                g2.drawLine(X1, y, X2, y);
                g2.setColor(yColor);
                g2.setStroke(PlottableObject.solid(axesLineWidth * scaleFactor));
            }

            // yAxisLabels
            AttributedString attStr = Text.stringToAttString(yTickLabel[i]);
            if (attStr == null) {
                break;
            }
            attStr.addAttribute(TextAttribute.FAMILY, fontName);
            if (scaleRef > 0 & scaleAxes) {
                attStr.addAttribute(TextAttribute.SIZE, fontSize
                        * (float) scaleRef);
            } else {
                attStr.addAttribute(TextAttribute.SIZE, fontSize);
            }
            attStr.addAttribute(TextAttribute.WEIGHT, fontStyle);
            TextLayout tl = new TextLayout(attStr.getIterator(), frc);
            Rectangle2D r = tl.getBounds();
            // draw Labels and Ticks
            float textY = (float) (y + r.getHeight() / 2);
            float textX = 0f;
            switch (yAxisLocation) {
                case "left":
                    // yTicks left
                    g2.drawLine(X1, y, (int) Math.round(x1Margin + yH), y);
                    if (box) {
                        g2.drawLine(X2, y, (int) Math.round(width - x2Margin - yH), y);
                    }
                    textX = (float) (x1Margin - yH - r.getWidth());
                    break;
                case "right":
                    // yTicks right
                    g2.drawLine(X2, y, (int) Math.round(width - x2Margin - yH), y);
                    if (box) {
                        g2.drawLine(X1, y, (int) Math.round(x1Margin + yH), y);
                    }
                    textX = (float) (width - x2Margin + yH);
                    break;
                default:
                    Plot.warn("Axes: yAxisLocation 'left' or 'right'");
                    break;
            }
            tl.draw(g2, textX, textY);
        } // for
        // draw axes lines
        switch (xAxisLocation) {
            case "bottom":
                g2.setColor(xColor);
                g2.drawLine(X1, Y2, X2, Y2);
                if (box) {// draw the opposite axes lines
                    g2.setColor(xColor);
                    g2.drawLine(X1, Y1, X2, Y1);
                }
                break;
            case "top":
                g2.setColor(xColor);
                g2.drawLine(X1, Y1, X2, Y1);
                if (box) {// draw the opposite axes lines
                    g2.setColor(xColor);
                    g2.drawLine(X1, Y2, X2, Y2);
                }
                break;
        }
        switch (yAxisLocation) {
            case "left":
                g2.setColor(yColor);
                g2.drawLine(X1, Y2, X1, Y1);
                if (box) {// draw the opposite axes lines
                    g2.setColor(yColor);
                    g2.drawLine(X2, Y2, X2, Y1);
                }
                break;
            case "right":
                g2.setColor(yColor);
                g2.drawLine(X2, Y2, X2, Y1);
                if (box) {// draw the opposite axes lines
                    g2.setColor(yColor);
                    g2.drawLine(X1, Y2, X1, Y1);
                }
                break;
        }
    } // drawAxesLabels

    private LinkedList<Double> calcMinorTicks(boolean isX) {
        LinkedList<Double> ticks = new LinkedList<>();
        double stSz;
        boolean isLog;
        double lim0, lim1;
        if (isX) {
            isLog = xLog;
            stSz = xTick[1] - xTick[0];
            lim0 = xLim0;
            lim1 = xLim1;
        } else {
            isLog = yLog;
            stSz = yTick[1] - yTick[0];
            lim0 = yLim0;
            lim1 = yLim1;
        }
        int lg = (int) Math.floor(Math.log10(stSz));
        double mn, mx;
        if (!isLog) { // linear (works already)
            if (stSz / Math.pow(10, lg) == 1) {
                stSz = .5 * Math.pow(10, lg);
            } else {
                stSz = Math.pow(10, lg);
            }
            mn = (int) (lim0 / stSz) * stSz;
            mx = (int) (lim1 / stSz) * stSz;
            for (double tick = mn; tick <= mx; tick = tick + stSz) {
                ticks.add(tick);
            }
        } else { // log minor ticks
            if (stSz == 1) {
                int tmplg = (int) Math.floor(lim0);
                mn = Math.ceil(Math.pow(10, lim0) / Math.pow(10, tmplg))
                        * Math.pow(10, tmplg);
                int i = tmplg;
                for (double tick = mn; tick < Math.pow(10, lim1); tick += Math
                        .pow(10, i)) {
                    ticks.add(tick);
                    if (tick >= (Math.pow(10, i + stSz) - Math.pow(10, i - 8))) {
                        i++;
                    }
                }
                /*
                 * for (int i = tmplg; i < lim1; i += stSz) { while (tick <
                 * (Math.pow(10, i+stSz)-Math.pow(10,i-8)) && tick <=
                 * Math.pow(10,lim1) - Math.pow(10,i)) { tick += Math.pow(10,i);
                 * ticks.add(tick); } }
                 */
            } // TODO log minor ticks with large values...
        } // if yLog
        return ticks;
    } // calcMinorTicks

    // takes mins and maxs
    private double[] calcTicks(double mn, double mx, double stepSize, int lg) {
        double[] axisTicks = new double[maxTicks];
        Arrays.fill(axisTicks, Double.NaN);
        if (v) {
            out("calcTicks() log=" + lg + ", mn/mx:" + mn + "/" + mx
                    + ", Stepsize:" + stepSize + "; Ticks: ");
        }
        int ind = 0;
        // TODO isn't it ceil istead of int??? (Yes, that's true)
        for (double tick = Math.ceil(mn / stepSize) * stepSize; // trunkate
                // number
                tick <= mx + Math.pow(10, lg - 10); tick = tick + stepSize) {
            if (ind > maxTicks - 1) {
                break;
            }
            axisTicks[ind++] = tick;
            if (v) {
                out(tick + ", ");
            }
        }
        if (v) {
            outn(".");
        }
        return axisTicks;
    } // calcLabels(double, double)

    private double calcStepSize(double min, double max, boolean isLog) {
        double stepSize;
        if (!isLog) {
            stepSize = (max - min) / (maxTicks - 1);
        } else {
            stepSize = (max - min) / (maxTicks * 2 / 3);
        }
        if (v) {
            out("calcStepSize() " + ", mn/mx:" + min + "/" + max + "sZ:" + stepSize);
        }
        int lg = (int) Math.floor(Math.log10(stepSize));
        stepSize /= Math.pow(10, lg);
        if (v) {
            out(", Range:" + (max - min) + ", lg:" + lg + ", sS:" + stepSize);
        }
        if (stepSize < 1) {
            stepSize = 1;
        } else if (stepSize >= 1 && stepSize < 2) {
            stepSize = 2.0;
        } else if (stepSize >= 2 && stepSize < 5) {
            stepSize = 5.0;
        } else if (stepSize >= 5) {
            stepSize = 1.0;
            lg++;
        }
        stepSize *= Math.pow(10, lg);
        if (isLog) {
            stepSize = Math.ceil(stepSize);
        }
        if (v) {
            outn(", sS:" + stepSize);
        }
        return stepSize;
    } // calcStepSize

    private String[] createAxisLabels(double[] tick, int lgVal, boolean isLog) {
        String formString;
        String st;
        String[] xTL = new String[maxTicks];
        Arrays.fill(xTL, null);
        if (v) {
            out("cAL(): TickLabels(isLog:" + isLog + ", lgVal:" + lgVal + ") ");
        }
        for (int i = 0; i < xTL.length; i++) {
            if (i >= tick.length) {
                break;
            }
            if (Double.isNaN(tick[i])) {
                break;
            }
            if (!isLog) { // linear
                if (lgVal > 0) {
                    formString = "%1.0f";
                } else {
                    formString = "%1." + Math.abs(lgVal) + "f";
                }
                st = String.format(formString, tick[i]);
                if (tick[i] == 0) {
                    st = "0";
                }
            } else { // logarithmic scaling
                st = String.format("10^{%1.0f}", tick[i]);
            }
            if (v) {
                out(st + ", ");
            }
            xTL[i] = st;
        }
        if (v) {
            outn(".");
        }
        return xTL;
    } // createAxisLabels

    public void holdOn() {
        hold = true;
    }

    public void holdOff() {
        hold = false;
    }

    public void gridOn() {
        xGrid = true;
        yGrid = true;
    }

    public void gridOff() {
        xGrid = false;
        yGrid = false;
    }

    public void boxOn() {
        box = true;
    }

    public void boxOff() {
        box = false;
    }

    /**
     * Clears the whole axes object (deleting all plottable objects, labels, and
     * axes Lims, Ticks, and Ticklabels.
     * <p>
     * Calls
     *
     * @link #clearPlots(),
     * @link #clearXAxes(),
     * @link #clearYAxes() and clears all labels.
     */
    public void clear() {
        clearPlots();
//        labelObjects.clear(); // Attention (WG 29-Apr-2014): you delete all labelObjects, but there is no way to re-instantiate them
//        xLabel = null; yLabel = null; title = null;
        clearXAxes();
        clearYAxes();
    } // clear

    /**
     * Clears all plots on the axes object, but leaves the axis properties and
     * the labels unchanged.
     *
     * @see #clear()
     * @see #clearXAxes()
     * @see #clearYAxes()
     */
    public void clearPlots() {
        plottableObjects.clear();
    }

    /**
     * Clears the x Lims, Ticks, and TickLabels.
     *
     * @see #clear()
     */
    public void clearXAxes() {
        setXLimMode("auto");
        xLim[0] = Double.MAX_VALUE;
        xLim[1] = Double.MIN_VALUE;
        minX = Double.MAX_VALUE;
        maxX = Double.MIN_VALUE;
        setXTickMode("auto");
        Arrays.fill(xTick, Double.NaN);
        setXTickLabelMode("auto");
        Arrays.fill(xTickLabel, null);
    } // clearXAxes()

    /**
     * Clears the y Lims, Ticks, and TickLabels.
     *
     * @see #clear()
     */
    public void clearYAxes() {
        setYLimMode("auto");
        yLim[0] = Double.MAX_VALUE;
        yLim[1] = Double.MIN_VALUE;
        minY = Double.MAX_VALUE;
        maxY = Double.MIN_VALUE;
        setYTickMode("auto");
        Arrays.fill(yTick, Double.NaN);
        setYTickLabelMode("auto");
        Arrays.fill(yTickLabel, null);
    } // clearYAxes

    public PlottableObject gco() {
        return plottableObjects.getLast();
    } // gco

    // ############# ALL PLOT METHODS ###############
    public Plot plot(double x, double y) {
        return new Plot(x, y, "", this);
    } // at.ofai.music.plot(x, y)

    public Plot plot(double x, double y, String lineSpecs) {
        return new Plot(x, y, lineSpecs, this);
    } // at.ofai.music.plot(x, y, lineSpecs)

    public Plot plot(double[] y) {
        return new Plot(y, "", this);
    } // at.ofai.music.plot(y[])

    public Plot plot(double[] y, String lineSpecs) {
        return new Plot(y, lineSpecs, this);
    } // at.ofai.music.plot(y[], lineSpecs)

    public Plot plot(double[] y, int i2) {
        return new Plot(y, i2, "", this);
    } // at.ofai.music.plot(y[], i2)

    public Plot plot(double[] y, int i2, String lineSpecs) {
        return new Plot(y, i2, lineSpecs, this);
    } // at.ofai.music.plot(y[], i2)

    public Plot plot(double[] y, int i1, int i2) {
        return new Plot(y, i1, i2, "", this);
    } // at.ofai.music.plot(y[], i1, i2)

    public Plot plot(double[] y, int i1, int i2, String lineSpecs) {
        return new Plot(y, i1, i2, lineSpecs, this);
    } // at.ofai.music.plot(y[], i1, i2, lineSpecs)

    public Plot plot(double[] x, double[] y) {
        return new Plot(x, y, "", this);
    } // at.ofai.music.plot(x[], y[])

    public Plot plot(double[] x, double[] y, String lineSpecs) {
        return new Plot(x, y, lineSpecs, this);
    } // at.ofai.music.plot(x[], y[])

    public Plot plot(double[] x, double[] y, int i2) {
        return new Plot(x, y, i2, "", this);
    } // at.ofai.music.plot(x[], y[], i2)

    public Plot plot(double[] x, double[] y, int i2, String lineSpecs) {
        return new Plot(x, y, i2, lineSpecs, this);
    } // at.ofai.music.plot(x[], y[], i2)

    public Plot plot(double[] x, double[] y, int i1, int i2) {
        return new Plot(x, y, i1, i2, "", this);
    } // at.ofai.music.plot(x[], y[], i1, i2)

    public Plot plot(double[] x, double[] y, int i1, int i2, String lineSpecs) {
        return new Plot(x, y, i1, i2, lineSpecs, this);
    } // at.ofai.music.plot(x[], y[], i1, i2)

    public Plot plot(LinkedList<Double> y) {
        return new Plot(y, "", this);
    } // at.ofai.music.plot(LinkedList<Double>)

    public Plot plot(LinkedList<Double> y, String lineSpecs) {
        return new Plot(y, lineSpecs, this);
    } // at.ofai.music.plot(LinkedList<Double>)

    public Plot plot(LinkedList<Double> x, LinkedList<Double> y) {
        return new Plot(x, y, "", this);
    } // at.ofai.music.plot(LinkedList<Double>, LinkedList<Double>)

    public Plot plot(LinkedList<Double> x, LinkedList<Double> y,
            String lineSpecs) {
        return new Plot(x, y, lineSpecs, this);
    } // at.ofai.music.plot(LinkedList<Double>, LinkedList<Double>)

    public Text text(double x, double y, String s) {
        return new Text(x, y, s, this);
    } // text(x, y, Str)

    public Line line(double x1, double y1, double x2, double y2) {
        return new Line(x1, y1, x2, y2, this);
    } // line(x1, y1, y1, y2)

    public at.ofai.music.plot.Rectangle rect() {
        return new at.ofai.music.plot.Rectangle(this);
    } // rect()

    public at.ofai.music.plot.Rectangle rect(double x, double y, double w,
            double h) {
        return new at.ofai.music.plot.Rectangle(x, y, w, h, this);
    } // rect(x, y, w, h)

    public at.ofai.music.plot.Rectangle rect(double x, double y, double w,
            double h, double xc, double yc) {
        return new at.ofai.music.plot.Rectangle(x, y, w, h, xc, yc, this);
    } // rect(x, y, w, h, xc, yc)

    /**
     * Creates a WormPlot into the Axes
     *
     * @return WormPlot
     */
    public WormPlot wormPlot() {
        return new WormPlot(this);
    } // WormPlot()

    /**
     * Creates a WormPlot into the Axes with a specified wormLength
     *
     * @param wormLength length of the visible worm
     * @return WormPlot
     */
    public WormPlot wormPlot(int wormLength) {
        return new WormPlot(this, wormLength);
    } // WormPlot(wormLength)

    /**
     * Creates a keyboard at the left side with a depth of 10% of the window
     * width.
     *
     * @return keyboard plottable object
     */
    public Keyboard keyboard() {
        return keyboard("left");
    } // keyboard()

    /**
     * Constructs a keyboard at the specified position ("left" "right" "top"
     * "bottom") with the black keys being attached to either the left or the
     * top side of the keyboard frame (or the reverse) and with the specified
     * depth (in plotting units of the axes).
     *
     * @param location ["left"] "right" "top" "bottom"
     * @return keyboard plottable object
     */
    public Keyboard keyboard(String location) {
        return keyboard(location, true);
    } // keyboard()

    /**
     * Constructs a keyboard at the specified position ("left" "right" "top"
     * "bottom") with the black keys being attached to either the left or the
     * top side of the keyboard frame (or the reverse) and with the specified
     * depth (in plotting units of the axes).
     *
     * @param location ["left"] "right" "top" "bottom"
     * @param blackKeysTopLeft if true (default) the black keys are attached to
     * either the left or the top side of the keyboard frame or the reverse
     * (false)
     * @return keyboard plottable object
     */
    public Keyboard keyboard(String location, boolean blackKeysTopLeft) {
        return keyboard(location, blackKeysTopLeft, -1);
    } // keyboard()

    /**
     * Constructs a keyboard at the specified position ("left" "right" "top"
     * "bottom") with the black keys being attached to either the left or the
     * top side of the keyboard frame (or the reverse) and with the specified
     * depth (in plotting units of the axes).
     *
     * @param location ["left"] "right" "top" "bottom"
     * @param blackKeysTopLeft if true (default) the black keys are attached to
     * either the left or the top side of the keyboard frame or the reverse
     * (false)
     * @param depth the depth of the keyboard (length of the keys) in axes units
     * @return keyboard plottable object
     */
    public Keyboard keyboard(String location, boolean blackKeysTopLeft,
            double depth) {
        return keyboard(location, blackKeysTopLeft, depth, this);
    } // keyboard()

    /**
     * Constructs a keyboard at the specified position ("left" "right" "top"
     * "bottom") with the black keys being attached to either the left or the
     * top side of the keyboard frame (or the reverse) and with the specified
     * depth (in plotting units of the axes).
     *
     * @param location ["left"] "right" "top" "bottom"
     * @param blackKeysTopLeft if true (default) the black keys are attached to
     * either the left or the top side of the keyboard frame or the reverse
     * (false)
     * @param depth the depth of the keyboard (length of the keys) in axes units
     * @param axes the Axes object where the keyboard is to be plotted
     * @return keyboard plottable object
     */
    public Keyboard keyboard(String location, boolean blackKeysTopLeft,
            double depth, Axes axes) {
        return new Keyboard(location, blackKeysTopLeft, depth, axes);
    } // keyboard()

    /**
     * Draws a bar plot onto TODO
     *
     * @param data 2-dim data object (x,y)
     * @return Bar object
     */
    public Bar bar(Data2D data) {
        return new Bar(data, this);
    } // bar()

    protected static void out(String str) {
        System.out.print(str);
    } // out

    protected static void outn(String str) {
        System.out.println(str);
    } // outn

    /**
     * @return Returns the backgroundColor.
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param backgroundColor The backgroundColor to set.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * @return Returns the color of the axes.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color The color of the whole axes to set (sets also xColor and
     * yColor in parallel).
     */
    public void setColor(Color color) {
        this.color = color;
        this.xColor = color;
        this.yColor = color;
    }

    /**
     * @return Returns the fontName.
     */
    public String getFontName() {
        return fontName;
    }

    /**
     * @param fontName The fontName of the axes to set (sets also fontName of
     * xLabel, yLabel, and Title).
     */
    public void setFontName(String fontName) {
        this.fontName = fontName;
        if (xLabel != null) {
            xLabel.setFontName(fontName);
        }
        if (yLabel != null) {
            yLabel.setFontName(fontName);
        }
        if (title != null) {
            title.setFontName(fontName);
        }
    }

    /**
     * @return Returns the fontSize.
     */
    public float getFontSize() {
        return fontSize;
    }

    /**
     * @param fontSize The fontSize to set.
     */
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * @return Returns the fontStyle.
     */
    public float getFontStyle() {
        return fontStyle;
    }

    /**
     * @param fontStyle The fontStyle to set.
     */
    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    /**
     * @return Returns the gridColor.
     */
    public Color getGridColor() {
        return gridColor;
    }

    /**
     * @param gridColor The gridColor to set.
     */
    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    /**
     * @return Returns the hold.
     */
    public boolean isHold() {
        return hold;
    }

    /**
     * @param hold The hold to set.
     */
    public void setHold(boolean hold) {
        this.hold = hold;
    }

    /**
     * @return Returns the position of the draw area within the axes object.
     * double array with {x, y, w, h} relative to entire JPanel.
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
     * @return Returns the scaleAxes.
     */
    public boolean isScaleAxes() {
        return scaleAxes;
    }

    /**
     * @param scaleAxes The scaleAxes to set (whether fonts and markers scale
     * with size of axes or not).
     */
    public void setScaleAxes(boolean scaleAxes) {
        this.scaleAxes = scaleAxes;
    }

    /**
     * @return Returns the xAxisLocation (<code>"top" (default) -
     * "bottom"</code>).
     */
    public String getXAxisLocation() {
        return xAxisLocation;
    }

    /**
     * @param axisLocation The xAxisLocation to set (<code>"top" (default) -
     * "bottom"</code>).
     */
    public void setXAxisLocation(String axisLocation) {
        if (axisLocation.equals("bottom") || axisLocation.equals("top")) {
            xAxisLocation = axisLocation;
            if (xLabel != null) {
                xLabel.updateLabelPosition(getPosition());
            }
        } else {
            err("xAxisLocation must be either 'bottom' or 'top'.");
        }
    }

    /**
     * @return Returns the xColor.
     */
    public Color getXColor() {
        return xColor;
    }

    /**
     * @param color The xColor to set.
     */
    public void setXColor(Color color) {
        xColor = color;
    }

    /**
     * @return Returns the xDir (<code>"normal" (default) -- "reverse"</code>).
     */
    public String getXDir() {
        return xDir;
    }

    /**
     * @param dir The xDir to set (<code>"normal" (default) --
     * "reverse"</code>).
     */
    public void setXDir(String dir) {
        if (dir.equals("normal") || dir.equals("reverse")) {
            xDir = dir;
        } else {
            err("xDir either 'normal' or 'reverse'");
        }
    }

    /**
     * @return Returns the xFact (scale factor of x data). See also xShift.
     */
    public double getXFact() {
        return xFact;
    }

    /**
     * @param fact The xFact to set (scale factor of x data). See also xShift.
     */
    public void setXFact(double fact) {
        xFact = fact;
    }

    /**
     * @return Returns the xGrid.
     */
    public boolean isXGrid() {
        return xGrid;
    }

    /**
     * @param grid The xGrid to set.
     */
    public void setXGrid(boolean grid) {
        xGrid = grid;
    }

    /**
     * Returns the xLabel object.
     *
     * @return Returns the xLabel.
     */
    public Label getXLabel() {
        return xLabel;
    }

    /**
     * Sets the yLabel string or creates it.
     *
     * @param label String of xLabel to set.
     * @return xLabel Returns the xLabel object
     * @see Text
     */
    public Label setXLabel(String label) {
        if (xLabel == null) {
            xLabel = new Label(label, this, Label.IS_XLABEL);
        } else {
            xLabel.setString(label);
        }
        return xLabel;
    }

    /**
     * @return Returns the xLim.
     */
    public double[] getXLim() {
        if (getXLimMode().equals("auto")) {
            return getXZoom();
        } else {
            return xLim;
        }
    }

    protected double[] getXZoom() {
        double[] lim = new double[2];
        lim[0] = xLim0;
        lim[1] = xLim1;
        return lim;
    }

    /**
     * @param lim The xLim to set (double[2]).
     */
    public void setXLim(double[] lim) {
        if (lim.length == 2) {
            xLim = lim;
            if (xTickMode.equals("auto")) {
                Arrays.fill(xTick, Double.NaN);
            }
            if (xTickLabelMode.equals("auto")) {
                Arrays.fill(xTickLabel, null);
            }
            setXLimMode("manual");
            xZoom = false;
        } else {
            err("xLim must be a double[2].");
        }
    }

    /**
     * @return Returns the xLimMode (["auto"] -- "normal").
     */
    public String getXLimMode() {
        return xLimMode;
    }

    /**
     * @param limMode The xLimMode to set (["auto"] -- "normal").
     */
    public void setXLimMode(String limMode) {
        if (limMode.equals("auto") || limMode.equals("manual")) {
            xLimMode = limMode;
        } else {
            err("xLimMode must be either 'auto' or 'normal'");
        }
    }

    /**
     * @return Returns the xScale (["linear"] -- "log").
     */
    public String getXScale() {
        return xScale;
    }

    /**
     * @param scale The xScale to set (["linear"] -- "log").
     */
    public void setXScale(String scale) {
        switch (scale) {
            case "log":
                xLog = true;
                xScale = scale;
                setXMinorGrid(true);
                if (xLimMode.equals("auto")) {
                    minX = 0.1;
                }
                maxX = 1.0;
                break;
            case "linear":
                xLog = false;
                xScale = scale;
                break;
            default:
                err("xScale must be 'linear' or 'log'.");
                break;
        }
    }

    /**
     * @return Returns the xShift (linear transform of x data). See also xFact.
     */
    public double getXShift() {
        return xShift;
    }

    /**
     * @param shift The xShift to set (linear transform of x data). See also
     * xFact.
     */
    public void setXShift(double shift) {
        xShift = shift;
    }

    /**
     * @return Returns the xTick.
     */
    public double[] getXTick() {
        return xTick;
    }

    /**
     * @param tick The xTick to set.
     */
    public void setXTick(double[] tick) {
        xTick = tick;
        setXTickMode("manual");
    }

    /**
     * @return Returns the xTickLabel.
     */
    public String[] getXTickLabel() {
        return xTickLabel;
    }

    /**
     * Sets the xTickLabel as an array of Strings. Superscript with "^",
     * subscript with "_"; e.g., "10^6", "10^8", "10^{10}", etc. To have no tick
     * labels, use this: ax.setXTickLabel(new String[]{""});
     *
     * @param tickLabel The xTickLabel to set.
     * @see Text#stringToAttString(String)
     */
    public void setXTickLabel(String[] tickLabel) {
        xTickLabel = tickLabel;
        setXTickLabelMode("manual");
    }

    /**
     * @return Returns the xTickLabelMode ("manual" -- "auto").
     */
    public String getXTickLabelMode() {
        return xTickLabelMode;
    }

    /**
     * @param tickLabelMode The xTickLabelMode ("manual" -- "auto") to set.
     */
    public void setXTickLabelMode(String tickLabelMode) {
        xTickLabelMode = tickLabelMode;
    }

    /**
     * @return Returns the xTickMode ("manual" -- "auto").
     */
    public String getXTickMode() {
        return xTickMode;
    }

    /**
     * @param tickMode The xTickMode to set ("manual" -- ["auto"]).
     */
    public void setXTickMode(String tickMode) {
        xTickMode = tickMode;
    }

    /**
     * @return Returns the yAxisLocation
     * (<code>"left" (default) - "right"</code>).
     */
    public String getYAxisLocation() {
        return yAxisLocation;
    }

    /**
     * @param axisLocation The yAxisLocation to set
     * (<code>"left" (default) - "right"</code>).
     */
    public void setYAxisLocation(String axisLocation) {
        if (axisLocation.equals("left") || axisLocation.equals("right")) {
            yAxisLocation = axisLocation;
            if (yLabel != null) {
                yLabel.updateLabelPosition(getPosition());
            }
        } else {
            err("yAxisLocation must be either 'left' or 'right'.");
        }
    }

    /**
     * @return Returns the yColor.
     */
    public Color getYColor() {
        return yColor;
    }

    /**
     * @param color The yColor to set.
     */
    public void setYColor(Color color) {
        yColor = color;
    }

    /**
     * @return Returns the yDir (["normal"] -- "reverse").
     */
    public String getYDir() {
        return yDir;
    }

    /**
     * @param dir The yDir to set (["normal"] -- "reverse").
     */
    public void setYDir(String dir) {
        if (dir.equals("normal") || dir.equals("reverse")) {
            yDir = dir;
        } else {
            err("yDir must be either 'normal' or 'reverse'.");
        }
    }

    /**
     * @return Returns the yFact (y data scaling), see also yShift.
     */
    public double getYFact() {
        return yFact;
    }

    /**
     * @param fact The yFact to set (y data scaling), see also yShift.
     */
    public void setYFact(double fact) {
        yFact = fact;
    }

    /**
     * @return Returns the yGrid.
     */
    public boolean isYGrid() {
        return yGrid;
    }

    /**
     * @param grid The yGrid to set.
     */
    public void setYGrid(boolean grid) {
        yGrid = grid;
    }

    /**
     * @return Returns the yLabel.
     */
    public Label getYLabel() {
        return yLabel;
    }

    /**
     * Creates or sets the yLabel.
     *
     * @param label String of yLabel to set.
     * @return yLabel Returns the yLabel object
     * @see Text
     */
    public Label setYLabel(String label) {
        if (yLabel == null) {
            yLabel = new Label(label, this, Label.IS_YLABEL);
        } else {
            yLabel.setString(label);
        }
        return yLabel;
    }

    /**
     * @return Returns the yLim (double[2]).
     */
    public double[] getYLim() {
        if (getYLimMode().equals("auto")) {
            return getYZoom();
        } else {
            return yLim;
        }
    }

    protected double[] getYZoom() {
        double[] lim = new double[2];
        lim[0] = yLim0;
        lim[1] = yLim1;
        return lim;
    }

    /**
     * @param lim The yLim to set.
     */
    public void setYLim(double[] lim) {
        if (lim.length == 2) {
            yLim = lim;
            if (yTickMode.equals("auto")) {
                Arrays.fill(yTick, Double.NaN);
            }
            if (yTickLabelMode.equals("auto")) {
                Arrays.fill(yTickLabel, null);
            }
            setYLimMode("manual");
        } else {
            err("yLim must be double[2].");
        }
    }

    /**
     * @return Returns the yLimMode (["auto"] -- "manual").
     */
    public String getYLimMode() {
        return yLimMode;
    }

    /**
     * @param limMode The yLimMode to set (["auto"] -- "manual").
     */
    public void setYLimMode(String limMode) {
        if (limMode.equals("auto") || limMode.equals("manual")) {
            yLimMode = limMode;
        } else {
            err("yLimMode must be 'auto' or 'manual'.");
        }
    }

    /**
     * @return Returns the yScale (["linear"] -- 'log").
     */
    public String getYScale() {
        return yScale;
    }

    /**
     * @param scale The yScale to set (["linear"] -- 'log").
     */
    public void setYScale(String scale) {
        switch (scale) {
            case "log":
                yLog = true;
                yScale = scale;
                setYMinorGrid(true);
                if (yLimMode.equals("auto")) {
                    minY = 0.1;
                }
                maxY = 1.0;
                break;
            case "linear":
                yLog = false;
                yScale = scale;
                break;
            default:
                err("yScale must be 'linear' or 'log'.");
                break;
        }
    }

    /**
     * @return Returns the yShift (linear y data scaling), see also yFact.
     */
    public double getYShift() {
        return yShift;
    }

    /**
     * @param shift The yShift to set (linear y data scaling), see also yFact.
     */
    public void setYShift(double shift) {
        yShift = shift;
    }

    /**
     * @return Returns the yTick.
     */
    public double[] getYTick() {
        return yTick;
    }

    /**
     * @param tick The yTick to set (double[]).
     */
    public void setYTick(double[] tick) {
        yTick = tick;
        setYTickMode("manual");
    }

    /**
     * @return Returns the yTickLabel.
     */
    public String[] getYTickLabel() {
        return yTickLabel;
    }

    /**
     * Sets the yTickLabel as an array of Strings. Superscript with "^",
     * subscript with "_". To have no tick labels, use this:
     * ax.setYTickLabel(new String[]{""});
     *
     * @param tickLabel The yTickLabel to set.
     * @see Text#stringToAttString(String)
     */
    public void setYTickLabel(String[] tickLabel) {
        yTickLabel = tickLabel;
        setYTickLabelMode("manual");
    }

    /**
     * @return Returns the yTickLabelMode. ("manual" -- "auto")
     */
    public String getYTickLabelMode() {
        return yTickLabelMode;
    }

    /**
     * @param tickLabelMode The yTickLabelMode to set. ("manual" -- "auto")
     */
    public void setYTickLabelMode(String tickLabelMode) {
        yTickLabelMode = tickLabelMode;
    }

    /**
     * @return Returns the yTickMode. ("manual" -- "auto")
     */
    public String getYTickMode() {
        return yTickMode;
    }

    /**
     * @param tickMode The yTickMode to set. ("manual" -- "auto")
     */
    public void setYTickMode(String tickMode) {
        yTickMode = tickMode;
    }

    /**
     * Returns the title object (or null).
     *
     * @return Returns the title object.
     * @see #setTitle(String)
     */
    public Title getTitle() {
        return title;
    }

    /**
     * Creates or updates the title object.
     *
     * @param title The title to set.
     * @return title Returns the title object
     * @see Text
     */
    public Title setTitle(String title) {
        if (this.title == null) {
            this.title = new Title(title, this, Title.IS_TITLE);
        } else {
            this.title.setString(title);
        }
        return this.title;
    }

    private void err(String err) {
        System.err.print(err);
    }

    /**
     * @return Returns the v (verbose output).
     */
    public boolean isV() {
        return v;
    }

    /**
     * @param v The v to set (verbose output).
     */
    public void setV(boolean v) {
        this.v = v;
    }

    /**
     * Set the figure frame (figFrame) as alternative to Figure object. To be
     * used, when default constructor without arguments created an axes object.
     *
     * @param jFrame
     */
    public void setFigFrame(JFrame jFrame) {
        figFrame = jFrame;
    }

    /**
     * @return Returns the axis (XMIN, XMAX, YMIN, YMAX) of the Axes;
     * @see #getXLim() and
     * @see #getYLim().
     */
    public double[] getAxis() {
        double[] lim = getXLim();
        double[] axis = new double[4];
        axis[0] = lim[0];
        axis[1] = lim[1];
        lim = getYLim();
        axis[2] = lim[2];
        axis[3] = lim[3];
        return axis;
    }

    /**
     * @param axis (XMIN, XMAX, YMIN, YMAX) to set (invokes calls of
     * {@link #setXLim(double[])}, {@link #setYLim(double[])}).
     */
    public void setAxis(double[] axis) {
        double[] xlim = new double[2];
        xlim[0] = axis[0];
        xlim[1] = axis[1];
        setXLim(xlim);
        double[] ylim = new double[2];
        ylim[0] = axis[2];
        ylim[1] = axis[3];
        setYLim(ylim);
    } // setAxis

    /**
     * @return Returns the antialiasing.
     */
    public boolean isAntialiasing() {
        return antialiasing;
    }

    /**
     * Sets the antialiasing option of the {@link Graphics2D} environment.
     *
     * @param antialiasing The antialiasing to set.
     * @see RenderingHints#KEY_ANTIALIASING
     */
    public void setAntialiasing(boolean antialiasing) {
        this.antialiasing = antialiasing;
    }

    /**
     * Returns the scale factor for resizing texts and markersizes when the
     * Figure gets resized (at the default size it is 1). Returns 0.0, if
     * {@link #isScaleAxes()} is false.
     *
     * @return Returns the scaleRef.
     * @see #isScaleAxes()
     * @see #setScaleAxes(boolean)
     */
    public double getScaleRef() {
        if (isScaleAxes()) {
            return scaleRef;
        } else {
            return 0.0;
        }
    }

    /**
     * @return Returns the xMinorGrid.
     */
    public boolean isXMinorGrid() {
        return xMinorGrid;
    }

    /**
     * @param minorGrid The xMinorGrid to set.
     */
    public void setXMinorGrid(boolean minorGrid) {
        setYMinorTick(true);
        xMinorGrid = minorGrid;
    }

    /**
     * @return Returns the xMinorTick.
     */
    public boolean isXMinorTick() {
        return xMinorTick;
    }

    /**
     * @param minorTick The xMinorTick to set.
     */
    public void setXMinorTick(boolean minorTick) {
        xMinorTick = minorTick;
    }

    /**
     * @return Returns the yMinorGrid.
     */
    public boolean isYMinorGrid() {
        return yMinorGrid;
    }

    /**
     * @param minorGrid The yMinorGrid to set.
     */
    public void setYMinorGrid(boolean minorGrid) {
        setYMinorTick(true);
        yMinorGrid = minorGrid;
    }

    /**
     * @return Returns the yMinorTick.
     */
    public boolean isYMinorTick() {
        return yMinorTick;
    }

    /**
     * @param minorTick The yMinorTick to set.
     */
    public void setYMinorTick(boolean minorTick) {
        yMinorTick = minorTick;
    }

    /**
     * @return Returns the guiMode.
     */
    public int getGuiMode() {
        return guiMode;
    }

    /**
     * @param guiMode The guiMode to set.
     */
    public void setGuiMode(int guiMode) {
        Axes.guiMode = guiMode;
    }

    protected void drawSelection(Rectangle2D r) {
        selectionRect = r;
    } // drawSelection

    /**
     * @param zoom The xZoom to set.
     */
    public void setXZoom(boolean zoom) {
        xZoom = zoom;
        if (xTickMode.equals("auto")) {
            Arrays.fill(xTick, Double.NaN);
        }
        if (xTickLabelMode.equals("auto")) {
            Arrays.fill(xTickLabel, null);
        }
    } // setXZoom

    /**
     * Sets the x Zoom
     *
     * @param x0
     * @param x1
     */
    public void setXZoom(double x0, double x1) {
        setXZoom(true);
        xLim0 = x0;
        xLim1 = x1;
    } // setXZoom

    /**
     * @param zoom The yZoom to set.
     */
    public void setYZoom(boolean zoom) {
        yZoom = zoom;
        if (yTickMode.equals("auto")) {
            Arrays.fill(yTick, Double.NaN);
        }
        if (yTickLabelMode.equals("auto")) {
            Arrays.fill(yTickLabel, null);
        }
    } // setYZoom

    /**
     * Sets the y Zoom
     *
     * @param y0
     * @param y1
     */
    public void setYZoom(double y0, double y1) {
        setYZoom(true);
        yLim0 = y0;
        yLim1 = y1;
    } // setYZoom

    /**
     * Zoom function for arrow keys (with CTRL).
     *
     * @param xAxis
     * @param in
     */
    protected void zoom(boolean xAxis, boolean in) {
        if (xAxis) {
            double[] lim = getXZoom();
            double diff = (lim[1] - lim[0]) / 6;
            if (in) {
                setXZoom(lim[0] + diff, lim[1] - diff);
            } else {
                setXZoom(lim[0] - diff, lim[1] + diff);
            }
        } else { // yAxis
            double[] lim = getYZoom();
            double diff = (lim[1] - lim[0]) / 6;
            if (in) {
                setYZoom(lim[0] + diff, lim[1] - diff);
            } else {
                setYZoom(lim[0] - diff, lim[1] + diff);
            }
        }
    } // zoom()

    /**
     * Translate function for arrow keys (shift axes limits to left/right;
     * up/down).
     *
     * @param xAxis
     * @param upright
     */
    protected void translate(boolean xAxis, boolean upright) {
        if (xAxis) {
            double[] lim = getXZoom();
            double diff = (lim[1] - lim[0]) / 6;
            if (upright) {
                setXZoom(lim[0] + diff, lim[1] + diff);
            } else {
                setXZoom(lim[0] - diff, lim[1] - diff);
            }
        } else { // yAxis
            double[] lim = getYZoom();
            double diff = (lim[1] - lim[0]) / 6;
            if (upright) {
                setYZoom(lim[0] + diff, lim[1] + diff);
            } else {
                setYZoom(lim[0] - diff, lim[1] - diff);
            }
        }
    } // translate()

    /**
     * @return Returns the axesLineWidth.
     */
    public float getAxesLineWidth() {
        return axesLineWidth;
    }

    /**
     * @param axesLineWidth The axesLineWidth to set.
     */
    public void setAxesLineWidth(float axesLineWidth) {
        doStroke = true;
        this.axesLineWidth = axesLineWidth;
    }

    /**
     * @return Returns the defaultAxesLineWidth.
     */
    public float getDefaultAxesLineWidth() {
        return defaultAxesLineWidth;
    }

    /**
     * @return Returns the fig.
     */
    public Figure getFig() {
        return fig;
    }

    /**
     * @return Returns the jFrame
     */
    public JFrame getJFrame() {
        return figFrame;
    }

    public String getEpsName() {
        return epsName;
    }

    public void setEpsName(String epsName) {
        this.epsName = epsName;
    }

    public boolean isForceRedraw() {
        return forceRedraw;
    }

    public void setForceRedraw(boolean _forceRedraw) {
        forceRedraw = _forceRedraw;
    }

    /**
     * Sets the color of the axes frame (usually a light gray).
     *
     * @param _frameColor
     */
    public void setFrameColor(Color _frameColor) {
        frameColor = _frameColor;
    }

    /**
     * Returns the image object to which all graphical elements are plotted.
     * Used to faster draw an axes.
     *
     * Example:      <code>plot.addXY(x,y);
     * plot.renderPoint((Graphics2D)ax.getAxImage().getGraphics(),x,y)
     * ax.repaint();</code>
     *
     * @return
     *
     * public BufferedImage getAxImage() { return axImage; }
     */
    /**
     * Returns the Graphics2D objects of the image object axImage. Antialiasing
     * is set here beforehand; used to draw onto an axes without erasing
     * existing drawings. Used to faster draw an axes.
     *
     * Example:      <code>plot.addXY(x,y);
     * plot.renderPoint(ax.getAxImageGraphics(),x,y)
     * ax.repaint();</code>
     *
     * @return g2 Graphics2D object
     */
    public Graphics2D getAxImageGraphics() {
        Graphics2D gax = (Graphics2D) axImage.getGraphics();
        if (antialiasing) {
            gax.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        return gax;
    }

    public void forceRedrawNext() {
        forceRedrawNext = true;
    }

    /**
     * Returns the scaleFactor of the axes. The scaleFactor is either 1 for
     * normal screens or 2 for retina screens (June 2015).
     *
     * @return
     */
    public float getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Sets the scaleFactor of the axes. The scaleFactor is either 1 for normal
     * screens or 2 for retina screens (June 2015).
     *
     * @param _scaleFactor
     */
    public void setScaleFactor(float _scaleFactor) {
        scaleFactor = _scaleFactor;
    }
} // class Axes

/**
 * Keylistener for the Axes object
 *
 * @author wernerg
 */
class AxesKeyListener implements KeyListener {

    Figure fig;
    Axes ax;

    // boolean
    public AxesKeyListener(Axes a) {
        ax = a;
        fig = ax.getFig();
    } // constructor

    @Override
    public void keyTyped(KeyEvent e) {
        // Axes.outn("Key typed.");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) // CTRL + key
        {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_P:
                    ax.printScreen = true;
                    ax.repaint();
                    break;
                case KeyEvent.VK_LEFT:
                    ax.zoom(true, false);
                    break;
                case KeyEvent.VK_RIGHT:
                    ax.zoom(true, true);
                    break;
                case KeyEvent.VK_UP:
                    ax.zoom(false, true);
                    break;
                case KeyEvent.VK_DOWN:
                    ax.zoom(false, false);
                    break;
                case KeyEvent.VK_EQUALS:
                    ax.zoom(false, true);
                    ax.zoom(true, true);
                    break;
                case KeyEvent.VK_PLUS:
                    ax.zoom(false, true);
                    ax.zoom(true, true);
                    break;
                case KeyEvent.VK_MINUS:
                    ax.zoom(false, false);
                    ax.zoom(true, false);
                    break;
                case KeyEvent.VK_UNDERSCORE:
                    ax.zoom(false, false);
                    ax.zoom(true, false);
                    break;
                case KeyEvent.VK_0:
                    ax.setXZoom(false);
                    ax.setYZoom(false);
                    break;
            }
        } // TODO when ALT pressed, change to - zoom at mouselistener.
        // else if ((e.getModifiers() & KeyEvent.ALT_DOWN_MASK) != 0) // ALT key
        // switch (e.getKeyCode()) {
        // case KeyEvent
        // }
        else if ((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) // SHIFT + key
        {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_P:
                    //fig.setColor(Color.WHITE);
                    //ax.setAxesLineWidth(.5f);
                    //fig.repaint();
                    //fig.print(0);
                    break;
                // fig.setColor(fig.getDefaultColor());
                // ax.setAxesLineWidth(ax.getDefaultAxesLineWidth());
            }
        } else { // normal key
            switch (e.getKeyCode()) {
                case KeyEvent.VK_Z: {
                    ax.setGuiMode(ax.getGuiMode() == Axes.ZOOM ? Axes.DEFAULT
                            : Axes.ZOOM);
                    // Axes.outn("Z pressed");
                    ax.repaint();
                    break;
                }
                case KeyEvent.VK_H: {
                    ax.setGuiMode(ax.getGuiMode() == Axes.PAN ? Axes.DEFAULT
                            : Axes.PAN);
                    // Axes.outn("H pressed");
                    ax.repaint();
                    break;
                }
                case KeyEvent.VK_LEFT:
                    ax.translate(true, false);
                    break;
                case KeyEvent.VK_RIGHT:
                    ax.translate(true, true);
                    break;
                case KeyEvent.VK_UP:
                    ax.translate(false, true);
                    break;
                case KeyEvent.VK_DOWN:
                    ax.translate(false, false);
                    break;
                case KeyEvent.VK_0:
                    ax.setXZoom(false);
                    ax.setYZoom(false);
                    break;
            }
        }
        ax.repaint();
    } // keyPressed

    @Override
    public void keyReleased(KeyEvent e) {
        // Axes.outn("Key released.");
    }
} // class AxesKeyListener

/**
 * A MouseListener for interactive zooming functionalities
 *
 * @author Werner Goebl
 *
 */
class AxesMouseListener extends MouseInputAdapter {

    Axes ax;
    Rectangle2D rect;
    double clickX, clickY;
    double[] clickXLim, clickYLim;

    public AxesMouseListener(Axes a) {
        ax = a;
    } // constructor

    @Override
    public void mouseClicked(MouseEvent e) {
    } // mouseClicked

    @Override
    public void mousePressed(MouseEvent e) {
        if (ax.getGuiMode() == Axes.DEFAULT) {
            return;
        }
        clickX = e.getX();
        clickY = e.getY();
        clickXLim = ax.getXZoom();
        clickYLim = ax.getYZoom();
        if (ax.getGuiMode() == Axes.ZOOM) {
            // Axes.outn("mouse pressed at x/y: "+ clickX + "/" + clickY);
            rect = new Rectangle2D.Double();
            rect.setRect(clickX, clickY, 0.0, 0.0);
            ax.drawSelection(rect);
            ax.repaint();
        } else if (ax.getGuiMode() == Axes.PAN) {
        }
    } // mousePressed

    @Override
    public void mouseReleased(MouseEvent e) {
        if (ax.getGuiMode() == Axes.DEFAULT) {
            return;
        }
        double x = e.getX();
        double y = e.getY();
        double xShift = ax.getXShift();
        double xFact = ax.getXFact();
        double yShift = ax.getYShift();
        double yFact = ax.getYFact();
        if (ax.getGuiMode() == Axes.ZOOM) {
            if (e.getButton() == MouseEvent.BUTTON1) { // left button released
                if (clickX != x && clickY != y) {
                    updateRect(e);
                    switch (ax.getXDir()) {
                        case "normal":
                            ax.setXZoom((rect.getX() - xShift) / xFact, (rect
                                    .getX()
                                    + rect.getWidth() - xShift)
                                    / xFact);
                            break;
                        case "reverse":
                            ax.setXZoom((rect.getX() + rect.getWidth() - xShift)
                                    / xFact, (rect.getX() - xShift) / xFact);
                            break;
                    }
                    switch (ax.getYDir()) {
                        case "normal":
                            ax.setYZoom((rect.getY() + rect.getHeight() - yShift)
                                    / yFact, (rect.getY() - yShift) / yFact);
                            break;
                        case "reverse":
                            ax.setYZoom((rect.getY() - yShift) / yFact, (rect
                                    .getY()
                                    + rect.getHeight() - yShift)
                                    / yFact);
                            break;
                    }
                    ax.drawSelection(null);
                    ax.repaint();
                } else { // simple zoom in and out
                    // Axes.outn("Left mouse buttom released (ZOOM IN).");
                    double rg4 = (ax.getXLim()[1] - ax.getXLim()[0]) / 3;
                    double userX = (x - xShift) / xFact;
                    ax.setXZoom(userX - rg4, userX + rg4);
                    rg4 = (ax.getYLim()[1] - ax.getYLim()[0]) / 3;
                    double userY = (y - yShift) / yFact;
                    ax.setYZoom(userY - rg4, userY + rg4);
                    ax.repaint();
                }
            }
        } // if ZOOM

        // right button: reset back to original
        if (e.getButton() != MouseEvent.BUTTON1) {
            // Axes.outn("Right mouse buttom released (ZOOM ORIGINAL).");
            ax.setXZoom(false);
            ax.setYZoom(false);
            ax.repaint();
        }
    } // mouseReleased

    @Override
    public void mouseDragged(MouseEvent e) {
        if (ax.getGuiMode() == Axes.DEFAULT) {
            return;
        }
        if (ax.getGuiMode() == Axes.ZOOM) {
            updateRect(e);
        } else if (ax.getGuiMode() == Axes.PAN) {
            double deltaX = (clickX - e.getX()) / ax.getXFact();
            ax.setXZoom(clickXLim[0] + deltaX, clickXLim[1] + deltaX);
            double deltaY = (clickY - e.getY()) / ax.getYFact();
            ax.setYZoom(clickYLim[0] + deltaY, clickYLim[1] + deltaY);
            ax.repaint();
        }
    } // mouseDragged

    private void updateRect(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        rect.setRect(clickX < x ? clickX : x, clickY < y ? clickY : y, Math
                .abs(x - clickX), Math.abs(y - clickY));
        ax.drawSelection(rect);
        ax.repaint();
    } // updateRect
} // class AxesMouseListener
