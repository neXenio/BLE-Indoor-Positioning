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
    public void latitudeToY() {
        double expectedY = 6894701.008722784;
        double actualY = SphericalMercatorProjection.latitudeToY(LocationTest.BERLIN.getLatitude());
        assertEquals(expectedY, actualY, 0.00001);
    }

    @Test
    public void longitudeToX() {
        double expectedX = 1492232.6533872557;
        double actualX = SphericalMercatorProjection.longitudeToX(LocationTest.BERLIN.getLongitude());
        assertEquals(expectedX, actualX, 0.00001);
    }

    @Test
    public void yToLatitude() {
        double actualLatitude = SphericalMercatorProjection.yToLatitude(6894701.008722784);
        assertEquals(LocationTest.BERLIN.getLatitude(), actualLatitude, 0.00001);
    }

    @Test
    public void xToLongitude() {
        double actualLongitude = SphericalMercatorProjection.xToLongitude(1492232.6533872557);
        assertEquals(LocationTest.BERLIN.getLongitude(), actualLongitude, 0.00001);
    }

    @Test
    public void conversion() {
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
    public void conversionWithElevation() {
        Location expectedLocation = LocationTest.BERLIN;
        expectedLocation.setElevation(2);
        double[] ecef = SphericalMercatorProjection.locationToEcef(expectedLocation);
        Location actualLocation = SphericalMercatorProjection.ecefToLocation(ecef);
        double delta = expectedLocation.getDistanceTo(actualLocation);
        assertEquals(0, delta, 0.0000001);
    }

    @Test
    public void geodeticToEcef_location_accurateEcef() {
        double[] expectedCenter = new double[]{3786292.474596871, 890822.9600122868, 5037857.368752121}; // SOCCER_FIELD_CENTER
        double[] geodetic = new double[]{
                Math.toRadians(LocationTest.SOCCER_FIELD_CENTER.getLatitude()),
                Math.toRadians(LocationTest.SOCCER_FIELD_CENTER.getLongitude()),
                Math.toRadians(LocationTest.SOCCER_FIELD_CENTER.getAltitude())
        };
        double[] actualCenter = SphericalMercatorProjection.geodeticToEcef(geodetic);

        assertEquals(expectedCenter[0], actualCenter[0], 1);
        assertEquals(expectedCenter[1], actualCenter[1], 1);
        assertEquals(expectedCenter[2], actualCenter[2], 1);
    }

    @Test
    public void ecefToGeodetic_ecefArray_accurateGeodetic() {
        double[] expectedGeodetic = new double[]{
                LocationTest.SOCCER_FIELD_CENTER.getLatitude(),
                LocationTest.SOCCER_FIELD_CENTER.getLongitude(),
                LocationTest.SOCCER_FIELD_CENTER.getAltitude()
        };
        double[] ecefArray = new double[]{3786292.474596871, 890822.9600122868, 5037857.368752121}; // SOCCER_FIELD_CENTER
        double[] actualGeodetic = SphericalMercatorProjection.ecefToGeodetic(ecefArray);

        assertEquals(expectedGeodetic[0], Math.toDegrees(actualGeodetic[0]), 1);
        assertEquals(expectedGeodetic[1], Math.toDegrees(actualGeodetic[1]), 1);
        assertEquals(expectedGeodetic[2], Math.toDegrees(actualGeodetic[2]), 1);
    }

}