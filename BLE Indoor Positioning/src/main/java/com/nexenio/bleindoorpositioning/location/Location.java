package com.nexenio.bleindoorpositioning.location;

import com.nexenio.bleindoorpositioning.location.distance.LocationDistanceCalculator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/*
    decimal
    places   degrees          distance
    -------  -------          --------
    0        1                111  km
    1        0.1              11.1 km
    2        0.01             1.11 km
    3        0.001            111  m
    4        0.0001           11.1 m
    5        0.00001          1.11 m
    6        0.000001         11.1 cm
    7        0.0000001        1.11 cm
    8        0.00000001       1.11 mm
 */

/**
 * Created by steppschuh on 15.11.17.
 */

public class Location {

    public static final double VALUE_NOT_SET = 0;

    private double latitude = VALUE_NOT_SET;
    private double longitude = VALUE_NOT_SET;

    /**
     * The altitude describes the distance to the sea level in meters.
     */
    private double altitude = VALUE_NOT_SET;

    /**
     * The elevation describes the relative height to the floor in meters.
     */
    private double elevation = VALUE_NOT_SET;

    /**
     * The estimated accuracy of the location in meters (imagine a circle with this radius around
     * the location).
     */
    private double accuracy = VALUE_NOT_SET;

    private long timestamp;

    public Location() {
        this.timestamp = System.currentTimeMillis();
    }

    public Location(double latitude, double longitude) {
        this();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(double latitude, double longitude, double altitude) {
        this(latitude, longitude);
        this.altitude = altitude;
    }

    public Location(double latitude, double longitude, double altitude, double elevation) {
        this(latitude, longitude);
        this.altitude = altitude;
        this.elevation = elevation;
    }

    public Location(Location location) {
        this(location.latitude, location.longitude, location.altitude, location.elevation);
        this.accuracy = location.accuracy;
        this.timestamp = location.timestamp;
    }

    /**
     * Calculates the distance between the current and the specified location in meters. Elevation /
     * altitude will be ignored.
     *
     * @return distance in meters
     */
    public double getDistanceTo(Location location) {
        return LocationDistanceCalculator.calculateDistanceBetween(this, location);
    }

    public double getAngleTo(Location location) {
        return getRotationAngleInDegrees(this, location);
    }

    /**
     * Shifts the current {@link #latitude} and {@link #longitude} based on the specified distance
     * and angle.
     *
     * @param distance in meters
     * @param angle    in degrees [0°-360°)
     * @see <a href="https://github.com/googlemaps/android-maps-utils/blob/master/library/src/com/google/maps/android/SphericalUtil.java">Java
     * Example</a>
     * @see <a href="http://mathworld.wolfram.com/GreatCircle.html">Great Circel Wolfram Alpha</a>
     * @see <a href="https://en.wikipedia.org/wiki/Great-circle_navigation#Finding_way-points">Great
     * Circle Wikipedia</a>
     */
    public void shift(double distance, double angle) {
        angle = angle % 360;
        double bearingRadians = Math.toRadians(angle);
        double latitudeRadians = Math.toRadians(latitude);
        double longitudeRadians = Math.toRadians(longitude);
        // convert distance to km and calculate fraction of earth radius
        double distanceFraction = (distance / 1000) / LocationDistanceCalculator.EARTH_RADIUS;
        double shiftedLatitudeRadians = Math.asin(Math.sin(latitudeRadians) * Math.cos(distanceFraction) +
                Math.cos(latitudeRadians) * Math.sin(distanceFraction) * Math.cos(bearingRadians));
        double shiftedLongitudeRadians = longitudeRadians + Math.atan2(Math.sin(bearingRadians) * Math.sin(distanceFraction) *
                Math.cos(latitudeRadians), Math.cos(distanceFraction) - Math.sin(latitudeRadians) * Math.sin(shiftedLatitudeRadians));

        latitude = Math.toDegrees(shiftedLatitudeRadians);
        longitude = Math.toDegrees(shiftedLongitudeRadians);
        timestamp = System.currentTimeMillis();
    }

    /**
     * Creates a copy of the current instance and calls {@link #shift(double, double)} on that
     * copy.
     *
     * @param distance in meters
     * @param angle    in degrees (0°-360°)
     */
    public Location getShiftedLocation(double distance, double angle) {
        Location shiftedLocation = new Location(this);
        shiftedLocation.shift(distance, angle);
        return shiftedLocation;
    }

    public boolean latitudeAndLongitudeEquals(Location location) {
        return latitudeAndLongitudeEquals(location, 0);
    }

    public boolean latitudeAndLongitudeEquals(Location location, double delta) {
        return (Math.abs(latitude - location.latitude) <= delta) && (Math.abs(longitude - location.longitude) <= delta);
    }

    public boolean hasLatitudeAndLongitude() {
        return latitude != VALUE_NOT_SET && !Double.isNaN(latitude) && longitude != VALUE_NOT_SET && !Double.isNaN(longitude);
    }

    public boolean hasAltitude() {
        return altitude != VALUE_NOT_SET && !Double.isNaN(altitude);
    }

    public boolean hasElevation() {
        return elevation != VALUE_NOT_SET && !Double.isNaN(elevation);
    }

    public boolean hasAccuracy() {
        return accuracy != VALUE_NOT_SET && !Double.isNaN(accuracy);
    }

    public URI generateGoogleMapsUri() {
        try {
            return new URI("https://www.google.com/maps/search/?api=1&query=" +
                    String.valueOf(latitude) + "," + String.valueOf(longitude));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unable to generate Google Maps URI", e);
        }
    }

    @Override
    public String toString() {
        if (!hasLatitudeAndLongitude()) {
            return "Empty location";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Latitude: ").append(latitude);
        sb.append(" Longitude: ").append(longitude);
        if (hasAltitude()) {
            sb.append(" Altitude: ").append(String.format(Locale.US, "%.2f", altitude)).append("m");
        }
        if (hasElevation()) {
            sb.append(" Elevation: ").append(String.format(Locale.US, "%.2f", elevation)).append("m");
        }
        if (hasAccuracy()) {
            sb.append(" Accuracy: ").append(String.format(Locale.US, "%.2f", accuracy)).append("m");
        }
        return sb.toString();
    }

    /**
     * Calculates the angle between two locations in degrees. The result ranges from [0,360),
     * rotating CLOCKWISE, 0 and 360 degrees represents NORTH, 90 degrees represents EAST. This is
     * also referred to as bearing.
     *
     * Calculation was derived from this <a href="http://www.igismap.com/formula-to-find-bearing-or-heading-angle-between-two-points-latitude-longitude/">
     * Bearing Calculation formula.</a>
     *
     * @param centerLocation Location we are rotating around.
     * @param targetLocation Location we want to calculate the angle to.
     * @return angle in degrees
     */
    public static double getRotationAngleInDegrees(Location centerLocation, Location targetLocation) {
        double longitudeDelta = Math.toRadians(targetLocation.longitude - centerLocation.longitude);
        double centerLocationLatitude = Math.toRadians(centerLocation.latitude);
        double targetLocationLatitude = Math.toRadians(targetLocation.latitude);

        double x = (Math.cos(centerLocationLatitude) * Math.sin(targetLocationLatitude))
                - (Math.sin(centerLocationLatitude) * Math.cos(targetLocationLatitude) * Math.cos(longitudeDelta));
        double y = Math.sin(longitudeDelta) * Math.cos(targetLocationLatitude);

        double angle = Math.toDegrees(Math.atan2(y, x));

        // convert the interval (-180, 180] to [0, 360)
        if (angle < 0) {
            angle += 360;
        }

        return angle; // note that the angle can be '-0.0'
    }

    /*
        Getter & Setter
     */

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        this.timestamp = System.currentTimeMillis();
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        this.timestamp = System.currentTimeMillis();
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}
