package com.nexenio.bleindoorpositioning.location.distance;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 22.11.17.
 */
public class BeaconDistanceCalculatorTest {

    @Test
    public void calculateDistance() throws Exception {
        int txLevel = -8;
        int rssiAtZeroMeters = -55;
        int rssiAtOneMeter = -75;

        int rssi = -90;
        int expectedDistance = 10;

        int rssiatFourMeters = -82;
        int expectedFourMeters = 4;

        float calculatedDistance = BeaconDistanceCalculator.calculateDistance(rssiatFourMeters, rssiAtOneMeter, 1, txLevel);
        System.out.println("Error: " + (expectedFourMeters - calculatedDistance));
        assertEquals(calculatedDistance, expectedFourMeters, 3);

        calculatedDistance = BeaconDistanceCalculator.calculateDistance(rssi, rssiAtOneMeter, 1, txLevel);
        System.out.println("Error: " + (expectedDistance - calculatedDistance));
        assertEquals(calculatedDistance, expectedDistance, 3);

        calculatedDistance = BeaconDistanceCalculator.calculateDistance(rssi, rssiAtZeroMeters, 0, txLevel);
        System.out.println("Error: " + (expectedDistance - calculatedDistance));
        assertEquals(calculatedDistance, expectedDistance, 3);
    }

    @Test
    public void calculateDistance_calibratedRssi_calibratedDistance() throws Exception {
        int txLevel = -8;
        int rssiAtZeroMeters = -55;
        int rssiAtOneMeter = -75;
        float calculatedDistance = BeaconDistanceCalculator.calculateDistance(rssiAtZeroMeters, rssiAtZeroMeters, 0, txLevel);
        assertEquals(calculatedDistance, 0, 0);
        calculatedDistance = BeaconDistanceCalculator.calculateDistance(rssiAtOneMeter, rssiAtOneMeter, 1, txLevel);
        assertEquals(calculatedDistance, 1, 0);
    }

}