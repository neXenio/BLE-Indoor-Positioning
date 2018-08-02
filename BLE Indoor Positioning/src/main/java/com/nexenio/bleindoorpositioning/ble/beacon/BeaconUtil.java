package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.WindowFilter;

import java.util.List;

/**
 * Created by steppschuh on 24.11.17.
 */

public abstract class BeaconUtil {

    /**
     * Estimates a maximum distance at which advertising packages sent using the specified
     * transmission power can be received.
     *
     * @param transmissionPower the tx power (in dBm) of the beacon
     * @return estimated range in meters
     * @see <a href="https://support.kontakt.io/hc/en-gb/articles/201621521-Transmission-power-Range-and-RSSI">Kontakt.io
     *         Knowledge Base</a>
     */
    public static float getAdvertisingRange(int transmissionPower) {
        if (transmissionPower < -30) {
            return 1;
        } else if (transmissionPower < -25) {
            return getAdvertisingRange(transmissionPower, -30, 2);
        } else if (transmissionPower < -18) {
            return getAdvertisingRange(transmissionPower, -20, 4);
        } else if (transmissionPower < -14) {
            return getAdvertisingRange(transmissionPower, -16, 16);
        } else if (transmissionPower < -10) {
            return getAdvertisingRange(transmissionPower, -12, 20);
        } else if (transmissionPower < -6) {
            return getAdvertisingRange(transmissionPower, -8, 30);
        } else if (transmissionPower < -2) {
            return getAdvertisingRange(transmissionPower, -4, 40);
        } else if (transmissionPower < 2) {
            return getAdvertisingRange(transmissionPower, 0, 60);
        } else {
            return getAdvertisingRange(transmissionPower, 4, 70);
        }
    }

    /**
     * Gets the smallest distance from the given beacons to the user using a filter.
     *
     * @param beaconList Beacons to evaluate the distance from
     * @param filter     Filter for getting the distance of the beacons
     * @return Distance to the closest beacon; Double.MAX_VALUE if no beacon was given
     */
    public static double getSmallestDistance(List<? extends Beacon> beaconList, WindowFilter filter) {
        double minimumDistance = Double.MAX_VALUE;
        for (Beacon beacon : beaconList) {
            float distance = beacon.getDistance(filter);
            if (distance < minimumDistance) {
                minimumDistance = distance;
            }
        }
        return minimumDistance;
    }

    /**
     * Gets the closest beacon from the given beacons to the user using a filter.
     *
     * @param beaconList Beacons to get the closest one from
     * @param filter     Filter for getting the distance of the beacons
     * @return Closest beacon if list is not empty; null else
     */
    public static Beacon getClosestBeacon(List<? extends Beacon> beaconList, WindowFilter filter) {
        double minimumDistance = Double.MAX_VALUE;
        Beacon closestBeacon = null;
        for (Beacon beacon : beaconList) {
            float distance = beacon.getDistance(filter);
            if (distance < minimumDistance) {
                minimumDistance = distance;
                closestBeacon = beacon;
            }
        }
        return closestBeacon;
    }

    /**
     * Uses a simple rule of three equation. Transmission power values will be incremented by 100 to
     * compensate for negative values.
     */
    public static float getAdvertisingRange(int transmissionPower, int calibratedTransmissionPower, int calibratedRange) {
        return (calibratedRange * (transmissionPower + 100)) / (float) (calibratedTransmissionPower + 100);
    }

    public static String getReadableBeaconType(AdvertisingPacket advertisingPacket) {
        return getReadableBeaconType(advertisingPacket.getBeaconClass());
    }

    public static String getReadableBeaconType(Class<? extends Beacon> beaconClass) {
        return beaconClass.getSimpleName();
    }

}
