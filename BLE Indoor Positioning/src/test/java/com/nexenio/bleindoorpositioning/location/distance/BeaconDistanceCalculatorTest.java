package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationTest;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 22.11.17.
 */
public class BeaconDistanceCalculatorTest {

    @Test
    public void calculateDistanceWithoutAltitudeDeltaToFloor_smallDistanceLowAltitude_correctDistance() throws Exception {
        Beacon lowDummyBeacon = createLowAltitudeDummyBeacon();
        // without pythagoras
        lowDummyBeacon.setRssi(-65);
        float expectedDistance = BeaconDistanceCalculator.calculateDistanceTo(lowDummyBeacon, lowDummyBeacon.getRssi());
        float actualDistance = BeaconDistanceCalculator.calculateDistanceWithoutAltitudeDeltaToFloor(lowDummyBeacon, lowDummyBeacon.getRssi());
        assertEquals(expectedDistance, actualDistance, 0);
    }

    @Test
    public void calculateDistanceWithoutAltitudeDeltaToFloor_largeDistanceLowAltitude_correctDistance() throws Exception {
        Beacon lowDummyBeacon = createLowAltitudeDummyBeacon();
        // with pythagoras
        lowDummyBeacon.setRssi(-90);
        float absoluteDistance = BeaconDistanceCalculator.calculateDistanceTo(lowDummyBeacon, lowDummyBeacon.getRssi());
        float expectedDistance = (float) Math.sqrt(Math.pow(absoluteDistance, 2) - Math.pow(lowDummyBeacon.getLocation().getAltitude(), 2));
        float actualDistance = BeaconDistanceCalculator.calculateDistanceWithoutAltitudeDeltaToFloor(lowDummyBeacon, lowDummyBeacon.getRssi());
        assertEquals(expectedDistance, actualDistance, 0);
    }

    @Test
    public void calculateDistanceWithoutAltitudeDeltaToFloor_smallDistanceHighAltitude_correctDistance() throws Exception {
        Beacon highDummyBeacon = createHighAltitudeDummyBeacon();
        // without pythagoras
        highDummyBeacon.setRssi(-65);
        float expectedDistance = BeaconDistanceCalculator.calculateDistanceTo(highDummyBeacon, highDummyBeacon.getRssi());
        float actualDistance = BeaconDistanceCalculator.calculateDistanceWithoutAltitudeDeltaToFloor(highDummyBeacon, highDummyBeacon.getRssi());
        assertEquals(expectedDistance, actualDistance, 0);
    }

    @Test
    public void calculateDistanceWithoutAltitudeDeltaToFloor_largeDistanceHighAltitude_correctDistance() throws Exception {
        Beacon highDummyBeacon = createHighAltitudeDummyBeacon();
        // with pythagoras
        highDummyBeacon.setRssi(-90);
        float absoluteDistance = BeaconDistanceCalculator.calculateDistanceTo(highDummyBeacon, highDummyBeacon.getRssi());
        float expectedDistance = (float) Math.sqrt(Math.pow(absoluteDistance, 2) - Math.pow(highDummyBeacon.getLocation().getAltitude(), 2));
        float actualDistance = BeaconDistanceCalculator.calculateDistanceWithoutAltitudeDeltaToFloor(highDummyBeacon, highDummyBeacon.getRssi());
        assertEquals(expectedDistance, actualDistance, 0);
    }

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

    public Beacon createLowAltitudeDummyBeacon() {
        return createDummyBeacon(LocationTest.BERLIN, 2, -65);
    }

    public Beacon createHighAltitudeDummyBeacon() {
        return createDummyBeacon(LocationTest.BERLIN, 10, -65);
    }

    public Beacon createDummyBeacon(final Location location, final double altitude, int calibratedRssi) {
        Beacon dummyBeacon = new IBeacon();
        final Location adjustedLocation = new Location(location);
        location.setAltitude(altitude);
        dummyBeacon.setLocationProvider(new LocationProvider() {
            @Override
            public Location getLocation() {
                return adjustedLocation;
            }
        });
        dummyBeacon.setCalibratedRssi(calibratedRssi);
        return dummyBeacon;
    }

}