package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.KalmanFilter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
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

        beacons.add(BeaconUtil.getBeaconsWithAdvertisingPackets(IBeacon.class, IBeaconAdvertisingPacket.class, expectedDistance));

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
            beacons.add(BeaconUtil.getBeaconsWithAdvertisingPackets(IBeacon.class, IBeaconAdvertisingPacket.class, expectedDistance + i));
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
        float expectedDistance = 2;

        List<IBeacon> beacons = new ArrayList<>();
        IBeacon<IBeaconAdvertisingPacket> expectedBeacon = BeaconUtil.getBeaconsWithAdvertisingPackets(IBeacon.class, IBeaconAdvertisingPacket.class, expectedDistance);
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
            beacons.add(BeaconUtil.getBeaconsWithAdvertisingPackets(IBeacon.class, IBeaconAdvertisingPacket.class, expectedDistance + i));
        }

        IBeacon<IBeaconAdvertisingPacket> expectedBeacon = BeaconUtil.getBeaconsWithAdvertisingPackets(IBeacon.class, IBeaconAdvertisingPacket.class, expectedDistance);
        beacons.add(expectedBeacon);

        IBeacon<IBeaconAdvertisingPacket> actualBeacon = (IBeacon) BeaconUtil.getClosestBeacon(beacons, new KalmanFilter(2, TimeUnit.SECONDS));
        assertEquals("Did not return the closest beacon", expectedBeacon, actualBeacon);
    }
}