package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.Eddystone;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;

/**
 * Created by steppschuh on 22.11.17.
 */

public abstract class BeaconDistanceCalculator {

    /**
     * Different Path Loss Exponent parameters for different environments.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model"></a>
     */
    public static final float PATH_LOSS_PARAMETER_OPEN_SPACE = 2;
    public static final float PATH_LOSS_PARAMETER_INDOOR = 1.7f;
    public static final float PATH_LOSS_PARAMETER_OFFICE_HARD_PARTITION = 3f;

    public static final int CALIBRATED_RSSI_AT_ONE_METER = -62;
    public static final int SIGNAL_LOSS_AT_ONE_METER = -41;

    private static float pathLossParameter = PATH_LOSS_PARAMETER_OFFICE_HARD_PARTITION;

    /**
     * Calculates the distance to the specified beacon using the <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">log-distance
     * path loss model</a>.
     */
    public static float calculateDistanceTo(Beacon beacon) {
        return calculateDistanceTo(beacon, beacon.getFilteredRssi());
    }

    /**
     * Use this method to remove the elevation delta from the distance between device and beacon.
     * Calculation based on Pythagoras to calculate distance on the floor (2D) to the beacon, if the
     * distance is double the elevation delta. The elevation expected refers to the distance above
     * the floor ground, rather than the altitude above sea level.
     */
    public static float calculateDistanceWithoutElevationDeltaToDevice(Beacon beacon, float rssi, double deviceElevation) {
        float distance = calculateDistanceTo(beacon, rssi);
        if (beacon.hasLocation() && beacon.getLocation().hasElevation()) {
            double elevationDelta = Math.abs(beacon.getLocation().getElevation() - deviceElevation);
            // distance should be double of the elevationDelta to make pythagoras meaningful
            if (elevationDelta > 0 && distance > (elevationDelta * 2)) {
                double delta = Math.pow(distance, 2) - Math.pow(elevationDelta, 2);
                return (float) Math.sqrt(delta);
            } else {
                return distance;
            }
        } else {
            return distance;
        }
    }

    /**
     * Calculates the distance to the specified beacon using the <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">log-distance
     * path loss model</a>.
     */
    public static float calculateDistanceTo(Beacon beacon, float rssi) {
        return calculateDistance(rssi, beacon.getCalibratedRssi(), beacon.getCalibratedDistance(), pathLossParameter);
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
        return calculateDistance(rssi, getCalibratedRssiAtOneMeter(calibratedRssi, calibratedDistance), pathLossParameter);
    }

    public static float getCalibratedRssiAtOneMeter(float calibratedRssi, float calibratedDistance) {
        float calibratedRssiAtOneMeter;
        if (calibratedDistance == IBeacon.CALIBRATION_DISTANCE_DEFAULT) {
            calibratedRssiAtOneMeter = calibratedRssi;
        } else if (calibratedDistance == Eddystone.CALIBRATION_DISTANCE_DEFAULT) {
            calibratedRssiAtOneMeter = calibratedRssi + SIGNAL_LOSS_AT_ONE_METER;
        } else {
            calibratedRssiAtOneMeter = CALIBRATED_RSSI_AT_ONE_METER;
        }
        return calibratedRssiAtOneMeter;
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

    public static void setPathLossParameter(float pathLossParameter) {
        BeaconDistanceCalculator.pathLossParameter = pathLossParameter;
    }

    public static float getPathLossParameter() {
        return BeaconDistanceCalculator.pathLossParameter;
    }
}
