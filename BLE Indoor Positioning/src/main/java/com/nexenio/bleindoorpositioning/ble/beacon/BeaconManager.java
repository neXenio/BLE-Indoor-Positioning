package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steppschuh on 07.12.17.
 */

public class BeaconManager {

    private static BeaconManager instance;

    private Map<String, Beacon> beaconMap = new HashMap<>();

    private BeaconManager() {

    }

    public static BeaconManager getInstance() {
        if (instance == null) {
            instance = new BeaconManager();
        }
        return instance;
    }

    public void processAdvertisingPacket(String macAddress, AdvertisingPacket advertisingPacket) {
        Beacon beacon;
        if (beaconMap.containsKey(macAddress)) {
            beacon = beaconMap.get(macAddress);
        } else {
            beacon = Beacon.from(advertisingPacket);
            if (beacon == null) {
                return;
            }
            beaconMap.put(macAddress, beacon);
        }
        beacon.addAdvertisingPacket(advertisingPacket);
    }

    /*
        Getter & Setter
     */

    public Map<String, Beacon> getBeaconMap() {
        return beaconMap;
    }

}
