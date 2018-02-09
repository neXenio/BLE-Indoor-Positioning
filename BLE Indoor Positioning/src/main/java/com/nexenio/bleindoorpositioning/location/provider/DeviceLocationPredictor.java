package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.distance.LocationDistanceCalculator;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by leon on 29.01.18.
 * Stores window of last locations and tries to predict the next
 * location based on average distance and angle during the window
 */

public class DeviceLocationPredictor {

    private List<Location> pastDeviceLocations;
    private Location predictedLocation;
    public boolean hasPrediction;

    public DeviceLocationPredictor() {
        pastDeviceLocations = new ArrayList<>();
    }

    public void updateCurrentLocation(Location location) {
        if (!hasPrediction) {
            setPredictedLocation(location);
            hasPrediction = true;
        }
        int locationWindow = 20;
        pastDeviceLocations.add(location);
        if (pastDeviceLocations.size() >= locationWindow) {
            pastDeviceLocations.remove(0);
        }
        predictNewLocation(location);
    }

    private void predictNewLocation(Location deviceCenter) {

        Location predictedLocation;
        boolean isMoving;

        // estimate speed of movement
        double sumDistances = 0;
        if (pastDeviceLocations.size() > 1) {
            for (int i = 0; i < pastDeviceLocations.size() - 1; i++) {
                sumDistances += LocationDistanceCalculator.calculateDistanceBetweeen(
                        pastDeviceLocations.get(i).getLatitude(),
                        pastDeviceLocations.get(i).getLongitude(),
                        pastDeviceLocations.get(i + 1).getLatitude(),
                        pastDeviceLocations.get(i + 1).getLongitude());
            }
        }

        //in meter
        double meanDistance = sumDistances / pastDeviceLocations.size();

        // TODO what if no movement --> Stay-point detection
        if (meanDistance > 0.6) {
            isMoving = true;
        }

        // calculate bearing based on last two known locations
        double bearing = 0;
        if (pastDeviceLocations.size() > 1) {
            for (int i = pastDeviceLocations.size() - 1; i >= pastDeviceLocations.size() - 1; i--) {
                bearing = calculateBearing(
                        pastDeviceLocations.get(i - 1).getLatitude(),
                        pastDeviceLocations.get(i - 1).getLongitude(),
                        pastDeviceLocations.get(i).getLatitude(),
                        pastDeviceLocations.get(i).getLongitude());
            }
        }

        // TODO check  different angle calculations - getRotationAngleInDegrees & calculateBearing
        // double angle = Location.getRotationAngleInDegrees(pastDeviceLocations.get(i - 1), pastDeviceLocations.get(i));
        // System.out.println("bearing: " + bearing + " | angle:" + angle + " | diff: " + (bearing - angle));

        // TODO calculate confidence of predicting --> is it reasonable to predict new location
        // compare large window angle with small window angle

        // set initial location
        if (pastDeviceLocations.size() == 0) {
            predictedLocation = deviceCenter;
        } else {
            predictedLocation = calculateNextLocation(pastDeviceLocations.get(pastDeviceLocations.size() - 1).getLatitude(),
                    pastDeviceLocations.get(pastDeviceLocations.size() - 1).getLongitude(),
                    meanDistance / 1000, bearing);
        }
        setPredictedLocation(predictedLocation);
    }

    // TODO compare prediction with next location

    /**
     * @return the angle
     */

    static double calculateBearing(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {
        fromLatitude = Math.toRadians(fromLatitude);
        toLatitude = Math.toRadians(toLatitude);
        double longitudeDifference = Math.toRadians(toLongitude - fromLongitude);
        double y = Math.sin(longitudeDifference) * Math.cos(toLatitude);
        double x = (Math.cos(fromLatitude) * Math.sin(toLatitude)) - (Math.sin(fromLatitude) * Math.cos(toLatitude) * Math.cos(longitudeDifference));
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    /**
     * @param distance in km
     * @param bearing  in degrees
     * @return Location
     */
    private static Location calculateNextLocation(double fromLatitude, double fromLongitude, double distance, double bearing) {
        double bearingRadians = Math.toRadians(bearing);
        double latitudeRadians = Math.toRadians(fromLatitude);
        double longitudeRadians = Math.toRadians(fromLongitude);
        double newLatitude = Math.asin(Math.sin(latitudeRadians) * Math.cos(distance / LocationDistanceCalculator.EARTH_RADIUS) +
                Math.cos(latitudeRadians) * Math.sin(distance / LocationDistanceCalculator.EARTH_RADIUS) * Math.cos(bearingRadians));
        double newLongitude = longitudeRadians + Math.atan2(Math.sin(bearingRadians) * Math.sin(distance / LocationDistanceCalculator.EARTH_RADIUS) *
                Math.cos(latitudeRadians), Math.cos(distance / LocationDistanceCalculator.EARTH_RADIUS) - Math.sin(latitudeRadians) * Math.sin(newLatitude));
        return new Location(Math.toDegrees(newLatitude), Math.toDegrees(newLongitude));
    }

    /*
     Getter & Setter
     */

    public Location getPredictedLocation() {
        return predictedLocation;
    }

    private void setPredictedLocation(Location predictedLocation) {
        this.predictedLocation = predictedLocation;
    }

}
