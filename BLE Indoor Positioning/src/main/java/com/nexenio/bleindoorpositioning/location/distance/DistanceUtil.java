package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.location.Location;

import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 23.11.17.
 */

public abstract class DistanceUtil {

    public static final double HUMAN_WALKING_SPEED = 1.388889; // meters per second

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

    public static Location walkingSpeedFilter(Location oldLocation, Location newLocation) {
        return speedFilter(oldLocation, newLocation, HUMAN_WALKING_SPEED);
    }

    /**
     * Define a maximum movement speed to restrain a new location being further away than the
     * distance possible in that time window.
     */
    public static Location speedFilter(Location oldLocation, Location newLocation, double maximumSpeed) {
        double distance = oldLocation.getDistanceTo(newLocation);
        long timestampDelta = newLocation.getTimestamp() - oldLocation.getTimestamp();
        double currentSpeed = 0;
        if (timestampDelta != 0) {
            currentSpeed = distance / ((float) timestampDelta / TimeUnit.SECONDS.toMillis(1));
        }
        if (currentSpeed > maximumSpeed) {
            double angle = oldLocation.getAngleTo(newLocation);
            Location adjustedLocation = oldLocation.getShiftedLocation(maximumSpeed, angle);
            adjustedLocation.setTimestamp(newLocation.getTimestamp());
            return adjustedLocation;
        } else {
            return newLocation;
        }
    }
}
