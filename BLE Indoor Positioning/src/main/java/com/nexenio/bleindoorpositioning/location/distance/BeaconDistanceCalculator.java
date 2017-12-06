package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;

/**
 * Created by steppschuh on 22.11.17.
 */

public abstract class BeaconDistanceCalculator {

    public static float calculateDistanceTo(Beacon beacon) {
        return calculateDistance(beacon.getRssi(), beacon.getCalibratedRssi());
    }

    public static float calculateDistance(int rssi, int calibratedRssi) {
        return 0;
    }

}
