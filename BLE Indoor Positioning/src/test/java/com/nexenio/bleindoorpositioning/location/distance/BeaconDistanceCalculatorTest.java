package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
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
    public void calculateDistanceWithAltitudeTo_smallDistance_correctDistance() throws Exception {
        Beacon dummyBeacon = createDummyBeacon();
        // without pythagoras
        dummyBeacon.setRssi(-50);
        float expectedDistance = BeaconDistanceCalculator.calculateDistanceTo(dummyBeacon, dummyBeacon.getRssi());
        float actualDistance = BeaconDistanceCalculator.calculateDistanceWithoutAltitudeDelta(dummyBeacon, dummyBeacon.getRssi());
        assertEquals(expectedDistance, actualDistance, 0);
    }

    @Test
    public void calculateDistanceWithAltitudeTo_largeDistance_correctDistance() throws Exception {
        Beacon dummyBeacon = createDummyBeacon();
        // with pythagoras
        dummyBeacon.setRssi(-60);
        float absoluteDistance = BeaconDistanceCalculator.calculateDistanceTo(dummyBeacon, dummyBeacon.getRssi());
        float expectedDistance = (float) Math.sqrt(Math.pow(absoluteDistance, 2) - Math.pow(dummyBeacon.getLocation().getAltitude(), 2));
        float actualDistance = BeaconDistanceCalculator.calculateDistanceWithoutAltitudeDelta(dummyBeacon, dummyBeacon.getRssi());
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

    public Beacon createDummyBeacon() {
        Beacon dummyBeacon = new Beacon() {
            @Override
            public LocationProvider createLocationProvider() {
                return null;
            }
        };
        dummyBeacon.setLocationProvider(new LocationProvider() {
            @Override
            public Location getLocation() {
                Location location = new Location(LocationTest.BERLIN);
                location.setAltitude(2);
                return location;
            }
        });
        return dummyBeacon;
    }

}