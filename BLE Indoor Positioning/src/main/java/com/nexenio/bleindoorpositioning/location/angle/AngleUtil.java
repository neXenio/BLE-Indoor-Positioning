package com.nexenio.bleindoorpositioning.location.angle;

/**
 * Created by leon on 14.02.18.
 */

public class AngleUtil {

    /**
     * A simple arithmetic mean is not appropriate for angles. Calculates mean by converting all
     * angles to corresponding point on the unit circle i.e. alpha to (cos(alpha),sin(alpha))
     * <a href="https://en.wikipedia.org/wiki/Mean_of_circular_quantities">Circle Mean</a>
     */
    public static double calculateAngleMean(double[] angles) {
        float sumSin = 0;
        float sumCos = 0;
        for (int i = 0; i < angles.length; i++) {
            sumSin += Math.sin(angles[i]);
            sumCos += Math.cos(angles[i]);
        }
        return Math.atan2(sumSin, sumCos);
    }
}
