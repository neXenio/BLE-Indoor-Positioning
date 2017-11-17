package com.nexenio.bleindoorpositioning.ble;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by steppschuh on 15.11.17.
 */

public abstract class Beacon {

    protected UUID uuid;
    protected int rssi;
    protected int major;
    protected int minor;
    protected List<AdvertisingPacket> advertisingPackets;
    protected LocationProvider locationProvider;

    public Beacon() {
        this.locationProvider = createLocationProvider();
    }

    public boolean hasLocation() {
        return locationProvider.getLocation() != null;
    }

    public Location getLocation() {
        return locationProvider.getLocation();
    }

    public abstract LocationProvider createLocationProvider();

    public boolean hasAnyAdvertisingPacket() {
        return advertisingPackets != null && !advertisingPackets.isEmpty();
    }

    public AdvertisingPacket getLatestAdvertisingPacket() {
        if (!hasAnyAdvertisingPacket()) {
            return null;
        }
        return advertisingPackets.get(advertisingPackets.size() - 1);
    }

    public void addAdvertisingPacket(AdvertisingPacket advertisingPacket) {
        if (advertisingPackets == null) {
            advertisingPackets = new ArrayList<>();
        }
        advertisingPackets.add(advertisingPacket);
        trimAdvertisingPackets();
    }

    public void trimAdvertisingPackets() {
        if (!hasAnyAdvertisingPacket()) {
            return;
        }
        List<AdvertisingPacket> removableAdvertisingPackets = new ArrayList<>();
        AdvertisingPacket latestAdvertisingPacket = getLatestAdvertisingPacket();
        long minimumPacketTimestamp = System.currentTimeMillis() - AdvertisingPacket.MAXIMUM_PACKET_AGE;
        for (AdvertisingPacket advertisingPacket : advertisingPackets) {
            if (advertisingPacket == latestAdvertisingPacket) {
                // don't remove the latest packet
                continue;
            }
            if (advertisingPacket.getTimestamp() < minimumPacketTimestamp) {
                // mark old packets as removable
                removableAdvertisingPackets.add(advertisingPacket);
            }
        }

        advertisingPackets.removeAll(removableAdvertisingPackets);
    }

    /*
        Getter & Setter
     */

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public List<AdvertisingPacket> getAdvertisingPackets() {
        return advertisingPackets;
    }

    public void setAdvertisingPackets(List<AdvertisingPacket> advertisingPackets) {
        this.advertisingPackets = advertisingPackets;
    }

    public LocationProvider getLocationProvider() {
        return locationProvider;
    }

    public void setLocationProvider(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }
}
