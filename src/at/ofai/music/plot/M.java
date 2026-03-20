package at.ofai.music.plot;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A useful class providing various mathematical basic functionalities that
 * might be of use more than once.
 *
 * @author Werner Goebl, Aug. 2005; Aug. 2006; April 2007
 */
public class M {

    /**
     * Returns the minimum value of an array.
     *
     * @param x input array
     * @return minimum value
     */
    public static int min(int[] x) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < x.length; i++) {
            if (x[i] < min) {
                min = x[i];
            }
        }
        return min;
    } // min()

    /**
     * Returns the minimum value of an array.
     *
     * @param x input array
     * @return minimum value
     */
    public static double min(double[] x) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < x.length; i++) {
            if (x[i] < min) {
                min = x[i];
            }
        }
        return min;
    } // min()

    /**
     * Returns the minimum value of an array.
     *
     * @param x input LinkedList<Double>
     * @return minimum value
     */
    public static double min(LinkedList<Double> x) {
        double min = Double.MAX_VALUE;
        for (Double i : x) {
            if (i < min) {
                min = i;
            }
        }
        return min;
    } // min()

    /**
     * Returns the maximum value of an array.
     *
     * @param x input array
     * @return maximum value
     */
    public static int max(int[] x) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < x.length; i++) {
            if (x[i] > max) {
                max = x[i];
            }
        }
        return max;
    } // max()

    /**
     * Returns the maximum value of an array.
     *
     * @param x input array
     * @return maximum value
     */
    public static double max(double[] x) {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < x.length; i++) {
            if (x[i] > max) {
                max = x[i];
            }
        }
        return max;
    } // max()

    /**
     * Returns the maximum value of an LinkedList<Double>.
     *
     * @param x input array
     * @return maximum value
     */
    public static double max(LinkedList<Double> x) {
        double max = Double.MIN_VALUE;
        for (Double i : x) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    } // max()

    /**
     * Returns the arithmetic mean value of an array.
     *
     * @param x input array
     * @return arithmetic mean
     */
    public static double mean(double[] x) {		// TODO fix all other mean 
        double sum = 0.0;
        int c = 0;
        for (int i = 0; i < x.length; i++) {
            if (!Double.isNaN(x[i])) {
                //outn("Number="+x[i]);
                sum += x[i];
                c++;
            }
        }
        return sum / c;
    } // mean()

    /**
     * Returns the arithmetic mean value of an array.
     *
     * @param x input array
     * @return arithmetic mean
     */
    public static double mean(long[] x) {
        return ((double) sum(x)) / x.length;
    } // mean()

    /**
     * Returns the arithmetic mean value of an LinkedList<Double>.
     *
     * @param x input array
     * @return arithmetic mean
     */
    public static double mean(LinkedList<Double> x) {
        return sum(x) / x.size();
    } // mean()

    /**
     * Returns the differences between adjacent values.
     *
     * @param x input array of doubles
     * @return out array of doubles (one element shorter than x)
     */
    public static double[] diff(double[] x) {
        double[] diffs = new double[x.length - 1];
        for (int i = 1; i < x.length; i++) {
            diffs[i - 1] = x[i] - x[i - 1];
        }
        return diffs;
    }

    /**
     * Returns the mean of the pairwise differences (same as mean(diff(x)), just
     * faster).
     *
     * @param x
     * @return double
     */
    public static double meandiff(double[] x) {
        double sum = 0;
        int i;
        double[] diffs = new double[x.length - 1];
        for (i = 1; i < x.length; i++) {
            sum += (x[i] - x[i - 1]);
        }
        return sum / (i - 1);
    }

    /**
     * Returns the sum of an array.
     *
     * @param x input array
     * @return sum of the array
     */
    public static double sum(double[] x) { // TODO check all other sums for NaN
        double sum = 0.0;
        for (int i = 0; i < x.length; i++) {
            if (!Double.isNaN(x[i])) {
                sum += x[i];
            }
        }
        return sum;
    } // sum()

    /**
     * Returns the sum of an array.
     *
     * @param x input array
     * @return sum of the array
     */
    public static long sum(long[] x) {
        long sum = 0l;
        for (int i = 0; i < x.length; i++) {
            sum += x[i];
        }
        return sum;
    } // sum()

    /**
     * Returns the sum of an LinkedList<Double>.
     *
     * @param x input array
     * @return sum of the LinkedList<Double>
     */
    public static double sum(LinkedList<Double> x) {
        double sum = 0.0;
        for (Double i : x) {
            sum += i;
        }
        return sum;
    } // sum()

    /**
     * Returns the variance of an array.
     *
     * @param x input array
     * @return variance
     */
    public static double var(double[] x) {
        double sum = 0.0;
        double sumSqr = 0.0;
        int i;
        for (i = 0; i < x.length; i++) {
            sum += x[i];
            sumSqr += x[i] * x[i];
        }
        return (sumSqr - sum * sum / i) / i;
    } // var()

    /**
     * Returns the standard deviation of an array.
     *
     * @param x input array
     * @return standard deviation
     */
    public static double std(double[] x) {
        return Math.sqrt(var(x));
    }

    /**
     * Returns the variance of a LinkedList of Double.
     *
     * @param x input array
     * @return variance
     */
    public static double var(LinkedList<Double> x) {
        double sum = 0.0;
        double sumSqr = 0.0;
        int i = 1;
        for (Iterator<Double> it = x.iterator(); it.hasNext();) {
            double v = it.next();
            i++;
            sum += v;
            sumSqr += v * v;
        }
        return (sumSqr - sum * sum / i) / i;
    } // var()

    /**
     * Returns the standard deviation of a LinkedList of Double.
     *
     * @param x input array
     * @return standard deviation
     */
    public static double std(LinkedList<Double> x) {
        return Math.sqrt(var(x));
    } // var()

    /**
     * Calculates auto correlation of the array X, returning an array double[]
     * of the same length (with 1 as the first and last value).
     *
     * @param X input array int[].
     * @return double[] (with 1 as first and last value).
     */
    public static double[] autoCorr(int[] X) {
        return autoCorr(X, X.length);
    }

    /**
     * Calculates an auto correlation of the array int[] X until the maximum
     * shift of int maxShift, returning an array of correlation coefficients
     * with the length maxShift.
     *
     * @param X
     * @param maxShift
     * @return double[] with the lenght maxShift, with 1 as the first value
     */
    public static double[] autoCorr(int[] X, int maxShift) {
        int mx = maxShift;
        if (mx > X.length) {
            mx = X.length;
        }
        double[] Rs = new double[mx];
        for (int shift = 0; shift < mx; shift++) {
            int[] xx = new int[mx - shift + 1];
            int[] yy = new int[mx - shift + 1];
            for (int i = shift; i < mx; i++) {
                xx[i - shift] = X[i - shift];
                yy[i - shift] = X[i];
            }
            double r = M.calcCorr(xx, yy);
            Rs[shift] = r;
            //System.out.print("r="+r+", ");
        }
        return Rs;
    } // autoCorr

    /**
     * Computes the Pearson's correlation coefficient of the arrays x and y and
     * returns a double (between -1 and 1)
     *
     * @param x input array (int[]).
     * @param y input array (int[]); must be same length as x.
     * @return correlation coefficient between -1 and 1.
     */
    public static double calcCorr(int[] x, int[] y) {
        double Ex = 0.0;
        double Ey = 0.0;
        double Exy = 0.0;
        double Ex2 = 0.0;
        double Ey2 = 0.0;
        int n = x.length;
        for (int i = 0; i < n; i++) {
            Ex += x[i];
            Ey += y[i];
            Exy += x[i] * y[i];
            Ex2 += x[i] * x[i];
            Ey2 += y[i] * y[i];
        }
        return (Exy - (Ex * Ey / n)) / Math.sqrt((Ex2 - Ex * Ex / n) * (Ey2 - Ey * Ey / n));
    }

    /**
     * Computes the Pearson's correlation coefficient of the arrays x and y and
     * returns a double (between -1 and 1)
     *
     * @param x input array (double[]).
     * @param y input array (double[]); must be same length as x.
     * @return correlation coefficient between -1 and 1.
     */
    public static double calcCorr(double[] x, double[] y) {
        double Ex = 0.0;
        double Ey = 0.0;
        double Exy = 0.0;
        double Ex2 = 0.0;
        double Ey2 = 0.0;
        int n = x.length;
        for (int i = 0; i < n; i++) {
            Ex += x[i];
            Ey += y[i];
            Exy += x[i] * y[i];
            Ex2 += x[i] * x[i];
            Ey2 += y[i] * y[i];
        }
        return (Exy - (Ex * Ey / n)) / Math.sqrt((Ex2 - Ex * Ex / n) * (Ey2 - Ey * Ey / n));
    } // calcCorr

    /**
     * Calculates a mode value for the double[] array taking a 10% binWidth
     * rounded to the nearest power of 10. WG, Aug 2005
     *
     * @param arr input array double[]
     * @return double mode value
     */
    public static double mode(double[] arr) {
        boolean v = false; // verbose output
        if (v) {
            out("M.mode(): arr.length=" + arr.length);
            for (int i = 0; i < arr.length; i++) {
                out(", " + arr[i]);
            }
            outn(".");
        }
        if (arr.length == 0) {
            return Double.NaN;
        }
        if (arr.length == 1) {
            return arr[0];
        }
        double binWidth = mean(arr);
        if (v) {
            out("mean=" + binWidth);
        }
        binWidth = Math.pow(10, Math.floor(Math.log10(binWidth))) / 10;
        if (v) {
            outn("--> binWidth=" + binWidth);
        }
        return mode(arr, binWidth);
    } // mode(arr[])

    /**
     * Calculates a mode values (most often value) of the input array double[]
     * using double binWidth as categorisation units. WG, Aug 2005
     *
     * @param arr double[] array of values
     * @param binWidth double bin width
     * @return double mode value (most often value).
     */
    public static double mode(double[] arr, double binWidth) {
        boolean v = false; // verbose output
        if (arr.length == 0) {
            return Double.NaN;
        }
        if (arr.length == 1) {
            return arr[0];
        }
        double min = min(arr);
        if (v) {
            out("min(arr)=" + min);
        }
        double lowerLimit = Math.floor(min / binWidth) * binWidth - binWidth / 2;
        if (v) {
            outn("; lowerLimit=" + lowerLimit);
        }
        double[][] h;
        h = histogram(arr, binWidth, lowerLimit);
        // TODO implement bar into plot package!!
        //if (v) Stat.histogramBinsPlot(arr, binWidth, lowerLimit);
        double tmp = 0.0;
        int tmpi = 0;
        for (int i = 0; i < h[1].length; i++) {
            if (h[1][i] > tmp) {
                tmp = h[1][i];
                tmpi = i;
            }
        }
        if (v) {
            outn("maxn=" + h[1][tmpi] + " at " + h[0][tmpi]);
        }
        return h[0][tmpi];
    } // mode(arr[], binWidth)

    /**
     * Converts a LinkedList into an array.
     *
     * @param L input LinkedList
     * @return output array
     */
    public static double[] lili2arr(LinkedList<Double> L) {
        double[] d = new double[L.size()];
        Iterator<Double> it = L.iterator();
        int i = 0;
        while (it.hasNext()) {
            d[i++] = it.next();
        }
        return d;
    } // lili2arr

    /**
     * INTERP1 1-D interpolation (table lookup); interpolates linearly to find
     * yy, the values of the underlying function y at the points in the array
     * xx. x and y must be vectors of length N.
     *
     * @param x input vector
     * @param y input vector with the same length
     * @param xx x values for the interpolated vector
     * @return (linearly) interpolated yy values
     */
    public static double[] interp1(double[] x, double[] y, double[] xx) {
        double[] yy = new double[xx.length];
        int i = 0;
        for (int ii = 0; ii < xx.length; ii++) {
            while (x[i] < xx[ii] && i < x.length - 1) {
                i++;
            }
            if (i > 0) {
                double a = (y[i] - y[i - 1]) / (x[i] - x[i - 1]);
                yy[ii] = a * (xx[ii] - x[i - 1]) + y[i - 1];
            } else { // i == 0
                double a = (y[i + 1] - y[i]) / (x[i + 1] - x[i]);
                yy[ii] = a * (xx[ii] - x[i]) + y[i];
            }
        }
        return yy;
    } // interp1

    public static void main(String args[]) {
        double[] x = new double[]{1, 3, 4, 6, 18};
        double[] y = new double[]{-2.1, 159, -44.3, 5, -20};
        Figure.splot(x, y, "b-x");
        Figure.gcf().holdOn();
        double[] xx = new double[]{.2, .3, .5, .8, 1.4, 2.3, 2.8, 3, 4, 5, 7,
            8.8, 10, 12, 14, 15.5, 17.6, 19, 21, 23, 25};
        Figure.splot(xx, M.interp1(x, y, xx), "r.--");
        Figure.gcf().gridOn();
        Figure.gcf().repaint();

        Figure fig = new Figure("Histogram");
        Axes ax = fig.gca();
        Bar b = ax.bar(hist(xx, 9));
        b.setEdgeColor(Color.BLACK);
        b.setFaceColor(Color.WHITE);
        b.setBinWidth(.6);
        ax.gridOn();
        fig.validate();
        fig.repaint();

        //Bar()
    } // main to test

    /**
     * Histogram. Groups the elements of data into nBins equally spaced
     * containers and returns the number of elements in each container.
     *
     * @param data Data array
     * @param nBins Number of bins of the histogram
     * @return array[0][] = binCenters; array[1][] = binCounts.
     */
    public static Data2D hist(double[] data, int nBins) {
        if (nBins < 2) {
            System.err.println("M.hist(): minimum number of bins is 2.");
        }
        double miny = min(data);
        double maxy = max(data);
        if (miny == maxy) {
            miny = miny - Math.floor(nBins / 2) - 0.5;
            maxy = maxy + Math.ceil(nBins / 2) - 0.5;
        }
        double binwidth = (maxy - miny) / nBins;
        double[] xx = new double[nBins + 1];
        xx[0] = Double.MIN_VALUE;
        for (int i = 1; i < nBins; i++) {
            xx[i] = miny + binwidth * i;
        }
        xx[xx.length - 1] = maxy;
        return histc(data, xx);
    } // hist

    /**
     * Histogram. For vector X, counts the number of values in X that fall
     * between the elements in the binEdges vector.
     *
     * @param data
     * @param binEdges
     * @return array[0][] = binCenters; array[1][] = binCounts.
     */
    public static Data2D histc(double[] data, double[] binEdges) {
        int nPoints = data.length;
        int nBins = binEdges.length - 1;
        double[] binCenters = new double[nBins];
        double[] binFrequs = new double[nBins];
        for (int i = 0; i < nBins; i++) {
            binCenters[i] = (binEdges[i] + binEdges[i + 1]) / 2.0;
            binFrequs[i] = 0.0;
        }
        boolean test = true;
        for (int i = 0; i < nPoints; i++) {
            test = true;
            int j = 0;
            while (test) {
                if (data[i] >= binEdges[j] && data[i] < binEdges[j + 1]) {
                    binFrequs[j] += 1.0;
                    test = false;
                } else {
                    if (j == nBins - 1) {
                        test = false;
                    } else {
                        j++;
                    }
                }
            }
        }
        return new Data2D(binCenters, binFrequs);
    } // histc

    /**
     *
     * @param data
     * @param binWidth
     * @return at.ofai.music.plot.Data2D object
     */
    public static double[][] histogram(double[] data, double binWidth) {
        double dmin = min(data);
        double span = max(data) - dmin;
        double nBins = Math.ceil(span / binWidth);
        double rem = nBins * binWidth - span;
        double binZero = dmin - rem / 2.0;
        return histogram(data, binWidth, binZero);
    } // histogram

    /**
     * Code from flanagan's stat package. <br>
     *
     * @param data
     * @param binWidth
     * @param binZero
     * @return
     */
    public static double[][] histogram(double[] data,
            double binWidth, double binZero) {
        double dmax = max(data);
        int nBins = (int) Math.ceil((dmax - binZero) / binWidth);
        if (binZero + nBins * binWidth == dmax) {
            nBins++;
        }
        int nPoints = data.length;
        int[] dataCheck = new int[nPoints];
        for (int i = 0; i < nPoints; i++) {
            dataCheck[i] = 0;
        }
        double[] binWall = new double[nBins + 1];
        binWall[0] = binZero;
        for (int i = 1; i <= nBins; i++) {
            binWall[i] = binWall[i - 1] + binWidth;
        }

        double[][] binFreq = new double[2][nBins];
        for (int i = 0; i < nBins; i++) {
            binFreq[0][i] = (binWall[i] + binWall[i + 1]) / 2.0;
            binFreq[1][i] = 0.0D;
        }
        boolean test = true;

        for (int i = 0; i < nPoints; i++) {
            test = true;
            int j = 0;
            while (test) {
                if (data[i] >= binWall[j] && data[i] < binWall[j + 1]) {
                    binFreq[1][j] += 1.0;
                    dataCheck[i] = 1;
                    test = false;
                } else {
                    if (j == nBins - 1) {
                        test = false;
                    } else {
                        j++;
                    }
                }
            }
        }
        int nMissed = 0;
        for (int i = 0; i < nPoints; i++) {
            if (dataCheck[i] == 0) {
                nMissed++;
            }
        }
        outn("M.histogram(): " + nMissed + " missing.");
        return binFreq;
    } // histogramBins()

    /**
     * WG, Feb 2015. Smoothes with a Rectangular window sample-wise with a
     * growing/waning window at the beginning and end of the array.
     *
     * @param _array
     * @param _windowSize number of samples either side (e.g., windowSize = 2
     * corresponds to a total window size of 5 samples)
     * @return
     */
    public static double[] smooth(double[] _array, int _windowSize) {
        double[] in = _array;
        double[] out = new double[in.length];
        int windowSize = _windowSize;
        double sum = 0.0;
        int n = 0;
        for (int i = 0; i < windowSize; i++) {
            sum += in[i];
            n++;
        }
        for (int i = 0; i < in.length; i++) {
            if (i + windowSize < out.length) {
                sum += in[i + windowSize];
                n++;
            }
            if (i > windowSize) {
                sum -= in[i - windowSize];
                n--;
            }
            // System.out.println("Sum: " + sum + " n: " + n);
            out[i] = sum / n;
        }
        return out;
    }

    public static void out(String str) {
        System.out.print(str);
    } // out

    public static void outn(String str) {
        System.out.println(str);
    } // outn
} // M
