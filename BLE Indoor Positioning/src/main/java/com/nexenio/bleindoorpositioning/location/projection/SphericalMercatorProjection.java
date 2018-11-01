package com.nexenio.bleindoorpositioning.location.projection;

import com.nexenio.bleindoorpositioning.location.Location;

/**
 * Utility class for converting spherical coordinates and cartesian coordinates using the
 * <a href="https://en.wikipedia.org/wiki/Mercator_projection">Mercator
 * projection</a>.
 */

public class SphericalMercatorProjection {

    public static final double EARTH_RADIUS = 6378137.0; // in meters on the equator (WGS-84 semi-major axis)
    private static final double E2 = 6.6943799901377997e-3;  //WGS-84 first eccentricity squared
    private static final double A1 = 4.2697672707157535e+4;  //A1 = a*E2
    private static final double A2 = 1.8230912546075455e+9;  //A2 = A1*A1
    private static final double A3 = 1.4291722289812413e+2;  //A3 = A1*E2/2
    private static final double A4 = 4.5577281365188637e+9;  //A4 = 2.5*A2
    private static final double A5 = 4.2840589930055659e+4;  //A5 = A1+A3
    private static final double A6 = 9.9330562000986220e-1;  //A6 = 1-E2

    /**
     * Caution: The conversion used by these simpler 2D methods is not compatible with the 3D
     * Earth-Centered-Earth-Fixed (ECEF) to location conversion.
     */
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

    /**
     * Convenience method to convert Earth-Centered-Earth-Fixed (ECEF) to Location. Expects input to
     * be in radians.
     */
    public static Location ecefToLocation(double[] ecef) {
        double[] geodetic = ecefToGeodetic(ecef);
        return new Location(Math.toDegrees(geodetic[0]), Math.toDegrees(geodetic[1]), 0, Math.toDegrees(geodetic[2]));
    }

    /**
     * Convert Earth-Centered-Earth-Fixed (ECEF) to latitude, longitude, elevation. Input is a three
     * element array containing x, y, z in meters. Output array contains latitude and longitude in
     * radians, and elevation in meters.
     *
     * @see <a href="http://danceswithcode.net/engineeringnotes/geodetic_to_ecef/geodetic_to_ecef.html">Source</a>
     */
    public static double[] ecefToGeodetic(double[] ecef) {
        double zp, w2, w, r2, r, s2, c2, s, c, ss, g, rg, rf, u, v, m, f, p, x, y, z;
        double[] geodetic = new double[3];
        x = ecef[0];
        y = ecef[1];
        z = ecef[2];
        zp = Math.abs(z);
        w2 = x * x + y * y;
        w = Math.sqrt(w2);
        r2 = w2 + z * z;
        r = Math.sqrt(r2);
        geodetic[1] = Math.atan2(y, x);       // Longitude
        s2 = z * z / r2;
        c2 = w2 / r2;
        u = A2 / r;
        v = A3 - A4 / r;
        if (c2 > 0.3) {
            s = (zp / r) * (1.0 + c2 * (A1 + u + s2 * v) / r);
            geodetic[0] = Math.asin(s);
            ss = s * s;
            c = Math.sqrt(1.0 - ss);
        } else {
            c = (w / r) * (1.0 - s2 * (A5 - u - c2 * v) / r);
            geodetic[0] = Math.acos(c);
            ss = 1.0 - c * c;
            s = Math.sqrt(ss);
        }
        g = 1.0 - E2 * ss;
        rg = EARTH_RADIUS / Math.sqrt(g);
        rf = A6 * rg;
        u = w - rg * c;
        v = zp - rf * s;
        f = c * u + s * v;
        m = c * v - s * u;
        p = m / (rf / g + f);

        geodetic[0] = geodetic[0] + p;
        geodetic[2] = f + m * p / 2.0;     // Elevation
        if (z < 0.0) {
            geodetic[0] *= -1.0;           // Latitude
        }
        return geodetic;
    }

    /**
     * Converts latitude, longitude, elevation to Earth-Centered-Earth-Fixed (ECEF).
     */
    public static double[] locationToEcef(Location location) {
        double[] geodetic = new double[]{Math.toRadians(location.getLatitude()), Math.toRadians(location.getLongitude()), location.getAltitude()};
        return geodeticToEcef(geodetic);
    }

    /**
     * Convert latitude, longitude, height to Earth-Centered-Earth-Fixed (ECEF). Input is a three
     * element array containing latitude, longitude (radians) and altitude (in meters). Returned
     * array contains x, y, z in meters
     *
     * @see <a href="http://danceswithcode.net/engineeringnotes/geodetic_to_ecef/geodetic_to_ecef.html">Source</a>
     */
    public static double[] geodeticToEcef(double[] geodetic) {
        double[] ecef = new double[3];
        double latitude = geodetic[0];
        double longitude = geodetic[1];
        double altitude = geodetic[2];
        double n = EARTH_RADIUS / Math.sqrt(1 - E2 * Math.sin(latitude) * Math.sin(latitude));
        ecef[0] = (n + altitude) * Math.cos(latitude) * Math.cos(longitude);
        ecef[1] = (n + altitude) * Math.cos(latitude) * Math.sin(longitude);
        ecef[2] = (n * (1 - E2) + altitude) * Math.sin(latitude);
        return ecef;
    }

}