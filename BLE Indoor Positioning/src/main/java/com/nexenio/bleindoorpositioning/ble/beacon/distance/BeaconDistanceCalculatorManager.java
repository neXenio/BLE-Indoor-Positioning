package com.nexenio.bleindoorpositioning.ble.beacon.distance;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.RssiFilter;

public final class BeaconDistanceCalculatorManager implements BeaconDistanceCalculator {

    private static volatile BeaconDistanceCalculatorManager instance;

    private BeaconDistanceCalculator beaconDistanceCalculator;

    private BeaconDistanceCalculatorManager() {
        beaconDistanceCalculator = new PathLossBeaconDistanceCalculator();
    }

    public static BeaconDistanceCalculatorManager getInstance() {
        if (instance == null) {
            synchronized (BeaconDistanceCalculatorManager.class) {
                if (instance == null) {
                    instance = new BeaconDistanceCalculatorManager();
                }
            }
        }
        return instance;
    }

    @Override
    public float calculateDistanceTo(Beacon beacon) {
        return beaconDistanceCalculator.calculateDistanceTo(beacon);
    }

    @Override
    public float calculateDistanceTo(Beacon beacon, RssiFilter rssiFilter) {
        return beaconDistanceCalculator.calculateDistanceTo(beacon, rssiFilter);
    }

    @Override
    public float calculateDistanceTo(Beacon beacon, float rssi) {
        return beaconDistanceCalculator.calculateDistanceTo(beacon, rssi);
    }

    public BeaconDistanceCalculator getBeaconDistanceCalculator() {
        return beaconDistanceCalculator;
    }

    public void setBeaconDistanceCalculator(BeaconDistanceCalculator beaconDistanceCalculator) {
        this.beaconDistanceCalculator = beaconDistanceCalculator;
    }

}
