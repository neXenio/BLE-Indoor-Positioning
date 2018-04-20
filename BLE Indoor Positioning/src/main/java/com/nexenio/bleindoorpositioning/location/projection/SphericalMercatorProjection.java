package com.nexenio.bleindoorpositioning.location.projection;

import com.nexenio.bleindoorpositioning.location.Location;

/**
 * Utility class for converting spherical coordinates and cartesian coordinates using the
 * <a href="https://en.wikipedia.org/wiki/Mercator_projection">Mercator
 * projection</a>.
 */

public class SphericalMercatorProjection {

    public static final double EARTH_RADIUS = 6378137.0; // in meters on the equator (WGS-84 semi-major axis)
    private static final double e2 = 6.6943799901377997e-3;  //WGS-84 first eccentricity squared
    private static final double a1 = 4.2697672707157535e+4;  //a1 = a*e2
    private static final double a2 = 1.8230912546075455e+9;  //a2 = a1*a1
    private static final double a3 = 1.4291722289812413e+2;  //a3 = a1*e2/2
    private static final double a4 = 4.5577281365188637e+9;  //a4 = 2.5*a2
    private static final double a5 = 4.2840589930055659e+4;  //a5 = a1+a3
    private static final double a6 = 9.9330562000986220e-1;  //a6 = 1-e2

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
     * Convert Earth-Centered-Earth-Fixed (ECEF) to lat, Lon, Altitude.
     * Input is a three element array containing x, y, z in meters.
     * Returned array contains lat and lon in Degrees, and altitude in meters.
     *
     * @see <a href="http://danceswithcode.net/engineeringnotes/geodetic_to_ecef/geodetic_to_ecef.html">Source</a>
     */
    public static Location ecefToLocation(double[] ecef) {
        double zp, w2, w, r2, r, s2, c2, s, c, ss, g, rg, rf, u, v, m, f, p, x, y, z;

        double[] geo = new double[3];
        x = ecef[0];
        y = ecef[1];
        z = ecef[2];
        zp = Math.abs(z);
        w2 = x * x + y * y;
        w = Math.sqrt(w2);
        r2 = w2 + z * z;
        r = Math.sqrt(r2);
        geo[1] = Math.atan2(y, x);       // Longitude
        s2 = z * z / r2;
        c2 = w2 / r2;
        u = a2 / r;
        v = a3 - a4 / r;
        if (c2 > 0.3) {
            s = (zp / r) * (1.0 + c2 * (a1 + u + s2 * v) / r);
            geo[0] = Math.asin(s);
            ss = s * s;
            c = Math.sqrt(1.0 - ss);
        } else {
            c = (w / r) * (1.0 - s2 * (a5 - u - c2 * v) / r);
            geo[0] = Math.acos(c);
            ss = 1.0 - c * c;
            s = Math.sqrt(ss);
        }
        g = 1.0 - e2 * ss;
        rg = EARTH_RADIUS / Math.sqrt(g);
        rf = a6 * rg;
        u = w - rg * c;
        v = zp - rf * s;
        f = c * u + s * v;
        m = c * v - s * u;
        p = m / (rf / g + f);

        geo[0] = geo[0] + p;
        geo[2] = f + m * p / 2.0;     // Elevation
        if (z < 0.0) {
            geo[0] *= -1.0;           // Latitude
        }
        return new Location(Math.toDegrees(geo[0]), Math.toDegrees(geo[1]), 0, Math.toDegrees(geo[2]));
    }

    /**
     * Converts Latitude, Longitude, Elevation to Earth-Centered-Earth-Fixed (ECEF).
     * Input is a three element array containing latitude, longitude (degrees) and elevation (m).
     * Returned array contains x, y, z in meters
     *
     * @see <a href="http://danceswithcode.net/engineeringnotes/geodetic_to_ecef/geodetic_to_ecef.html">Source</a>
     */
    public static double[] locationToEcef(Location location) {
        double[] ecef = new double[3];
        double latitude = Math.toRadians(location.getLatitude());
        double longitude = Math.toRadians(location.getLongitude());
        double elevation = Math.toRadians(location.getElevation());
        double n = EARTH_RADIUS / Math.sqrt(1 - e2 * Math.sin(latitude) * Math.sin(latitude));
        ecef[0] = (n + elevation) * Math.cos(latitude) * Math.cos(longitude);
        ecef[1] = (n + elevation) * Math.cos(latitude) * Math.sin(longitude);
        ecef[2] = (n * (1 - e2) + elevation) * Math.sin(latitude);
        return (ecef);
    }

}