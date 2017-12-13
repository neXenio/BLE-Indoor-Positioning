package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.EddystoneAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.distance.BeaconDistanceCalculator;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 15.11.17.
 */

public abstract class Beacon {

    public static final long MAXIMUM_PACKET_AGE = TimeUnit.SECONDS.toMillis(60);

    protected UUID uuid;
    protected String macAddress;
    protected int rssi; // in dBm
    protected int calibratedRssi; // in dBm
    protected int calibratedDistance; // in cm
    protected int transmissionPower; // in dBm
    protected List<AdvertisingPacket> advertisingPackets = new ArrayList<>();
    protected LocationProvider locationProvider;

    public Beacon() {
        this.locationProvider = createLocationProvider();
    }

    public static Beacon from(AdvertisingPacket advertisingPacket) {
        Beacon beacon = null;
        if (advertisingPacket instanceof IBeaconAdvertisingPacket) {
            beacon = new IBeacon();
        } else if (advertisingPacket instanceof EddystoneAdvertisingPacket) {
            beacon = new Eddystone();
        }
        return beacon;
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
        rssi = advertisingPacket.getRssi();
        if (!hasAnyAdvertisingPacket() || !advertisingPacket.dataEquals(getLatestAdvertisingPacket())) {
            applyPropertiesFromAdvertisingPacket(advertisingPacket);
        }
        advertisingPackets.add(advertisingPacket);
        trimAdvertisingPackets();
    }

    public void applyPropertiesFromAdvertisingPacket(AdvertisingPacket advertisingPacket) {
        //setTransmissionPower(lastAdvertisingPacket.get);
    }

    public void trimAdvertisingPackets() {
        if (!hasAnyAdvertisingPacket()) {
            return;
        }
        List<AdvertisingPacket> removableAdvertisingPackets = new ArrayList<>();
        AdvertisingPacket latestAdvertisingPacket = getLatestAdvertisingPacket();
        long minimumPacketTimestamp = System.currentTimeMillis() - MAXIMUM_PACKET_AGE;
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

    public boolean equalsLastAdvertisingPackage(AdvertisingPacket advertisingPacket) {
        return hasAnyAdvertisingPacket() && getLatestAdvertisingPacket().equals(advertisingPacket);
    }

    public float getDistance() {
        return BeaconDistanceCalculator.calculateDistanceTo(this);
    }

    public float getEstimatedAdvertisingRange() {
        return BeaconUtil.getAdvertisingRange(transmissionPower);
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

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getCalibratedRssi() {
        return calibratedRssi;
    }

    public void setCalibratedRssi(int calibratedRssi) {
        this.calibratedRssi = calibratedRssi;
    }

    public int getCalibratedDistance() {
        return calibratedDistance;
    }

    public void setCalibratedDistance(int calibratedDistance) {
        this.calibratedDistance = calibratedDistance;
    }

    public int getTransmissionPower() {
        return transmissionPower;
    }

    public void setTransmissionPower(int transmissionPower) {
        this.transmissionPower = transmissionPower;
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
