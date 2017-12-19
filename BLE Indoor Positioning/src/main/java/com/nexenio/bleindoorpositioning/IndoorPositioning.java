package com.nexenio.bleindoorpositioning;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.listener.LocationListener;
import com.nexenio.bleindoorpositioning.location.multilateration.Multilateration;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IndoorPositioning implements LocationProvider, BeaconUpdateListener {

    public static final long UPDATE_INTERVAL_IMMEDIATE = 50;
    public static final long UPDATE_INTERVAL_FAST = 100;
    public static final long UPDATE_INTERVAL_MEDIUM = 500;
    public static final long UPDATE_INTERVAL_SLOW = 3000;

    private static IndoorPositioning instance;

    private Location lastKnownLocation;
    private long lastLocationUpdateTimestamp;
    private long maximumLocationUpdateInterval = UPDATE_INTERVAL_MEDIUM;
    private Set<LocationListener> locationListeners = new HashSet<>();

    private IndoorPositioning() {

    }

    public static IndoorPositioning getInstance() {
        if (instance == null) {
            instance = new IndoorPositioning();
            BeaconManager.registerBeaconUpdateListener(instance);
        }
        return instance;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void onBeaconUpdated(Beacon beacon) {
        if (shouldUpdateLocation()) {
            updateLocation();
        }
    }

    private void updateLocation() {
        List<Beacon> usableBeacons = getUsableBeacons(BeaconManager.getInstance().getBeaconMap().values());

        if (usableBeacons.size() < 3) {
            return; // multilateration requires at least 3 beacons
        } else if (usableBeacons.size() > 3) {
            usableBeacons.sort(Beacon.RssiComparator);
            Collections.reverse(usableBeacons);
            for (int beaconIndex = usableBeacons.size() - 1; beaconIndex >= 3; beaconIndex--) {
                if (usableBeacons.get(beaconIndex).getRssi() < 80) {
                    usableBeacons.remove(beaconIndex);
                }
            }
        }

        Multilateration multilateration = new Multilateration(usableBeacons);
        onLocationUpdated(multilateration.getLocation());
        lastLocationUpdateTimestamp = System.currentTimeMillis();
    }

    private static List<Beacon> getUsableBeacons(Collection<Beacon> availableBeacons) {
        List<Beacon> usableBeacons = new ArrayList<>();
        long minimumTimestamp = System.currentTimeMillis() - 1000;
        for (Beacon beacon : availableBeacons) {
            if (!beacon.hasLocation()) {
                continue; // beacon has no location assigned, can't use it for multilateration
            }
            if (!beacon.hasBeenSeenSince(minimumTimestamp)) {
                continue; // beacon hasn't been in range recently, avoid using outdated data
            }
            usableBeacons.add(beacon);
        }
        return usableBeacons;
    }

    private void onLocationUpdated(Location location) {
        lastKnownLocation = location;
        for (LocationListener locationListener : locationListeners) {
            locationListener.onLocationUpdated(this, lastKnownLocation);
        }
    }

    private boolean shouldUpdateLocation() {
        return lastLocationUpdateTimestamp < System.currentTimeMillis() - maximumLocationUpdateInterval;
    }

    public static boolean registerLocationListener(LocationListener locationListener) {
        return getInstance().locationListeners.add(locationListener);
    }

    public static boolean unregisterLocationListener(LocationListener locationListener) {
        return getInstance().locationListeners.remove(locationListener);
    }

}
