package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by steppschuh on 07.12.17.
 */

public class BeaconManager {

    private static BeaconManager instance;

    private Map<String, Beacon> beaconMap = new HashMap<>();

    private Set<BeaconUpdateListener> beaconUpdateListeners = new HashSet<>();

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
            beacon.setMacAddress(macAddress);
            beaconMap.put(macAddress, beacon);
        }
        beacon.addAdvertisingPacket(advertisingPacket);
        notifyBeaconUpdateListeners(beacon);
    }

    private void notifyBeaconUpdateListeners(Beacon beacon) {
        for (BeaconUpdateListener beaconUpdateListener : beaconUpdateListeners) {
            beaconUpdateListener.onBeaconUpdated(beacon);
        }
    }

    public static boolean registerBeaconUpdateListener(BeaconUpdateListener beaconUpdateListener) {
        return getInstance().beaconUpdateListeners.add(beaconUpdateListener);
    }

    public static boolean unregisterBeaconUpdateListener(BeaconUpdateListener beaconUpdateListener) {
        return getInstance().beaconUpdateListeners.remove(beaconUpdateListener);
    }

    /*
        Getter & Setter
     */

    public Map<String, Beacon> getBeaconMap() {
        return beaconMap;
    }

}
