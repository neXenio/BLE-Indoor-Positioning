package com.nexenio.bleindoorpositioning.location;

import com.nexenio.bleindoorpositioning.location.distance.DistanceCalculator;

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
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Calculates the distance between the current and the specified location in meters.
     * Elevation / altitude will be ignored.
     *
     * @return distance in meters
     */
    public double getDistanceTo(Location location) {
        return DistanceCalculator.getDistanceBetween(this, location, false);
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
