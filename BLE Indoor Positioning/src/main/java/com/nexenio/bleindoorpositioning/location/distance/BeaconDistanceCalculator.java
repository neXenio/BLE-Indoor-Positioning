package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.RssiFilter;

public interface BeaconDistanceCalculator {

    float calculateDistanceTo(Beacon beacon);

    float calculateDistanceTo(Beacon beacon, RssiFilter rssiFilter);

    float calculateDistanceTo(Beacon beacon, float rssi);

}
