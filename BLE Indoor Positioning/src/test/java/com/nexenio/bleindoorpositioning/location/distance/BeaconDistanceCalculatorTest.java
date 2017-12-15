package com.nexenio.bleindoorpositioning.location.distance;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 22.11.17.
 */
public class BeaconDistanceCalculatorTest {

    @Test
    public void calculateDistance() throws Exception {
        int rssiAtZeroMeters = -45;
        int rssiAtOneMeter = -65;

        float calculatedDistance = BeaconDistanceCalculator.calculateDistance(-80, rssiAtOneMeter, BeaconDistanceCalculator.PATH_LOSS_PARAMETER_INDOOR);
        assertEquals(8, calculatedDistance, 1);

        calculatedDistance = BeaconDistanceCalculator.calculateDistance(-100, rssiAtOneMeter, BeaconDistanceCalculator.PATH_LOSS_PARAMETER_INDOOR);
        assertEquals(110, calculatedDistance, 10);
    }

    @Test
    public void calculateDistance_calibratedRssi_calibratedDistance() throws Exception {
        int rssiAtZeroMeters = -45;
        int rssiAtOneMeter = -65;
        float calculatedDistance = BeaconDistanceCalculator.calculateDistance(rssiAtZeroMeters, rssiAtOneMeter, BeaconDistanceCalculator.PATH_LOSS_PARAMETER_INDOOR);
        assertEquals(0, calculatedDistance, 0.1);
        calculatedDistance = BeaconDistanceCalculator.calculateDistance(rssiAtOneMeter, rssiAtOneMeter, BeaconDistanceCalculator.PATH_LOSS_PARAMETER_INDOOR);
        assertEquals(1, calculatedDistance, 0.1);
    }

}