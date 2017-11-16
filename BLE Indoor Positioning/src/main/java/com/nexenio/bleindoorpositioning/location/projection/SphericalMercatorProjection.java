package com.nexenio.bleindoorpositioning.location.projection;

/**
 * <a href="https://en.wikipedia.org/wiki/Mercator_projection">Mercator projection</a>
 */

public class SphericalMercatorProjection {

    public static final double EARTH_RADIUS = 6378137.0; // in meters on the equator

    /**
     * These functions take their length parameter in meters and return an angle in degrees
     */
    public static double y2lat(double aY) {
        return Math.toDegrees(Math.atan(Math.exp(aY / EARTH_RADIUS)) * 2 - Math.PI / 2);
    }

    public static double x2lon(double aX) {
        return Math.toDegrees(aX / EARTH_RADIUS);
    }

    /**
     * These functions take their angle parameter in degrees and return a length in meters
     */
    public static double lat2y(double aLat) {
        return Math.log(Math.tan(Math.PI / 4 + Math.toRadians(aLat) / 2)) * EARTH_RADIUS;
    }

    public static double lon2x(double aLong) {
        return Math.toRadians(aLong) * EARTH_RADIUS;
    }

}
