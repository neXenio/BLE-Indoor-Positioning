package com.nexenio.bleindoorpositioning.location;

import com.nexenio.bleindoorpositioning.location.distance.LocationDistanceCalculator;

import java.net.URI;
import java.net.URISyntaxException;

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

    public static double VALUE_NOT_SET = 0;

    private double latitude = VALUE_NOT_SET;
    private double longitude = VALUE_NOT_SET;
    private double altitude = VALUE_NOT_SET;
    private long lastChangeTimestamp;

    public Location() {
        this.lastChangeTimestamp = System.currentTimeMillis();
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

    public Location(Location location) {
        this();
        this.latitude = location.latitude;
        this.longitude = location.longitude;
        this.altitude = location.altitude;
    }

    /**
     * Calculates the distance between the current and the specified location in meters.
     * Elevation / altitude will be ignored.
     *
     * @return distance in meters
     */
    public double getDistanceTo(Location location) {
        return LocationDistanceCalculator.calculateDistanceBetween(this, location, false);
    }

    public double getAngleTo(Location location) {
        return getRotationAngleInDegrees(this, location);
    }

    public Location getShiftedLocation(double distance, double angle) {
        return calculateNextLocation(this, distance, angle, 0);
    }

    public Location getShiftedLocation(double distance, double angle, double altitude) {
        return calculateNextLocation(this, distance, angle, altitude);
    }

    public boolean latitudeAndLongitudeEquals(Location location) {
        return latitude == location.latitude && longitude == location.longitude;
    }

    public boolean hasLatitudeAndLongitude() {
        return latitude != VALUE_NOT_SET && !Double.isNaN(latitude) && longitude != VALUE_NOT_SET && !Double.isNaN(longitude);
    }

    public boolean hasAltitude() {
        return altitude != VALUE_NOT_SET && !Double.isNaN(altitude);
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
        sb.append("Latitude: ").append(latitude).append(" ");
        sb.append("Longitude: ").append(longitude).append(" ");
        if (hasAltitude()) {
            sb.append("Altitude: ").append(altitude).append(" ");
        }
        return sb.toString();
    }

    /**
     * Calculates the angle between two locations in degrees.
     * The result ranges from [0,360), rotating CLOCKWISE,
     * 0 and 360 degrees represents NORTH, 90 degrees represents EAST.
     * This is also referred to as bearing.
     *
     * @param centerLocation Location we are rotating around.
     * @param targetLocation Location we want to calculate the angle to.
     * @return angle in degrees
     */
    public static double getRotationAngleInDegrees(Location centerLocation, Location targetLocation) {
        double longitudeDelta = targetLocation.longitude - centerLocation.longitude;
        double x = (Math.cos(centerLocation.latitude) * Math.sin(targetLocation.latitude))
                - (Math.sin(centerLocation.latitude) * Math.cos(targetLocation.latitude) * Math.cos(longitudeDelta));
        double y = Math.sin(longitudeDelta) * Math.cos(targetLocation.latitude);
        double angle = Math.toDegrees(Math.atan2(y, x));
        return 360 - ((angle + 360) % 360);
    }

    // TODO calculate angle with altitudes https://stackoverflow.com/questions/41542465/calculate-vertical-bearing-between-two-gps-coordinates-with-altitudes

    /**
     * Calculates new location based on current location, distance and angle.
     *
     * @param distance in m
     * @param angle    in degrees
     * @param altitude in m
     * @return Location
     */
    public static Location calculateNextLocation(Location location, double distance, double angle, double altitude) {
        double bearingRadians = Math.toRadians(angle);
        double latitudeRadians = Math.toRadians(location.latitude);
        double longitudeRadians = Math.toRadians(location.longitude);
        double distanceFraction = (distance / 1000) / LocationDistanceCalculator.EARTH_RADIUS;
        double newLatitude = Math.asin(Math.sin(latitudeRadians) * Math.cos(distanceFraction) +
                Math.cos(latitudeRadians) * Math.sin(distanceFraction) * Math.cos(bearingRadians));
        double newLongitude = longitudeRadians + Math.atan2(Math.sin(bearingRadians) * Math.sin(distanceFraction) *
                Math.cos(latitudeRadians), Math.cos(distanceFraction) - Math.sin(latitudeRadians) * Math.sin(newLatitude));
        // TODO missing altitude calculation
        if (altitude == 0) {
            return new Location(Math.toDegrees(newLatitude), Math.toDegrees(newLongitude));
        } else {
            return new Location(Math.toDegrees(newLatitude), Math.toDegrees(newLongitude), altitude);
        }
    }

    /*
        Getter & Setter
     */

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        this.lastChangeTimestamp = System.currentTimeMillis();
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        this.lastChangeTimestamp = System.currentTimeMillis();
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public long getLastChangeTimestamp() {
        return lastChangeTimestamp;
    }

    public void setLastChangeTimestamp(long lastChangeTimestamp) {
        this.lastChangeTimestamp = lastChangeTimestamp;
    }
}
