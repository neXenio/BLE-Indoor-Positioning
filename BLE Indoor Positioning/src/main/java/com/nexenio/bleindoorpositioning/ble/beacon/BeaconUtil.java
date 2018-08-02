package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.WindowFilter;
import com.nexenio.bleindoorpositioning.location.distance.BeaconDistanceCalculator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
     * Calculate the rssi for which the calculated distance will be close to the given distance.
     *
     * @param beacon   Beacon from which the rssi should be send
     * @param distance Distance the beacon should be away
     * @return Estimated rssi for the given distance
     */
    public static int calculateRssiForDistance(Beacon beacon, float distance) {
        return calculateRssi(distance, beacon.getCalibratedRssi(), beacon.getCalibratedDistance(), BeaconDistanceCalculator.getPathLossParameter());
    }

    /**
     * Calculates distances using the reverse <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">log-distance
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
     * Calculates distances using the reverse <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">log-distance
     * path loss model</a>.
     *
     * @param distance          Distance for which a rssi should be estimated
     * @param calibratedRssi    the RSSI measured at 1m distance
     * @param pathLossParameter the path-loss adjustment parameter
     */
    public static int calculateRssi(float distance, float calibratedRssi, float pathLossParameter) {
        return (int) ((Math.log(distance) / Math.log(10)) * (10 * pathLossParameter) + calibratedRssi);
    }

    /**
     * Please note that if you want a single instance of a beacon instead of adding it to a list you
     * need to specify the complete type e.g. "IBeacon<IBeaconAdvertisingPacket>".</IBeaconAdvertisingPacket>
     *
     * @param beaconClazz            Class token of the specific beacon type
     * @param advertisingPacketClazz Class token of the specific advertising packet type
     * @param distance               Distance for which a rssi will be generated
     * @param <A>                    Specific advertising packet type
     * @param <B>                    Specific beacon type
     * @param <CA>                   Class of the specific advertising packet type
     * @param <CB>                   Class of the specific beacon type
     * @return Beacon for the specified beacon type with the specified advertising packet type and
     *         set rssi
     * @throws ExecutionException If reflections fail
     */
    public static <A extends AdvertisingPacket, B extends Beacon<A>, CA extends Class<A>, CB extends Class<B>> B getBeaconsWithAdvertisingPackets(CB beaconClazz, CA advertisingPacketClazz, float distance) throws ExecutionException {
        try {
            return getBeaconsWithAdvertisingPackets(beaconClazz.getConstructor(), advertisingPacketClazz.getConstructor(byte[].class), distance);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * @param beaconConstructor            Constructor of the specific beacon type
     * @param advertisingPacketConstructor Constructor of the specific advertising packet type
     * @param distance                     Distance for which a rssi will be generated
     * @param <A>                          Specific advertising packet type
     * @param <B>                          Specific beacon type
     * @param <CA>                         Class of the specific advertising packet type
     * @param <CB>                         Class of the specific beacon type
     * @return Beacon for the specified beacon type with the specified advertising packet type and
     *         set rssi
     */
    private static <A extends AdvertisingPacket, B extends Beacon<A>, CA extends Constructor<A>, CB extends Constructor<B>> B getBeaconsWithAdvertisingPackets(CB beaconConstructor, CA advertisingPacketConstructor, float distance) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        B beacon = beaconConstructor.newInstance();
        A advertisingPacket = advertisingPacketConstructor.newInstance((Object) new byte[30]);

        int rssi = BeaconUtil.calculateRssiForDistance(beacon, distance);
        advertisingPacket.setRssi(-rssi);

        beacon.addAdvertisingPacket(advertisingPacket);
        return beacon;
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
