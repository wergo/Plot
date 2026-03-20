package at.ofai.music.plot;

public class Data2D {

    double[] x, y;

    public Data2D(double[] x, double[] y) {
        if (x.length != y.length) {
            System.err.println("Bar(): Vectors must have the same length.");
        }
        this.x = x;
        this.y = y;
    } // constructor

    public double[] getX() {
        return x;
    }

    public void setX(double[] x) {
        this.x = x;
    }

    public double[] getY() {
        return y;
    }

    public void setY(double[] y) {
        this.y = y;
    }

} // at.ofai.music.plot.Data2D
