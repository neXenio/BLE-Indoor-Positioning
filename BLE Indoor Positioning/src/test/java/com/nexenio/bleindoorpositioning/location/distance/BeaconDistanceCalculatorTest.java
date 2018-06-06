package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationTest;
import com.nexenio.bleindoorpositioning.location.provider.IBeaconLocationProvider;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 22.11.17.
 */
public class BeaconDistanceCalculatorTest {

    @Test
    public void calculateDistanceWithoutElevationDeltaToFloor_smallDistanceLowAltitude_correctDistance() throws Exception {
        Beacon lowDummyBeacon = createLowElevationDummyBeacon();
        // without pythagoras
        lowDummyBeacon.setRssi(-65);
        float expectedDistance = BeaconDistanceCalculator.calculateDistanceTo(lowDummyBeacon, lowDummyBeacon.getRssi());
        float actualDistance = BeaconDistanceCalculator.calculateDistanceWithoutElevationDeltaToDevice(lowDummyBeacon, lowDummyBeacon.getRssi(), 0);
        assertEquals(expectedDistance, actualDistance, 0);
    }

    @Test
    public void calculateDistanceWithoutElevationDeltaToFloor_largeDistanceLowAltitude_correctDistance() throws Exception {
        Beacon lowDummyBeacon = createLowElevationDummyBeacon();
        // with pythagoras
        lowDummyBeacon.setRssi(-90);
        float absoluteDistance = BeaconDistanceCalculator.calculateDistanceTo(lowDummyBeacon, lowDummyBeacon.getRssi());
        float expectedDistance = (float) Math.sqrt(Math.pow(absoluteDistance, 2) - Math.pow(lowDummyBeacon.getLocation().getElevation(), 2));
        float actualDistance = BeaconDistanceCalculator.calculateDistanceWithoutElevationDeltaToDevice(lowDummyBeacon, lowDummyBeacon.getRssi(), 0);
        assertEquals(expectedDistance, actualDistance, 0);
    }

    @Test
    public void calculateDistanceWithoutElevationDeltaToFloor_smallDistanceHighAltitude_correctDistance() throws Exception {
        Beacon highDummyBeacon = createHighElevationDummyBeacon();
        // without pythagoras
        highDummyBeacon.setRssi(-65);
        float expectedDistance = BeaconDistanceCalculator.calculateDistanceTo(highDummyBeacon, highDummyBeacon.getRssi());
        float actualDistance = BeaconDistanceCalculator.calculateDistanceWithoutElevationDeltaToDevice(highDummyBeacon, highDummyBeacon.getRssi(), 0);
        assertEquals(expectedDistance, actualDistance, 0);
    }

    @Test
    public void calculateDistanceWithoutElevationDeltaToFloor_largeDistanceHighAltitude_correctDistance() throws Exception {
        Beacon highDummyBeacon = createHighElevationDummyBeacon();
        // with pythagoras
        highDummyBeacon.setRssi(-90);
        float absoluteDistance = BeaconDistanceCalculator.calculateDistanceTo(highDummyBeacon, highDummyBeacon.getRssi());
        float expectedDistance = (float) Math.sqrt(Math.pow(absoluteDistance, 2) - Math.pow(highDummyBeacon.getLocation().getElevation(), 2));
        float actualDistance = BeaconDistanceCalculator.calculateDistanceWithoutElevationDeltaToDevice(highDummyBeacon, highDummyBeacon.getRssi(), 0);
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

    public Beacon createLowElevationDummyBeacon() {
        return createDummyBeacon(LocationTest.BERLIN, 2, -65);
    }

    public Beacon createHighElevationDummyBeacon() {
        return createDummyBeacon(LocationTest.BERLIN, 10, -65);
    }

    public Beacon createDummyBeacon(final Location location, final double elevation, int calibratedRssi) {
        IBeacon dummyBeacon = new IBeacon();
        final Location adjustedLocation = new Location(location);
        location.setElevation(elevation);
        dummyBeacon.setLocationProvider(new IBeaconLocationProvider<IBeacon>(dummyBeacon) {
            @Override
            protected boolean canUpdateLocation() {
                return true;
            }

            @Override
            public void updateLocation() {
                this.location = adjustedLocation;
            }
        });
        dummyBeacon.setCalibratedRssi(calibratedRssi);
        return dummyBeacon;
    }

}