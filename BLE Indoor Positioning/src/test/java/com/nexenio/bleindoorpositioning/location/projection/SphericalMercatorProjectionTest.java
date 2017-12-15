package com.nexenio.bleindoorpositioning.location.projection;

import com.nexenio.bleindoorpositioning.location.LocationTest;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by steppschuh on 15.12.17.
 */
public class SphericalMercatorProjectionTest {

    @Test
    public void yToLatitude() throws Exception {
        double expectedLatitude = LocationTest.BERLIN.getLatitude();
        double expectedLongitude = LocationTest.BERLIN.getLongitude();

        double y = SphericalMercatorProjection.latitudeToY(expectedLatitude);
        double x = SphericalMercatorProjection.longitudeToX(expectedLongitude);

        double actualLatitude = SphericalMercatorProjection.yToLatitude(y);
        double actualLongitude = SphericalMercatorProjection.xToLongitude(x);

        assertEquals(expectedLatitude, actualLatitude, 0.00001);
        assertEquals(expectedLongitude, actualLongitude, 0.00001);
    }

}