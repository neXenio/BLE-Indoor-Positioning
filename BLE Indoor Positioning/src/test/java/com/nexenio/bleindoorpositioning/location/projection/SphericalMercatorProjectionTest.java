package com.nexenio.bleindoorpositioning.location.projection;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 15.12.17.
 */
public class SphericalMercatorProjectionTest {

    @Test
    public void latitudeToY() throws Exception {
        double expectedY = 6894701.008722784;
        double actualY = SphericalMercatorProjection.latitudeToY(LocationTest.BERLIN.getLatitude());
        assertEquals(expectedY, actualY, 0.00001);
    }

    @Test
    public void longitudeToX() throws Exception {
        double expectedX = 1492232.6533872557;
        double actualX = SphericalMercatorProjection.longitudeToX(LocationTest.BERLIN.getLongitude());
        assertEquals(expectedX, actualX, 0.00001);
    }

    @Test
    public void yToLatitude() throws Exception {
        double actualLatitude = SphericalMercatorProjection.yToLatitude(6894701.008722784);
        assertEquals(LocationTest.BERLIN.getLatitude(), actualLatitude, 0.00001);
    }

    @Test
    public void xToLongitude() throws Exception {
        double actualLongitude = SphericalMercatorProjection.xToLongitude(1492232.6533872557);
        assertEquals(LocationTest.BERLIN.getLongitude(), actualLongitude, 0.00001);
    }

    @Test
    public void conversion() throws Exception {
        double expectedLatitude = LocationTest.BERLIN.getLatitude();
        double y = SphericalMercatorProjection.latitudeToY(expectedLatitude);
        double actualLatitude = SphericalMercatorProjection.yToLatitude(y);
        assertEquals(expectedLatitude, actualLatitude, 0.00001);

        double expectedLongitude = LocationTest.BERLIN.getLongitude();
        double x = SphericalMercatorProjection.longitudeToX(expectedLongitude);
        double actualLongitude = SphericalMercatorProjection.xToLongitude(x);
        assertEquals(expectedLongitude, actualLongitude, 0.00001);
    }

    @Test
    public void conversionWithAltitude() throws Exception {
        Location expectedLocation = LocationTest.BERLIN;
        expectedLocation.setElevation(2);
        double[] ecef = SphericalMercatorProjection.locationToEcef(expectedLocation);
        Location actualLocation = SphericalMercatorProjection.ecefToLocation(ecef);
        double delta = expectedLocation.getDistanceTo(actualLocation);
        assertEquals(0, delta, 0.0000001);
    }

}