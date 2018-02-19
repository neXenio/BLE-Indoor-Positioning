package com.nexenio.bleindoorpositioning.location.angle;

import com.nexenio.bleindoorpositioning.location.Location;

import java.util.List;

/**
 * Created by leon on 14.02.18.
 */

public final class AngleUtil {

    /**
     * Calculates the mean by converting all angles to corresponding points on the
     * unit circle (i.e. alpha to (cos(alpha), sin(alpha))).
     *
     * @see <a href="https://en.wikipedia.org/wiki/Mean_of_circular_quantities">Circle Mean</a>
     */
    public static double calculateMeanAngle(double[] angles) {
        if (angles == null) {
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
     * Calculates the mean by converting all angles to corresponding points on the
     * unit circle (i.e. alpha to (cos(alpha), sin(alpha))).
     *
     * @see <a href="https://en.wikipedia.org/wiki/Mean_of_circular_quantities">Circle Mean</a>
     */
    public static double calculateMeanAngle(List<Location> deviceLocations) {
        if (deviceLocations.isEmpty()) {
            return 0;
        }
        double[] angles = new double[deviceLocations.size()];
        for (int i = 0; i < deviceLocations.size() - 1; i++) {
            angles[i] = deviceLocations.get(i).getAngleTo(deviceLocations.get(i + 1));
        }
        return calculateMeanAngle(angles);
    }

}
