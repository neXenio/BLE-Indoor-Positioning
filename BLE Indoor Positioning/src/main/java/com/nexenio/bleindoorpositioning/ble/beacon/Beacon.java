package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacketUtil;
import com.nexenio.bleindoorpositioning.ble.advertising.EddystoneAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.KalmanFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.RssiFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.WindowFilter;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.distance.BeaconDistanceCalculator;
import com.nexenio.bleindoorpositioning.location.provider.BeaconLocationProvider;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 15.11.17.
 */

public abstract class Beacon<P extends AdvertisingPacket> {

    public static final long MAXIMUM_PACKET_AGE = TimeUnit.SECONDS.toMillis(60);

    protected String macAddress;
    protected int rssi; // in dBm
    protected int calibratedRssi; // in dBm
    protected int calibratedDistance; // in m
    protected int transmissionPower; // in dBm
    protected float distance; // in m
    protected boolean shouldUpdateDistance = true;
    protected final ArrayList<P> advertisingPackets = new ArrayList<>();
    protected BeaconLocationProvider<? extends Beacon> locationProvider;

    public Beacon() {
        this.locationProvider = createLocationProvider();
    }

    /**
     * @deprecated use a {@link BeaconFactory} instead (e.g. in {@link BeaconManager#beaconFactory}).
     */
    @Deprecated
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
        return locationProvider != null && locationProvider.hasLocation();
    }

    public Location getLocation() {
        return locationProvider == null ? null : locationProvider.getLocation();
    }

    public static List<Location> getLocations(List<? extends Beacon> beacons) {
        List<Location> locations = new ArrayList<>();
        for (Beacon beacon : beacons) {
            if (beacon.hasLocation()) {
                locations.add(beacon.getLocation());
            }
        }
        return locations;
    }

    public abstract BeaconLocationProvider<? extends Beacon> createLocationProvider();

    public boolean hasAnyAdvertisingPacket() {
        return !advertisingPackets.isEmpty();
    }

    public P getOldestAdvertisingPacket() {
        synchronized (advertisingPackets) {
            if (!hasAnyAdvertisingPacket()) {
                return null;
            }
            return advertisingPackets.get(0);
        }
    }

    public P getLatestAdvertisingPacket() {
        synchronized (advertisingPackets) {
            if (!hasAnyAdvertisingPacket()) {
                return null;
            }
            return advertisingPackets.get(advertisingPackets.size() - 1);
        }
    }

    /**
     * Returns an ArrayList of AdvertisingPackets that have been received in the specified time
     * range. If no packets match, an empty list will be returned.
     *
     * @param startTimestamp minimum timestamp, inclusive
     * @param endTimestamp   maximum timestamp, exclusive
     */
    public ArrayList<P> getAdvertisingPacketsBetween(long startTimestamp, long endTimestamp) {
        return AdvertisingPacketUtil.getAdvertisingPacketsBetween(new ArrayList<>(advertisingPackets), startTimestamp, endTimestamp);
    }

    public ArrayList<P> getAdvertisingPacketsFromLast(long amount, TimeUnit timeUnit) {
        return getAdvertisingPacketsBetween(System.currentTimeMillis() - timeUnit.toMillis(amount), System.currentTimeMillis());
    }

    public ArrayList<P> getAdvertisingPacketsSince(long timestamp) {
        return getAdvertisingPacketsBetween(timestamp, System.currentTimeMillis());
    }

    public ArrayList<P> getAdvertisingPacketsBefore(long timestamp) {
        return getAdvertisingPacketsBetween(0, timestamp);
    }

    public void addAdvertisingPacket(P advertisingPacket) {
        synchronized (advertisingPackets) {
            rssi = advertisingPacket.getRssi();

            P latestAdvertisingPacket = getLatestAdvertisingPacket();
            if (latestAdvertisingPacket == null || !advertisingPacket.dataEquals(latestAdvertisingPacket)) {
                applyPropertiesFromAdvertisingPacket(advertisingPacket);
            }

            if (latestAdvertisingPacket != null && latestAdvertisingPacket.getTimestamp() > advertisingPacket.getTimestamp()) {
                return;
            }

            advertisingPackets.add(advertisingPacket);
            trimAdvertisingPackets();
            invalidateDistance();
        }
    }

    public void applyPropertiesFromAdvertisingPacket(P advertisingPacket) {
        //setTransmissionPower(lastAdvertisingPacket.get);
    }

    public void trimAdvertisingPackets() {
        synchronized (advertisingPackets) {
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

    public boolean hasBeenSeenInThePast(long duration, TimeUnit timeUnit) {
        if (!hasAnyAdvertisingPacket()) {
            return false;
        }
        return getLatestAdvertisingPacket().getTimestamp() > System.currentTimeMillis() - timeUnit.toMillis(duration);
    }

    public float getRssi(RssiFilter filter) {
        return filter.filter(this);
    }

    public float getFilteredRssi() {
        return getRssi(createSuggestedWindowFilter());
    }

    protected void invalidateDistance() {
        shouldUpdateDistance = true;
    }

    public float getDistance() {
        if (shouldUpdateDistance) {
            distance = getDistance(createSuggestedWindowFilter());
            shouldUpdateDistance = false;
        }
        return distance;
    }

    public float getDistance(RssiFilter filter) {
        float filteredRssi = getRssi(filter);
        // TODO get real device elevation with 3D multilateration
        //return BeaconDistanceCalculator.calculateDistanceWithoutElevationDeltaToDevice(this, filteredRssi, 1);
        return BeaconDistanceCalculator.calculateDistanceTo(this, filteredRssi);
    }

    public float getEstimatedAdvertisingRange() {
        return BeaconUtil.getAdvertisingRange(transmissionPower);
    }

    public long getLatestTimestamp() {
        if (!hasAnyAdvertisingPacket()) {
            return 0;
        }
        return getLatestAdvertisingPacket().getTimestamp();
    }

    public WindowFilter createSuggestedWindowFilter() {
        return new KalmanFilter(getLatestTimestamp());
    }

    /**
     * This function and its reverse are implemented with indicative naming in BeaconUtil.
     *
     * @deprecated use {@link BeaconUtil#AscendingRssiComparator} instead
     */
    @Deprecated
    public static Comparator<Beacon> RssiComparator = new Comparator<Beacon>() {
        public int compare(Beacon firstBeacon, Beacon secondBeacon) {
            if (firstBeacon.equals(secondBeacon)) {
                return 0;
            }
            return Integer.compare(firstBeacon.rssi, secondBeacon.rssi);
        }
    };

    @Override
    public String toString() {
        return "Beacon{" +
                ", macAddress='" + macAddress + '\'' +
                ", rssi=" + rssi +
                ", calibratedRssi=" + calibratedRssi +
                ", calibratedDistance=" + calibratedDistance +
                ", transmissionPower=" + transmissionPower +
                ", advertisingPackets=" + advertisingPackets +
                '}';
    }

    /*
        Getter & Setter
     */

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
        invalidateDistance();
    }

    public int getCalibratedRssi() {
        return calibratedRssi;
    }

    public void setCalibratedRssi(int calibratedRssi) {
        this.calibratedRssi = calibratedRssi;
        invalidateDistance();
    }

    public int getCalibratedDistance() {
        return calibratedDistance;
    }

    public void setCalibratedDistance(int calibratedDistance) {
        this.calibratedDistance = calibratedDistance;
        invalidateDistance();
    }

    public int getTransmissionPower() {
        return transmissionPower;
    }

    public void setTransmissionPower(int transmissionPower) {
        this.transmissionPower = transmissionPower;
    }

    public ArrayList<P> getAdvertisingPackets() {
        return advertisingPackets;
    }

    public LocationProvider getLocationProvider() {
        return locationProvider;
    }

    public void setLocationProvider(BeaconLocationProvider<? extends Beacon> locationProvider) {
        this.locationProvider = locationProvider;
    }

}
