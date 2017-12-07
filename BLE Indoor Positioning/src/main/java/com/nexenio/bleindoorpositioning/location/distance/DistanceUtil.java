package com.nexenio.bleindoorpositioning.location.distance;

/**
 * Created by steppschuh on 23.11.17.
 */

public abstract class DistanceUtil {

    public static long getClosestEvenDistance(double distance, int evenIncrement) {
        return Math.round(distance / evenIncrement) * evenIncrement;
    }

    public static long getReasonableSmallerEvenDistance(double distance) {
        return getClosestEvenDistance(distance, getMaximumEvenIncrement(distance));
    }

    public static int getMaximumEvenIncrement(double distance) {
        if (distance < 10) {
            return 1;
        }
        return (int) Math.pow(10, Math.floor(Math.log10(distance)));
    }

}
