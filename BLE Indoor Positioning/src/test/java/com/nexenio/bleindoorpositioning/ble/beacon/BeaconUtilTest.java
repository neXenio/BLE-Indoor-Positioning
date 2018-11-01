package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.KalmanFilter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BeaconUtilTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private BeaconCreator<IBeacon> beaconCreator = new BeaconCreator<>(IBeacon.class);

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

        beacons.add(beaconCreator.createBeaconWithAdvertisingPacket(expectedDistance));

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
            beacons.add(beaconCreator.createBeaconWithAdvertisingPacket(expectedDistance + i));
        }
        double actualDistance = BeaconUtil.getSmallestDistance(beacons, new KalmanFilter(2, TimeUnit.SECONDS));
        assertEquals("Actual distance did not return distance of closest beacon", expectedDistance, actualDistance, 0.01);
    }

    @Test
    public void getClosestBeacon_noBeacon_returnsNull() {
        Beacon beacon = BeaconUtil.getClosestBeacon(new ArrayList<Beacon<AdvertisingPacket>>(), new KalmanFilter());
        assertNull("Beacon should not be initialized", beacon);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getClosestBeacon_singleBeacon_returnsBeacon() throws Exception {
        List<IBeacon> beacons = new ArrayList<>();
        IBeacon<IBeaconAdvertisingPacket> expectedBeacon = beaconCreator.createBeaconWithAdvertisingPacket(2);
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
            beacons.add(beaconCreator.createBeaconWithAdvertisingPacket(expectedDistance + i));
        }

        IBeacon<IBeaconAdvertisingPacket> expectedBeacon = beaconCreator.createBeaconWithAdvertisingPacket(expectedDistance);
        beacons.add(expectedBeacon);

        IBeacon<IBeaconAdvertisingPacket> actualBeacon = (IBeacon) BeaconUtil.getClosestBeacon(beacons, new KalmanFilter(2, TimeUnit.SECONDS));
        assertEquals("Did not return the closest beacon", expectedBeacon, actualBeacon);
    }

    @Test
    public void calculateRssi_distanceSmallerOne_returnsRssiGreaterThanCalibrated() {
        float calibratedRssi = -35;
        float actualRssi = BeaconUtil.calculateRssi(0.5F, -35, 2F);
        assertTrue(calibratedRssi < actualRssi);
    }

    @Test
    public void calculateRssi_distanceGreaterOne_returnsRssiSmallerThanCalibrated() {
        float calibratedRssi = -35;
        float actualRssi = BeaconUtil.calculateRssi(10, -35, 2F);
        assertTrue(calibratedRssi > actualRssi);
    }

    @Test
    public void calculateRssi_distanceEqualOne_returnsRssiEqualThanCalibrated() {
        float calibratedRssi = -35;
        float actualRssi = BeaconUtil.calculateRssi(1, -35, 2F);
        assertEquals(calibratedRssi, actualRssi, 0.1);
    }

    @Test
    public void calculateRssi_negativeDistance_throwsException() {
        exception.expect(IllegalArgumentException.class);
        BeaconUtil.calculateRssi(-1, -35, 2F);
    }

}