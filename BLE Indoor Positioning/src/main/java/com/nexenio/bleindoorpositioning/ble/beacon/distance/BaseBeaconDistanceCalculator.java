package com.nexenio.bleindoorpositioning.ble.beacon.distance;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.RssiFilter;

public abstract class BaseBeaconDistanceCalculator implements BeaconDistanceCalculator {

    @Override
    public float calculateDistanceTo(Beacon beacon) {
        return calculateDistanceTo(beacon, beacon.getFilteredRssi());
    }

    @Override
    public float calculateDistanceTo(Beacon beacon, RssiFilter rssiFilter) {
        return calculateDistanceTo(beacon, rssiFilter.filter(beacon));
    }

}
