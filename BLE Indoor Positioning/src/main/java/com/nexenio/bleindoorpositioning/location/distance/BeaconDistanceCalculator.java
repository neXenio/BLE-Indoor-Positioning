package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.Eddystone;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;

/**
 * Created by steppschuh on 22.11.17.
 */

public abstract class BeaconDistanceCalculator {

    public static final float PATH_LOSS_PARAMETER_OPEN_SPACE = 2;
    public static final float PATH_LOSS_PARAMETER_INDOOR = 1.7f;

    public static final int SIGNAL_LOSS_AT_ONE_METER = -41;

    public static final int EARTH_RADIUS = 6378;

    /**
     * Calculates the distance to the specified beacon using the <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">log-distance
     * path loss model</a>.
     */
    public static float calculateDistanceTo(Beacon beacon) {
        return calculateDistanceTo(beacon, beacon.getRssi());
    }

    /**
     * Calculates the distance to the specified beacon using the <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">log-distance
     * path loss model</a>.
     */
    public static float calculateDistanceTo(Beacon beacon, float rssi) {
        return calculateDistance(rssi, beacon.getCalibratedRssi(), beacon.getCalibratedDistance(), PATH_LOSS_PARAMETER_INDOOR);
    }

    /**
     * Calculates distances using the <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">log-distance
     * path loss model</a>.
     *
     * @param rssi               the currently measured RSSI
     * @param calibratedRssi     the RSSI measured at the calibration distance
     * @param calibratedDistance the distance in meters at which the calibrated RSSI was measured
     * @param pathLossParameter  the path-loss adjustment parameter
     */
    public static float calculateDistance(float rssi, float calibratedRssi, int calibratedDistance, float pathLossParameter) {
        float calibratedRssiAtOneMeter;
        if (calibratedDistance == IBeacon.CALIBRATION_DISTANCE_DEFAULT) {
            calibratedRssiAtOneMeter = calibratedRssi;
        } else if (calibratedDistance == Eddystone.CALIBRATION_DISTANCE_DEFAULT) {
            calibratedRssiAtOneMeter = calibratedRssi + SIGNAL_LOSS_AT_ONE_METER;
        } else {
            calibratedRssiAtOneMeter = -62;
        }
        return calculateDistance(rssi, calibratedRssiAtOneMeter, pathLossParameter);
    }

    /**
     * Calculates distances using the <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">log-distance
     * path loss model</a>.
     *
     * @param rssi              the currently measured RSSI
     * @param calibratedRssi    the RSSI measured at 1m distance
     * @param pathLossParameter the path-loss adjustment parameter
     */
    public static float calculateDistance(float rssi, float calibratedRssi, float pathLossParameter) {
        return (float) Math.pow(10, (calibratedRssi - rssi) / (10 * pathLossParameter));
    }

    public static double calculateCartesianDistance(double latA, double lonA, double altA, double latB, double lonB, double altB) {
        double latDistance = Math.toRadians(latB - latA);
        double lonDistance = Math.toRadians(lonB - lonA);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latA)) * Math.cos(Math.toRadians(latB))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c * 1000; // convert to meters

        double height = altA - altB;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public static double calculateCartesianDistance(double latA, double lonA, double latB, double lonB) {
        return calculateCartesianDistance(latA,lonA,0,latB,lonB,0);
    }

}
