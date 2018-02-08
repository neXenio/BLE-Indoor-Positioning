package com.nexenio.bleindoorpositioning.location.provider;


import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.distance.LocationDistanceCalculator;

import java.util.ArrayList;
import java.util.List;

// import org.testng.annotations.Test;

/**
 * Created by leon on 29.01.18.
 */

public class DeviceLocationPredictor {

    List<Location> pastDeviceLocations;
    List<Long> pastTimestamps;
    Location predictedLocation;
    public boolean hasPrediction;

    public DeviceLocationPredictor() {
        pastDeviceLocations = new ArrayList<>();
        pastTimestamps = new ArrayList<>();
    }

    public void updateCurrentLocation(Location location) {
        if (!hasPrediction) {
            setPredictedLocation(location);
            hasPrediction = true;
        }
        int locationWindow = 20;
        pastDeviceLocations.add(location);
        pastTimestamps.add(System.currentTimeMillis());
        if (pastDeviceLocations.size() >= locationWindow) {
            pastDeviceLocations.remove(0);
            pastTimestamps.remove(0);
        }
        predictNewLocation(location);
    }

    public void predictNewLocation(Location deviceCenter) {

        Location predictedLocation;
        boolean isMoving;
        // estimate speed of movement
        double sumPastLocationsLat = 0;
        double sumPastLocationsLon = 0;
        double sumDistances = 0;
        double sumWindowAngle = 0;
        if (pastDeviceLocations.size() > 1) {
            for (int i = 0; i < pastDeviceLocations.size() - 1; i++) {
                sumPastLocationsLat += pastDeviceLocations.get(i).getLatitude();
                sumPastLocationsLon += pastDeviceLocations.get(i).getLongitude();
                sumDistances += LocationDistanceCalculator.calculateDistanceBetweeen(
                        pastDeviceLocations.get(i).getLatitude(),
                        pastDeviceLocations.get(i).getLongitude(),
                        pastDeviceLocations.get(i + 1).getLatitude(),
                        pastDeviceLocations.get(i + 1).getLongitude());
                //calculate bearing between past Locations and last Location
                sumWindowAngle += calculateBearing(
                        pastDeviceLocations.get(i).getLatitude(),
                        pastDeviceLocations.get(i).getLongitude(),
                        pastDeviceLocations.get(pastDeviceLocations.size() - 1).getLatitude(),
                        pastDeviceLocations.get(pastDeviceLocations.size() - 1).getLongitude());
            }
        }

        double meanPastLocationsLat = sumPastLocationsLat / pastDeviceLocations.size();
        double meanPastLocationsLon = sumPastLocationsLon / pastDeviceLocations.size();
        double meanDistanceInMeters = LocationDistanceCalculator.calculateDistanceBetweeen(meanPastLocationsLat,
                meanPastLocationsLon, deviceCenter.getLatitude(), deviceCenter.getLongitude());
        double meanWindowAngle = sumWindowAngle / pastDeviceLocations.size();

        //in meter
        double meanDistance = sumDistances / pastDeviceLocations.size();

        //System.out.println("avg Distance: " + meanDistance);
        //System.out.println("meanLastDist: " + meanDistanceInMeters);
        //System.out.println("time diff in location update: " + (pastTimestamps.get(pastTimestamps.size() - 1) - pastTimestamps.get(0)));

        // TODO what if no movement --> Stay-point detection
        if (meanDistance > 0.6) {
            isMoving = true;
        }

        // estimate bearing based on last two known locations
        double bearing = 0;
        double distance = 0;
        double angle = 0;

        if (pastDeviceLocations.size() > 1) {
            for (int i = pastDeviceLocations.size() - 1; i >= pastDeviceLocations.size() - 1; i--) {
                bearing = calculateBearing(
                        pastDeviceLocations.get(i - 1).getLatitude(),
                        pastDeviceLocations.get(i - 1).getLongitude(),
                        pastDeviceLocations.get(i).getLatitude(),
                        pastDeviceLocations.get(i).getLongitude());
                distance = LocationDistanceCalculator.calculateDistanceBetweeen(
                        pastDeviceLocations.get(i - 1).getLatitude(),
                        pastDeviceLocations.get(i - 1).getLongitude(),
                        pastDeviceLocations.get(i).getLatitude(),
                        pastDeviceLocations.get(i).getLongitude());
                angle = Location.getRotationAngleInDegrees(pastDeviceLocations.get(i - 1), pastDeviceLocations.get(i));
            }
        }

        // TODO check  different angle calculations
        // System.out.println("bearing: " + bearing + " | angle:" + angle + " | diff: " + (bearing - angle));

        // TODO calculate confidence of predicting --> is it reasonable to predict new location
        // compare large window angle with small window angle

        //set window to start prediction
        if (pastDeviceLocations.size() == 0) {
            predictedLocation = deviceCenter;
        } else {
            predictedLocation = calculateNextLocation(pastDeviceLocations.get(pastDeviceLocations.size() - 1).getLatitude(),
                    pastDeviceLocations.get(pastDeviceLocations.size() - 1).getLongitude(),
                    meanDistance / 1000, bearing);
        }
        setPredictedLocation(predictedLocation);
    }

    /**
     * @return the angle
     */

    protected static double calculateBearing(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {
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
    protected static Location calculateNextLocation(double fromLatitude, double fromLongitude, double distance, double bearing) {
        double bearingRadians = Math.toRadians(bearing);
        double latitudeRadians = Math.toRadians(fromLatitude);
        double longitudeRadians = Math.toRadians(fromLongitude);
        double newLatitude = Math.asin(Math.sin(latitudeRadians) * Math.cos(distance / LocationDistanceCalculator.EARTH_RADIUS) +
                Math.cos(latitudeRadians) * Math.sin(distance / LocationDistanceCalculator.EARTH_RADIUS) * Math.cos(bearingRadians));
        double newLongitude = longitudeRadians + Math.atan2(Math.sin(bearingRadians) * Math.sin(distance / LocationDistanceCalculator.EARTH_RADIUS) *
                Math.cos(latitudeRadians), Math.cos(distance / LocationDistanceCalculator.EARTH_RADIUS) - Math.sin(latitudeRadians) * Math.sin(newLatitude));
        return new Location(Math.toDegrees(newLatitude), Math.toDegrees(newLongitude));
    }


    // TODO compare prediction with next location


    /*
     Getter & Setter
     */

    public Location getPredictedLocation() {
        return predictedLocation;
    }

    public void setPredictedLocation(Location predictedLocation) {
        this.predictedLocation = predictedLocation;
    }

}
