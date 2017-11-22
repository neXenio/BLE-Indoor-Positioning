package com.nexenio.bleindoorpositioning.location.distance;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 22.11.17.
 */
public class BeaconDistanceCalculatorTest {

    @Test
    public void calculateDistance() throws Exception {
        int tx = -8;
        int rssi = -80;
        int rssiAtZeroMeters = -55;
        int rssiAtOneMeter = -75;
        int expectedDistance = 10;

        float calculatedDistance = BeaconDistanceCalculator.calculateDistance(rssi, rssiAtZeroMeters);
        assertEquals(calculatedDistance, expectedDistance, 1);

        calculatedDistance = BeaconDistanceCalculator.calculateDistance(rssi, rssiAtOneMeter);
        assertEquals(calculatedDistance, expectedDistance, 1);
    }

    @Test
    public void calculateDistance_calibratedRssi_calibratedDistance() throws Exception {
        int rssiAtZeroMeters = -55;
        int rssiAtOneMeter = -75;
        float calculatedDistance = BeaconDistanceCalculator.calculateDistance(rssiAtZeroMeters, rssiAtZeroMeters);
        assertEquals(calculatedDistance, 0, 0);
        calculatedDistance = BeaconDistanceCalculator.calculateDistance(rssiAtOneMeter, rssiAtOneMeter);
        assertEquals(calculatedDistance, 1, 0);
    }

}