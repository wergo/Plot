package at.ofai.music.plot;

import java.awt.font.TextAttribute;

/**
 * Creates the axes title (special case of Text object).
 *
 * @author Werner Goebl, Aug. 2005
 * @see PlottableObject
 * @see Text
 */
public class Title extends Text {

    public static final int IS_TITLE = 3;

    public Title(String string) {
        this(string, Figure.gcf().gca());
    } // constructor

    public Title(String string, Axes ax) {
        this(string, ax, IS_TITLE);
    } // constructor

    public Title(String string, Axes ax, int isTitle) {
        super(0, 0, string, ax, isTitle);
        fontStyle = TextAttribute.WEIGHT_BOLD;
        fontSize = 16f;
        updateTitlePosition(ax.getPosition());
    } // constructor

    /**
     * Recalculates the position of the title (for internal use).
     *
     * @param axPos double[4] of axes position.
     * @see Axes#getPosition()
     */
    protected void updateTitlePosition(double[] axPos) {
        if (isLabel == IS_TITLE) {
            setHorizontalAlignment("center");
            position[0] = 0.5;
            setVerticalAlignment("top");
            position[1] = (1 - (axPos[1] + axPos[3])) / 4;
        }
    } // setTitlePosition

} // class Title
