package com.nexenio.bleindoorpositioning.location.projection;

/**
 * Utility class for converting spherical coordinates and cartesian coordinates using the
 * <a href="https://en.wikipedia.org/wiki/Mercator_projection">Mercator
 * projection</a>.
 */

public class SphericalMercatorProjection {

    public static final double EARTH_RADIUS = 6378137.0; // in meters on the equator

    public static double yToLatitude(double y) {
        return Math.toDegrees(Math.atan(Math.exp(y / EARTH_RADIUS)) * 2 - Math.PI / 2);
    }

    public static double xToLongitude(double x) {
        return Math.toDegrees(x / EARTH_RADIUS);
    }

    public static double latitudeToY(double latitude) {
        return Math.log(Math.tan(Math.PI / 4 + Math.toRadians(latitude) / 2)) * EARTH_RADIUS;
    }

    public static double longitudeToX(double longitude) {
        return Math.toRadians(longitude) * EARTH_RADIUS;
    }

}
