package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.angle.AngleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Stores window of last locations and tries to predict the next
 * location based on average distance and angle during the window
 *
 * Created by leon on 29.01.18.
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
        int locationWindow = 10;
        deviceLocations.add(location);
        if (deviceLocations.size() > locationWindow) {
            deviceLocations.remove(0);
        }
        predictNextLocation(deviceLocations);
    }

    // TODO use acceleration data
    // TODO what if no movement --> Stay-point detection
    // TODO compare prediction with next location (confidence of prediction) --> is it reasonable to predict new location

    private void predictNextLocation(List<Location> deviceLocations) {
        setPredictedLocation(predictLocationFromLocations(deviceLocations));
    }

    public static Location predictLocationFromLocations(List<Location> deviceLocations) {
        Location predictedLocation;
        Location lastLocation = deviceLocations.get(deviceLocations.size() - 1);
        double meanAngle = calculateMeanAngleFromLocations(deviceLocations);
        float metersPerSecond = calculateMovementSpeedFromLocations(deviceLocations);
        if (deviceLocations.size() == 0) {
            predictedLocation = lastLocation;
        } else {
            predictedLocation = lastLocation.getShiftedLocation(metersPerSecond, meanAngle);
        }
        return predictedLocation;
    }

    public static double calculateMeanAngleFromLocations(List<Location> deviceLocations) {
        double[] angles = new double[deviceLocations.size()];
        if (deviceLocations.size() > 1) {
            for (int i = 0; i < deviceLocations.size() - 1; i++) {
                angles[i] = deviceLocations.get(i).getAngleTo(deviceLocations.get(i + 1));
            }
            return AngleUtil.calculateAngleMean(angles);
        } else {
            return 0;
        }
    }

    public static float calculateMovementSpeedFromLocations(List<Location> deviceLocations) {
        if (deviceLocations.size() > 1) {
            float metersPerSecondSum = 0;
            for (int i = 0; i < deviceLocations.size() - 1; i++) {
                double distance = deviceLocations.get(i).getDistanceTo(deviceLocations.get(i + 1));
                long delta = deviceLocations.get(i + 1).getLastChangeTimestamp() - deviceLocations.get(i).getLastChangeTimestamp();
                //TODO catch delta of 0
                metersPerSecondSum += distance / ((float) delta / TimeUnit.SECONDS.toMillis(1));
            }
            return (metersPerSecondSum / deviceLocations.size());
        } else {
            return 0;
        }
    }

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
