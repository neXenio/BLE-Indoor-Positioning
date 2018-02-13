package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.distance.LocationDistanceCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by leon on 29.01.18.
 * Stores window of last locations and tries to predict the next
 * location based on average distance and angle during the window
 */

public class DeviceLocationPredictor {

    private List<Location> deviceLocations;
    private List<Long> deviceLocationsTimestamps;
    private static Location predictedLocation;
    private static boolean hasPrediction;

    public DeviceLocationPredictor() {
        deviceLocations = new ArrayList<>();
        deviceLocationsTimestamps = new ArrayList<>();
    }

    public static boolean hasPrediction() {
        return hasPrediction;
    }

    public void updateCurrentLocation(Location location) {
        if (!hasPrediction) {
            setPredictedLocation(location);
            hasPrediction = true;
        }
        int locationWindow = 20;
        deviceLocations.add(location);
        deviceLocationsTimestamps.add(System.currentTimeMillis());
        if (deviceLocations.size() > locationWindow) {
            deviceLocations.remove(0);
            deviceLocationsTimestamps.remove(0);
        }
        predictNewLocation(location);
    }

    private void predictNewLocation(Location deviceCenter) {
        Location predictedLocation;
        boolean isMoving;
        double sumDistances = 0;
        double sumAngle = 0;
        float metersPerSecondSum = 0;
        if (deviceLocations.size() > 1) {
            for (int i = 0; i < deviceLocations.size() - 1; i++) {
                double distance = LocationDistanceCalculator.calculateDistanceBetweeen(
                        deviceLocations.get(i).getLatitude(),
                        deviceLocations.get(i).getLongitude(),
                        deviceLocations.get(i + 1).getLatitude(),
                        deviceLocations.get(i + 1).getLongitude());
                sumDistances += distance;
                sumAngle += Location.getRotationAngleInDegrees(
                        deviceLocations.get(i),
                        deviceLocations.get(i + 1));
                float timeDifferenceInSeconds = (deviceLocationsTimestamps.get(i + 1) -
                        deviceLocationsTimestamps.get(i)) / (float) TimeUnit.SECONDS.toMillis(1);
                metersPerSecondSum += distance / timeDifferenceInSeconds;
            }
        }

        // in meter
        double meanDistance = sumDistances / deviceLocations.size();
        double meanAngle = sumAngle / deviceLocations.size();

        System.out.println("meanAngle: " + meanAngle);

        // estimate speed of movement
        // TODO also use acceleration
        float metersPerSecond = 0;
        if (deviceLocations.size() > 1) {
            metersPerSecond = (metersPerSecondSum / deviceLocations.size());
        }

        // TODO what if no movement --> Stay-point detection
        if (meanDistance > 0.6) {
            isMoving = true;
        }

        // set initial location
        if (deviceLocations.size() == 0) {
            predictedLocation = deviceCenter;
        } else {
            predictedLocation = deviceCenter.calculateNextLocation(metersPerSecond / 1000, meanAngle);
        }
        setPredictedLocation(predictedLocation);
    }

    // TODO compare prediction with next location (confidence of prediction) --> is it reasonable to predict new location

    /*
     Getter & Setter
     */

    public static Location getPredictedLocation() {
        return predictedLocation;
    }

    private void setPredictedLocation(Location predictedLocation) {
        this.predictedLocation = predictedLocation;
    }

}
