package com.nexenio.bleindoorpositioning.location;

import com.nexenio.bleindoorpositioning.location.distance.LocationDistanceCalculator;

import java.net.URI;
import java.net.URISyntaxException;

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

    public boolean latitudeAndLongitudeEquals(Location location) {
        return latitude == location.latitude && longitude == location.longitude;
    }

    public boolean hasLatitudeAndLongitude() {
        return latitude != VALUE_NOT_SET && longitude != VALUE_NOT_SET;
    }

    public boolean hasAltitude() {
        return altitude != VALUE_NOT_SET;
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
