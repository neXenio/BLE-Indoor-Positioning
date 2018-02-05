package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacketFactoryManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 07.12.17.
 */

public class BeaconManager {

    private static BeaconManager instance;

    private BeaconFactory beaconFactory = new BeaconFactory();

    private AdvertisingPacketFactoryManager advertisingPacketFactoryManager = new AdvertisingPacketFactoryManager();

    private Map<String, Beacon> beaconMap = new LinkedHashMap<>();

    private Set<BeaconUpdateListener> beaconUpdateListeners = new HashSet<>();

    private long inactivityDuration = TimeUnit.MINUTES.toMillis(3);

    private BeaconManager() {

    }

    public static BeaconManager getInstance() {
        if (instance == null) {
            instance = new BeaconManager();
        }
        return instance;
    }

    public static AdvertisingPacket processAdvertisingData(String macAddress, byte[] advertisingData, int rssi) {
        AdvertisingPacket advertisingPacket = getInstance().advertisingPacketFactoryManager.createAdvertisingPacket(advertisingData);
        if (advertisingPacket != null) {
            advertisingPacket.setRssi(rssi);
        }
        return processAdvertisingPacket(macAddress, advertisingPacket);
    }

    public static AdvertisingPacket processAdvertisingPacket(String macAddress, AdvertisingPacket advertisingPacket) {
        if (advertisingPacket == null) {
            return null;
        }
        BeaconManager instance = getInstance();
        String key = getBeaconKey(macAddress, advertisingPacket);
        Beacon beacon;
        if (instance.beaconMap.containsKey(key)) {
            beacon = instance.beaconMap.get(key);
        } else {
            removeInactiveBeacons();
            beacon = instance.beaconFactory.createBeacon(advertisingPacket);
            if (beacon == null) {
                return advertisingPacket;
            }
            beacon.setMacAddress(macAddress);
            instance.beaconMap.put(key, beacon);
        }
        beacon.addAdvertisingPacket(advertisingPacket);
        instance.notifyBeaconUpdateListeners(beacon);
        return advertisingPacket;
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

    public static IBeacon getIBeacon(String macAddress) {
        return (IBeacon) getBeacon(macAddress, IBeacon.class);
    }

    public static Eddystone getEddystone(String macAddress) {
        return (Eddystone) getBeacon(macAddress, Eddystone.class);
    }

    public static Beacon getBeacon(String macAddress, Class<? extends Beacon> beaconClass) {
        String key = getBeaconKey(macAddress, BeaconUtil.getReadableBeaconType(beaconClass));
        return getInstance().beaconMap.get(key);
    }

    public static void removeInactiveBeacons() {
        removeInactiveBeacons(getInstance().inactivityDuration, TimeUnit.MILLISECONDS);
    }

    public static void removeInactiveBeacons(long inactivityDuration, TimeUnit timeUnit) {
        removeInactiveBeacons(System.currentTimeMillis() - timeUnit.toMillis(inactivityDuration));
    }

    public static void removeInactiveBeacons(long minimumAdvertisingTimestamp) {
        BeaconManager instance = getInstance();
        AdvertisingPacket latestAdvertisingPacket;
        List<String> inactiveBeaconKeys = new ArrayList<>();
        for (Map.Entry<String, Beacon> beaconEntry : instance.beaconMap.entrySet()) {
            latestAdvertisingPacket = beaconEntry.getValue().getLatestAdvertisingPacket();
            if (latestAdvertisingPacket == null || latestAdvertisingPacket.getTimestamp() < minimumAdvertisingTimestamp) {
                inactiveBeaconKeys.add(beaconEntry.getKey());
            }
        }
        instance.beaconMap.keySet().removeAll(inactiveBeaconKeys);
    }

    /*
        Getter & Setter
     */

    public BeaconFactory getBeaconFactory() {
        return beaconFactory;
    }

    public void setBeaconFactory(BeaconFactory beaconFactory) {
        this.beaconFactory = beaconFactory;
    }

    public AdvertisingPacketFactoryManager getAdvertisingPacketFactoryManager() {
        return advertisingPacketFactoryManager;
    }

    public void setAdvertisingPacketFactoryManager(AdvertisingPacketFactoryManager advertisingPacketFactoryManager) {
        this.advertisingPacketFactoryManager = advertisingPacketFactoryManager;
    }

    public Map<String, Beacon> getBeaconMap() {
        return beaconMap;
    }

    public long getInactivityDuration() {
        return inactivityDuration;
    }

    public void setInactivityDuration(long inactivityDuration) {
        this.inactivityDuration = inactivityDuration;
    }

}
