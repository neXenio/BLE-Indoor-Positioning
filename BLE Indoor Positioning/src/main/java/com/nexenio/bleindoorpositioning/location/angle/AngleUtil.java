package com.nexenio.bleindoorpositioning.location.angle;

import com.nexenio.bleindoorpositioning.location.Location;

import java.util.List;

/**
 * Created by leon on 14.02.18.
 */

public final class AngleUtil {

    /**
     * Calculates the mean by converting all angles to corresponding points on the unit circle (i.e.
     * alpha to (cos(alpha), sin(alpha))).
     *
     * Caution: circular mean is NOT the arithmetic mean Example: the arithmetic mean of the three
     * angles 0°, 0° and 90° is (0+0+90)/3 = 30°, but the vector mean is 26.565°
     *
     * @see <a href="https://en.wikipedia.org/wiki/Mean_of_circular_quantities">Circle Mean</a>
     */
    public static double calculateMeanAngle(double[] angles) {
        if (angles == null || angles.length == 0) {
            return 0;
        }
        if (angles.length == 1) {
            return angles[0];
        }
        float sumSin = 0;
        float sumCos = 0;
        for (double angle : angles) {
            sumSin += Math.sin(Math.toRadians(angle));
            sumCos += Math.cos(Math.toRadians(angle));
        }
        return Math.toDegrees(Math.atan2(sumSin, sumCos));
    }

    /**
     * Calculates the mean by converting all angles to corresponding points on the unit circle (i.e.
     * alpha to (cos(alpha), sin(alpha))).
     *
     * @see <a href="https://en.wikipedia.org/wiki/Mean_of_circular_quantities">Circle Mean</a>
     */
    public static double calculateMeanAngle(List<Location> deviceLocations) {
        double[] angles = new double[deviceLocations.size()];
        for (int i = 0; i < deviceLocations.size() - 1; i++) {
            angles[i] = deviceLocations.get(i).getAngleTo(deviceLocations.get(i + 1));
        }
        return calculateMeanAngle(angles);
    }

    /**
     * Length (angular) of a shortest way between two angles. It will be in range [0, 180].
     *
     * @see <a href="https://stackoverflow.com/questions/7570808/how-do-i-calculate-the-difference-of-two-angle-measures">Stackoverflow</a>
     */
    public static double angleDistance(double alpha, double beta) {
        double phi = Math.abs(beta - alpha) % 360; // This is either the distance or 360 - distance
        return phi > 180 ? 360 - phi : phi;
    }

}
