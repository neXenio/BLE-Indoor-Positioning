package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.KalmanFilter;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class BeaconUtilTest {

    @Test
    public void getSmallestDistance_emptyList_returnsDoubleMaxValue() {
        double distance = BeaconUtil.getSmallestDistance(new ArrayList<Beacon<AdvertisingPacket>>(), new KalmanFilter());
        assertEquals("Distance should be initialized with Double.MAX_VALUE", Double.MAX_VALUE, distance, 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getSmallestDistance_singleBeacon_returnsBeaconDistance() throws Exception {
        float expectedDistance = 2;

        List<IBeacon> beacons = new ArrayList<>();

        beacons.add(getBeaconsWithAdvertisingPackets(IBeacon.class, IBeaconAdvertisingPacket.class, expectedDistance));

        double actualDistance = BeaconUtil.getSmallestDistance(beacons, new KalmanFilter(2, TimeUnit.SECONDS));
        assertEquals("Actual distance did not match of the given beacon", expectedDistance, actualDistance, 0.01);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getSmallestDistance_multipleBeacons_returnsClosestBeaconsDistance() throws Exception {
        int numberOfBeaconsToUse = 5;
        float expectedDistance = 2;

        List<IBeacon> beacons = new ArrayList<>();
        for (int i = 0; i < numberOfBeaconsToUse; i++) {
            beacons.add(getBeaconsWithAdvertisingPackets(IBeacon.class, IBeaconAdvertisingPacket.class, expectedDistance + i));
        }
        double actualDistance = BeaconUtil.getSmallestDistance(beacons, new KalmanFilter(2, TimeUnit.SECONDS));
        assertEquals("Actual distance did not return distance of closest beacon", expectedDistance, actualDistance, 0.01);
    }

    @Test
    public void getClosestBeacon_noBeacon_returnsNull() {
        Beacon beacon = BeaconUtil.getClosestBeacon(new ArrayList<Beacon<AdvertisingPacket>>(), new KalmanFilter());
        assertEquals("Beacon should not be initialized", null, beacon);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getClosestBeacon_singleBeacon_returnsBeacon() throws Exception {
        List<IBeacon> beacons = new ArrayList<>();
        IBeacon<IBeaconAdvertisingPacket> expectedBeacon = getBeaconsWithAdvertisingPackets(IBeacon.class, IBeaconAdvertisingPacket.class, 2);
        beacons.add(expectedBeacon);

        IBeacon<IBeaconAdvertisingPacket> actualBeacon = (IBeacon) BeaconUtil.getClosestBeacon(beacons, new KalmanFilter(2, TimeUnit.SECONDS));
        assertEquals("Did not return the only beacon added", expectedBeacon, actualBeacon);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getClosestBeacon_multipleBeacons_returnsClosestBeacon() throws Exception {
        int numberOfBeaconsToUse = 5;
        float expectedDistance = 2;

        List<IBeacon> beacons = new ArrayList<>();

        for (int i = 1; i < numberOfBeaconsToUse; i++) {
            beacons.add(getBeaconsWithAdvertisingPackets(IBeacon.class, IBeaconAdvertisingPacket.class, expectedDistance + i));
        }

        IBeacon<IBeaconAdvertisingPacket> expectedBeacon = getBeaconsWithAdvertisingPackets(IBeacon.class, IBeaconAdvertisingPacket.class, expectedDistance);
        beacons.add(expectedBeacon);

        IBeacon<IBeaconAdvertisingPacket> actualBeacon = (IBeacon) BeaconUtil.getClosestBeacon(beacons, new KalmanFilter(2, TimeUnit.SECONDS));
        assertEquals("Did not return the closest beacon", expectedBeacon, actualBeacon);
    }


    /**
     * Please note that if you want a single instance of a beacon instead of adding it to a list you
     * need to specify the complete type e.g. "IBeacon<IBeaconAdvertisingPacket>".</IBeaconAdvertisingPacket>
     *
     * @param beaconClass            Class token of the specific beacon type
     * @param advertisingPacketClass Class token of the specific advertising packet type
     * @param distance               Distance for which a rssi will be generated
     * @param <A>                    Specific advertising packet type
     * @param <B>                    Specific beacon type
     * @param <CA>                   Class of the specific advertising packet type
     * @param <CB>                   Class of the specific beacon type
     * @return Beacon for the specified beacon type with the specified advertising packet type and
     *         set rssi
     * @throws ExecutionException If reflections fail
     */
    public static <A extends AdvertisingPacket, B extends Beacon<A>, CA extends Class<A>, CB extends Class<B>> B getBeaconsWithAdvertisingPackets(CB beaconClass, CA advertisingPacketClass, float distance) throws ExecutionException {
        try {
            return getBeaconsWithAdvertisingPackets(beaconClass.getConstructor(), advertisingPacketClass.getConstructor(byte[].class), distance);
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

}