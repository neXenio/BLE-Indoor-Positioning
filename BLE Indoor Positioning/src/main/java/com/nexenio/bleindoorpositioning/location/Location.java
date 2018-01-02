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

    public Location() {
    }

    public Location(double latitude, double longitude) {
        this();
        this.latitude = latitude;
        this.longitude = longitude;
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
     * Calculates the angle from centerPt to targetPt in degrees.
     * The return should range from [0,360), rotating CLOCKWISE,
     * 0 and 360 degrees represents NORTH, 90 degrees represents EAST.
     *
     * @param centerLocation Location we are rotating around.
     * @param targetLocation Location we want to calculate the angle to.
     * @return angle in degrees. This is the angle from centerLocation to targetLocation.
     */
    public static double getRotationAngleInDegrees(Location centerLocation, Location targetLocation) {
        double longitudeDelta = targetLocation.longitude - centerLocation.longitude;
        double x = (Math.cos(centerLocation.latitude) * Math.sin(targetLocation.latitude))
                - (Math.sin(centerLocation.latitude) * Math.cos(targetLocation.latitude) * Math.cos(longitudeDelta));
        double y = Math.sin(longitudeDelta) * Math.cos(targetLocation.latitude);
        double angle = Math.toDegrees(Math.atan2(y, x));
        return 360 - ((angle + 360) % 360);
    }

    /*
        Getter & Setter
     */

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
