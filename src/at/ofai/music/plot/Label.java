package at.ofai.music.plot;

import java.awt.font.TextAttribute;

/**
 * Special case of a text object; used for x and y labels.
 *
 * @author Werner Goebl, Aug. 2005
 * @see Text
 * @see Title
 */
public class Label extends Text {

    public static final int IS_XLABEL = 1;
    public static final int IS_YLABEL = 2;

    public Label(String string) {
        this(string, Figure.gcf().gca());
    }

    public Label(String string, Axes ax) {
        this(string, ax, 1);
    }

    public Label(String string, Axes ax, int isLabel) {
        super(0, 0, string, ax, isLabel);
        fontStyle = TextAttribute.WEIGHT_BOLD;
        fontSize = 14f;
        updateLabelPosition(ax.getPosition());
    } // constructor

    /**
     * Recalculates the position of the labels (for internal use).
     *
     * @param axPos double[4] of axes position.
     * @see Axes#getPosition()
     */
    protected void updateLabelPosition(double[] axPos) {
        if (isLabel == IS_XLABEL) {
            setHorizontalAlignment("center");
            position[0] = 0.5;
            switch (ax.getXAxisLocation()) {
                case "bottom":
                    setVerticalAlignment("top");
                    position[1] = 1 - axPos[1] / 2;
                    break;
                case "top":
                    setVerticalAlignment("baseline");
                    position[1] = axPos[1] / 2;
                    break;
            }
        } else if (isLabel == IS_YLABEL) {
            setHorizontalAlignment("center");
            position[1] = 0.5;
            setRotation(-90);
            switch (ax.getYAxisLocation()) {
                case "left":
                    setVerticalAlignment("top");
                    position[0] = axPos[0] / 4;
                    break;
                case "right":
                    setVerticalAlignment("bottom");
                    position[0] = 1 - axPos[0] / 4;
                    break;
            }
        }
    } // setLabelPosition
} // Label