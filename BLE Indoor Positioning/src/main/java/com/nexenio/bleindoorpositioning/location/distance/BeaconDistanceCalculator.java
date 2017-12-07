package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.ble.Beacon;

/**
 * Created by steppschuh on 22.11.17.
 */

public abstract class BeaconDistanceCalculator {

    public static float calculateDistanceTo(Beacon beacon) {
        return calculateDistance(beacon.getRssi(), beacon.getCalibratedRssi(), beacon.getCalibratedDistance(), beacon.getTransmissionPower());
    }

    /**
     * Calculates distances based distance with the log-distance path loss model <a
     * href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">Log-distance path loss
     * model</a>
     */
    public static float calculateDistance(double rssi, double calibratedRssi, double calibratedDistance, int txLevel) {
        // propagation constant or path-loss exponent (Free space has n = 2 for reference)
        // Line-of-Sight in building 1.6 - 1.8
        double n = 1.7;

        // Convert to calibrated distance at 1 meter
        if (calibratedDistance == 0) {

            //TODO TxPower Level to calibrated Rssi
            // txPower -8 dBm --> -72 dBm RSSI for BlueUp Maxi Beacon
            if (txLevel == -8) {
                //calibratedRssi = calibratedRssi - 20;
                calibratedRssi = -72;
                calibratedDistance = 1;
            }
        }

        // Both RSSI and (supposedly correctly named) txPower are in dBm, hence subtraction before exponentiation.
        // If the values were actual (in Watts), then division without exponentiation.
        double ratio = calibratedRssi - rssi;

        if (ratio < 0 || calibratedRssi == rssi) {
            return (float) calibratedDistance;
        }

        double exponent = (ratio / (10 * n));
        float distance = (float) (calibratedDistance * Math.pow(10, exponent));
        return distance;
    }

}
