package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.WindowFilter;
import com.nexenio.bleindoorpositioning.location.distance.BeaconDistanceCalculator;

import java.util.Comparator;
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
     * Gets the smallest distance from the given beacons to the user using the default filter.
     *
     * @param beaconList Beacons to evaluate the distance from
     * @return Distance to the closest beacon; Double.MAX_VALUE if no beacon was given
     */
    public static double getSmallestDistance(List<? extends Beacon> beaconList) {
        Beacon beacon = getClosestBeacon(beaconList);
        return (beacon == null) ? Double.MAX_VALUE : beacon.getDistance();
    }

    /**
     * Gets the smallest distance from the given beacons to the user using a specified filter.
     *
     * @param beaconList Beacons to evaluate the distance from
     * @param filter     Filter for getting the distance of the beacons
     * @return Distance to the closest beacon; Double.MAX_VALUE if no beacon was given
     */
    public static double getSmallestDistance(List<? extends Beacon> beaconList, WindowFilter filter) {
        Beacon beacon = getClosestBeacon(beaconList, filter);
        return (beacon == null) ? Double.MAX_VALUE : beacon.getDistance(filter);
    }

    /**
     * Gets the closest beacon from the given beacons to the user using the default filter.
     *
     * @param beaconList Beacons to get the closest one from
     * @return Closest beacon if list is not empty; null else
     */
    public static Beacon getClosestBeacon(List<? extends Beacon> beaconList) {
        if (beaconList.isEmpty()) {
            return null;
        }
        return getClosestBeacon(beaconList, beaconList.get(0).createSuggestedWindowFilter());
    }

    /**
     * Gets the closest beacon from the given beacons to the user using a specified filter.
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
     * Calculate the RSSI for which the calculated distance will be close to the given distance.
     *
     * @param beacon   Beacon from which the rssi should be send
     * @param distance Distance the beacon should be away
     * @return Estimated rssi for the given distance
     */
    public static int calculateRssiForDistance(Beacon beacon, float distance) {
        return calculateRssi(distance, beacon.getCalibratedRssi(), beacon.getCalibratedDistance(), BeaconDistanceCalculator.getPathLossParameter());
    }

    /**
     * Calculates the RSSI using the reverse <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">log-distance
     * path loss model</a>.
     *
     * @param distance           Distance the beacon should be away
     * @param calibratedRssi     the RSSI measured at the calibration distance
     * @param calibratedDistance the distance in meters at which the calibrated RSSI was measured
     * @param pathLossParameter  the path-loss adjustment parameter
     * @return Estimated rssi for the given distance
     */
    public static int calculateRssi(float distance, float calibratedRssi, int calibratedDistance, float pathLossParameter) {
        return calculateRssi(distance, BeaconDistanceCalculator.getCalibratedRssiAtOneMeter(calibratedRssi, calibratedDistance), pathLossParameter);
    }

    /**
     * Calculates the RSSI using the reverse <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">log-distance
     * path loss model</a>.
     *
     * @param distance          Distance for which a rssi should be estimated
     * @param calibratedRssi    the RSSI measured at 1m distance
     * @param pathLossParameter the path-loss adjustment parameter
     */
    public static int calculateRssi(float distance, float calibratedRssi, float pathLossParameter) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance must be greater than 0");
        }
        return (int) (calibratedRssi - ((Math.log(distance) / Math.log(10)) * (10 * pathLossParameter)));
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

    /**
     * Used to sort beacons from highest rssi to lowest rssi.
     */
    public static Comparator<Beacon> DescendingRssiComparator = new Comparator<Beacon>() {
        public int compare(Beacon firstBeacon, Beacon secondBeacon) {
            if (firstBeacon.equals(secondBeacon)) {
                return 0;
            }
            return Integer.compare(secondBeacon.rssi, firstBeacon.rssi);
        }
    };

    /**
     * Used to sort beacons from lowest rssi to highest rssi.
     */
    public static Comparator<Beacon> AscendingRssiComparator = new Comparator<Beacon>() {
        public int compare(Beacon firstBeacon, Beacon secondBeacon) {
            if (firstBeacon.equals(secondBeacon)) {
                return 0;
            }
            return Integer.compare(firstBeacon.rssi, secondBeacon.rssi);
        }
    };

}
