package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.EddystoneAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.ArmaFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.MeanFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.RssiFilter;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.distance.BeaconDistanceCalculator;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 15.11.17.
 */

public abstract class Beacon<P extends AdvertisingPacket> {

    public static final long MAXIMUM_PACKET_AGE = TimeUnit.SECONDS.toMillis(60);

    protected UUID uuid;
    protected String macAddress;
    protected int rssi; // in dBm
    protected int calibratedRssi; // in dBm
    protected int calibratedDistance; // in m
    protected int transmissionPower; // in dBm
    protected List<P> advertisingPackets = new ArrayList<>();
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
        return locationProvider.getLocation() != null && locationProvider.getLocation().hasLatitudeAndLongitude();
    }

    public Location getLocation() {
        return locationProvider.getLocation();
    }

    public abstract LocationProvider createLocationProvider();

    public boolean hasAnyAdvertisingPacket() {
        return advertisingPackets != null && !advertisingPackets.isEmpty();
    }

    public P getLatestAdvertisingPacket() {
        if (!hasAnyAdvertisingPacket()) {
            return null;
        }
        return advertisingPackets.get(advertisingPackets.size() - 1);
    }

    public List<P> getAdvertisingPacketsBetween(long startTimestamp, long endTimestamp) {
        List<P> matchingAdvertisingPackets = new ArrayList<>();
        for (P advertisingPacket : advertisingPackets) {
            if (advertisingPacket.getTimestamp() <= startTimestamp) {
                continue;
            }
            if (advertisingPacket.getTimestamp() > endTimestamp) {
                continue;
            }
            matchingAdvertisingPackets.add(advertisingPacket);
        }
        return matchingAdvertisingPackets;
    }

    public List<P> getAdvertisingPacketsFromLast(long amount, TimeUnit timeUnit) {
        return getAdvertisingPacketsBetween(System.currentTimeMillis() - timeUnit.toMillis(amount), System.currentTimeMillis());
    }

    public List<P> getAdvertisingPacketsSince(long timestamp) {
        return getAdvertisingPacketsBetween(timestamp, System.currentTimeMillis());
    }

    public List<P> getAdvertisingPacketsBefore(long timestamp) {
        return getAdvertisingPacketsBetween(0, timestamp);
    }

    public void addAdvertisingPacket(P advertisingPacket) {
        rssi = advertisingPacket.getRssi();
        if (!hasAnyAdvertisingPacket() || !advertisingPacket.dataEquals(getLatestAdvertisingPacket())) {
            applyPropertiesFromAdvertisingPacket(advertisingPacket);
        }
        advertisingPackets.add(advertisingPacket);
        trimAdvertisingPackets();
    }

    public void applyPropertiesFromAdvertisingPacket(P advertisingPacket) {
        //setTransmissionPower(lastAdvertisingPacket.get);
    }

    public void trimAdvertisingPackets() {
        if (!hasAnyAdvertisingPacket()) {
            return;
        }
        List<P> removableAdvertisingPackets = new ArrayList<>();
        AdvertisingPacket latestAdvertisingPacket = getLatestAdvertisingPacket();
        long minimumPacketTimestamp = System.currentTimeMillis() - MAXIMUM_PACKET_AGE;
        for (P advertisingPacket : advertisingPackets) {
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

    public boolean equalsLastAdvertisingPackage(P advertisingPacket) {
        return hasAnyAdvertisingPacket() && getLatestAdvertisingPacket().equals(advertisingPacket);
    }

    public boolean hasBeenSeenSince(long timestamp) {
        if (!hasAnyAdvertisingPacket()) {
            return false;
        }
        return getLatestAdvertisingPacket().getTimestamp() > timestamp;
    }

    public float getRssi(RssiFilter filter) {
        return filter.filter(this);
    }

    public float getDistance() {
        RssiFilter filter = new ArmaFilter();
        filter.setMaximumTimestamp(this.getLatestAdvertisingPacket().getTimestamp());
        filter.setMinimumTimestamp(this.getLatestAdvertisingPacket().getTimestamp() - filter.getDuration());
        return getDistance(filter);
    }

    public float getDistance(RssiFilter filter) {
        float filteredRssi = getRssi(filter);
        return BeaconDistanceCalculator.calculateDistanceTo(this, filteredRssi);
    }

    public float getMeanRssi() {
        RssiFilter meanFilter = new MeanFilter();
        return getRssi(meanFilter);
    }

    public float getEstimatedAdvertisingRange() {
        return BeaconUtil.getAdvertisingRange(transmissionPower);
    }

    public static Comparator<Beacon> RssiComparator = new Comparator<Beacon>() {

        public int compare(Beacon firstBeacon, Beacon secondBeacon) {
            return firstBeacon.rssi - secondBeacon.rssi;
        }

    };

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

    public List<P> getAdvertisingPackets() {
        return advertisingPackets;
    }

    public void setAdvertisingPackets(List<P> advertisingPackets) {
        this.advertisingPackets = advertisingPackets;
    }

    public LocationProvider getLocationProvider() {
        return locationProvider;
    }

    public void setLocationProvider(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }
}
