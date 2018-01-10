package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by steppschuh on 07.12.17.
 */

public class BeaconManager {

    private static BeaconManager instance;

    private Map<String, Beacon> beaconMap = new LinkedHashMap<>();

    private Set<BeaconUpdateListener> beaconUpdateListeners = new HashSet<>();

    private BeaconManager() {

    }

    public static BeaconManager getInstance() {
        if (instance == null) {
            instance = new BeaconManager();
        }
        return instance;
    }

    public static void processAdvertisingPacket(String macAddress, AdvertisingPacket advertisingPacket) {
        BeaconManager instance = getInstance();
        String key = getBeaconKey(macAddress, advertisingPacket);
        Beacon beacon;
        if (instance.beaconMap.containsKey(key)) {
            beacon = instance.beaconMap.get(key);
        } else {
            beacon = Beacon.from(advertisingPacket);
            if (beacon == null) {
                return;
            }
            beacon.setMacAddress(macAddress);
            instance.beaconMap.put(key, beacon);
        }
        beacon.addAdvertisingPacket(advertisingPacket);
        instance.notifyBeaconUpdateListeners(beacon);
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

    public static String getBeaconKey(String macAddress, AdvertisingPacket advertisingPacket) {
        return getBeaconKey(macAddress, BeaconUtil.getReadableBeaconType(advertisingPacket));
    }

    private static String getBeaconKey(String macAddress, String beaconType) {
        return macAddress + "-" + beaconType;
    }

    public static Eddystone getEddystone(String macAddress) {
        return (Eddystone) getBeacon(macAddress, Eddystone.class);
    }

    public static IBeacon getIBeacon(String macAddress) {
        return (IBeacon) getBeacon(macAddress, IBeacon.class);
    }

    public static Beacon getBeacon(String macAddress, Class<? extends Beacon> beaconClass) {
        String key = getBeaconKey(macAddress, BeaconUtil.getReadableBeaconType(beaconClass));
        return getInstance().beaconMap.get(key);
    }

    /*
        Getter & Setter
     */

    public Map<String, Beacon> getBeaconMap() {
        return beaconMap;
    }

}
