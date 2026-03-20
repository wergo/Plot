package at.ofai.music.plot;

import java.awt.Graphics2D;

/**
 * Draws a bar plot. Useful in combination with {@link M#hist(double[], int)}.
 *
 * @author Werner Goebl, Aug. 9, 2006
 * @see M#hist(double[], int)
 * @see PlottableObject
 * @see Axes
 * @see Figure
 */
public class Bar extends PlottableObject {

    protected MyConcurrentQueue<Rectangle> bars;
    protected double binWidth = 0.0; // from 0 to 1
    protected double[] x, y;
    private double dx = 0;

    /**
     * Creates an object of bars into the current {@link Axes} of the current
     * {@link Figure} ( {@link Figure#gcf()} and {@link Figure#gca()}) with x
     * centerBins and y bin heights.
     *
     * @param d the x/y data object
     * @see at.ofai.music.plot.Data2D
     */
    public Bar(Data2D d) {
        this(d, Figure.gcf().gca());
    } // constructor

    /**
     * Creates an object of bars into a the {@link Axes} ax with x centerBins
     * and y bin heights.
     *
     * @param d the x/y data object
     * @param a the Axes object
     * @see at.ofai.music.plot.Data2D
     */
    public Bar(Data2D d, Axes a) {
        bars = new MyConcurrentQueue<>();

        ax = a;
        ax.holdOn();
        ax.plottableObjects.add(this);

        faceColor = defaultColor;
        binWidth = .8;
        x = d.getX();
        y = d.getY();
        double sum = 0.0;
        for (int i = 1; i < x.length; i++) { // calc diff of x
            sum = x[i] - x[i - 1];
        }
        dx = sum / (x.length - 1);
        double w = binWidth * (dx);
        for (int i = 0; i < x.length; i++) {
            bars.add(ax.rect(x[i] - (w / 2.0), 0, w, y[i]));
        }
        ax.setXLim(new double[]{M.min(x) - w, M.max(x) + w});
        ax.setYLim(new double[]{M.min(y), M.max(y) * 1.05});
    } // constructor

    /**
     * Redraws the property values of the object.
     *
     * @param g
     */
    @Override
    public void render(Graphics2D g) {
        double w = binWidth * (dx);
        int i = 0;
        for (Rectangle r : bars) {
            r.setColor(color);
            r.setEdgeColor(edgeColor);
            r.setFaceColor(faceColor);
            r.setLineStyle(lineStyle);
            r.setLineWidth(lineWidth);
            r.setPosition(new double[]{x[i] - w / 2.0, 0, w, y[i]});
            i++;
        }
    } // render()

    /**
     * Returns the bin width (between 1 and 0).
     *
     * @return binWidth
     */
    public double getBinWidth() {
        return binWidth;
    }

    /**
     * Determines the width of the bins. 1 means the borders of the bins touch
     * each other; default width is 0.8.
     *
     * @param binWidth
     */
    public void setBinWidth(double binWidth) {
        this.binWidth = binWidth;
    }
} // Bar
