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

    /**
     * Calculates the distance to the specified beacon using the <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">log-distance
     * path loss model</a>.
     */
    public static float calculateDistanceTo(Beacon beacon) {
        return calculateDistanceTo(beacon, beacon.getFilteredRssi());
    }

    /**
     * Use Pythagoras with altitude to calculate more accurate distance to the beacon, if the
     * distance is larger than the altitude.
     */
    public static float calculateDistanceWithAltitudeTo(Beacon beacon, float rssi) {
        double altitude = beacon.getLocation().getAltitude();
        double distance = calculateDistanceTo(beacon, rssi);
        // distance should be double of the altitude to make pythagoras meaningful
        if (altitude > 0 && distance > (altitude * 2)) {
            double delta = Math.pow(distance, 2) - Math.pow(altitude, 2);
            return (float) Math.sqrt(delta);
        } else {
            return (float) distance;
        }
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

}
