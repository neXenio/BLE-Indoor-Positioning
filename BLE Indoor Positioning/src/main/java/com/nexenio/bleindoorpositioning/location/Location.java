package com.nexenio.bleindoorpositioning.location;

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
