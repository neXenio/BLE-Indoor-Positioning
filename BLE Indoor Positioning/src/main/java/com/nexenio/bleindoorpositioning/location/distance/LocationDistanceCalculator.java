package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.location.Location;

/**
 * Created by steppschuh on 17.11.17.
 */

public abstract class LocationDistanceCalculator {

    public static final int EARTH_RADIUS = 6371; // in km

    public static double calculateDistanceBetween(Location fromLocation, Location toLocation) {
        return calculateDistanceBetween(fromLocation, toLocation, false);
    }

    public static double calculateDistanceBetween(Location fromLocation, Location toLocation, boolean includeElevation) {
        if (includeElevation) {
            return calculateDistanceBetween(
                    fromLocation.getLatitude(), fromLocation.getLongitude(), fromLocation.getAltitude(),
                    toLocation.getLatitude(), toLocation.getLongitude(), toLocation.getAltitude()
            );
        } else {
            return calculateDistanceBetween(
                    fromLocation.getLatitude(), fromLocation.getLongitude(), 0,
                    toLocation.getLatitude(), toLocation.getLongitude(), 0
            );
        }
    }

    /**
     * Calculates the distance between two points in latitude and longitude taking
     * the elevation delta into account. Uses the Haversine method as its base.
     *
     * @return Distance in Meters
     * @see <a href="https://stackoverflow.com/a/16794680/1188330">StackOverflow</a>
     */
    public static double calculateDistanceBetween(double fromLatitude, double fromLongitude, double fromElevation,
                                                  double toLatitude, double toLongitude, double toElevation) {
        double latDistance = Math.toRadians(toLatitude - fromLatitude);
        double lonDistance = Math.toRadians(toLongitude - fromLongitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(fromLatitude)) * Math.cos(Math.toRadians(toLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c * 1000; // convert to meters
        double height = fromElevation - toElevation;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }

    /**
     * Convenience distance calculation without elevation
     */
    public static double calculateDistanceBetweeen(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {
        return calculateDistanceBetween(fromLatitude, fromLongitude, 0, toLatitude, toLongitude, 0);
    }

}
