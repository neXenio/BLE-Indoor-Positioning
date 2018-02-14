package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.angle.AngleUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by leon on 29.01.18.
 * Stores window of last locations and tries to predict the next
 * location based on average distance and angle during the window
 */

public class DeviceLocationPredictor {

    private List<Location> deviceLocations;
    private static Location predictedLocation;
    private static boolean hasPrediction;

    public DeviceLocationPredictor() {
        deviceLocations = new ArrayList<>();
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

        if (deviceLocations.size() > locationWindow) {
            deviceLocations.remove(0);
        }
        predictNewLocation(location);
    }

    private void predictNewLocation(Location deviceCenter) {
        Location predictedLocation;
        boolean isMoving;
        double sumDistances = 0;
        double[] angles = new double[deviceLocations.size()];
        float metersPerSecondSum = 0;
        if (deviceLocations.size() > 1) {
            for (int i = 0; i < deviceLocations.size() - 1; i++) {
                double distance = deviceLocations.get(i).getDistanceTo(deviceLocations.get(i + 1));
                sumDistances += distance;
                angles[i] = deviceLocations.get(i).getAngleTo(deviceLocations.get(i + 1));
                // can't use TimeUnit.MILLISECONDS.toSeconds because float is required for fractions of a second
                float timeDifferenceInSeconds = (deviceLocations.get(i + 1).getLastChangeTimestamp() -
                        deviceLocations.get(i).getLastChangeTimestamp()) / (float) 1000;
                metersPerSecondSum += distance / timeDifferenceInSeconds;
            }
        }

        // in meter
        double meanDistance = sumDistances / deviceLocations.size();
        double meanAngle = AngleUtil.calculateAngleMean(angles);

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
            predictedLocation = deviceCenter.getShiftedLocation(metersPerSecond, meanAngle);
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
