package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacketFactoryManager;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.MeanFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.WindowFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 07.12.17.
 */

public class BeaconManager {

    private static volatile BeaconManager instance;

    private BeaconFactory beaconFactory = new BeaconFactory();

    private AdvertisingPacketFactoryManager advertisingPacketFactoryManager = new AdvertisingPacketFactoryManager();

    private Map<String, Beacon> beaconMap = new LinkedHashMap<>();

    private final Set<BeaconUpdateListener> beaconUpdateListeners = new HashSet<>();

    private long inactivityDuration = TimeUnit.MINUTES.toMillis(3);

    private Beacon closestBeacon;

    private static final WindowFilter meanFilter = new MeanFilter(15, TimeUnit.SECONDS);

    private BeaconManager() {

    }

    public static BeaconManager getInstance() {
        if (instance == null) {
            synchronized (BeaconManager.class) {
                if (instance == null) {
                    instance = new BeaconManager();
                }
            }
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
        //TODO move outside method
        processClosestBeacon(beacon);
        instance.notifyBeaconUpdateListeners(beacon);
        return advertisingPacket;
    }

    public static void processClosestBeacon(Beacon beacon) {
        BeaconManager instance = getInstance();

        meanFilter.setMaximumTimestamp(beacon.getLatestAdvertisingPacket().getTimestamp());
        meanFilter.setMinimumTimestamp(beacon.getLatestAdvertisingPacket().getTimestamp() - meanFilter.getTimeUnit().toMillis(meanFilter.getDuration()));

        if (instance.closestBeacon == null) {
            instance.closestBeacon = beacon;
        } else {
            if (instance.closestBeacon != beacon) {
                if (beacon.getDistance(meanFilter) + 1 < instance.closestBeacon.getDistance(meanFilter)) {
                    instance.setClosestBeacon(beacon);
                }
            }
        }
    }

    private void notifyBeaconUpdateListeners(Beacon beacon) {
        synchronized (beaconUpdateListeners) {
            for (BeaconUpdateListener beaconUpdateListener : beaconUpdateListeners) {
                try {
                    beaconUpdateListener.onBeaconUpdated(beacon);
                } catch (ClassCastException e) {
                    // meh
                }
            }
        }
    }

    public static boolean registerBeaconUpdateListener(BeaconUpdateListener beaconUpdateListener) {
        synchronized (getInstance().beaconUpdateListeners) {
            return getInstance().beaconUpdateListeners.add(beaconUpdateListener);
        }
    }

    public static boolean unregisterBeaconUpdateListener(BeaconUpdateListener beaconUpdateListener) {
        synchronized (getInstance().beaconUpdateListeners) {
            return getInstance().beaconUpdateListeners.remove(beaconUpdateListener);
        }
    }

    public static String getBeaconKey(String macAddress, AdvertisingPacket advertisingPacket) {
        return macAddress + "-" + Arrays.hashCode(advertisingPacket.getData());
    }

    public static Beacon getBeacon(String macAddress, AdvertisingPacket advertisingPacket) {
        String key = getBeaconKey(macAddress, advertisingPacket);
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
        for (Iterator<Map.Entry<String, Beacon>> beaconMapIterator = instance.beaconMap.entrySet().iterator(); beaconMapIterator.hasNext(); ) {
            Map.Entry<String, Beacon> beaconEntry = beaconMapIterator.next();
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

    public Beacon getClosestBeacon() {
        return closestBeacon;
    }

    public void setClosestBeacon(Beacon closestBeacon) {
        this.closestBeacon = closestBeacon;
    }

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
