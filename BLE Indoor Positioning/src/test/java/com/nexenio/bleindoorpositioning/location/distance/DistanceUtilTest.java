package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.provider.LocationPredictorTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 23.11.17.
 */
public class DistanceUtilTest {

    @Test
    public void speedFilter_locations_accurateLocation() throws Exception {
        // larger distance than speed filter
        Location oldLocation = LocationPredictorTest.GENDARMENMARKT;
        oldLocation.setTimestamp(0);
        Location newLocation = oldLocation.getShiftedLocation(5, 0);
        newLocation.setTimestamp(1000);
        Location expectedLocation = oldLocation.getShiftedLocation(2, 0);
        Location actualLocation = DistanceUtil.speedFilter(oldLocation, newLocation, 2);
        assertEquals(expectedLocation.getLatitude(), actualLocation.getLatitude(), 0);
        assertEquals(expectedLocation.getLongitude(), actualLocation.getLongitude(), 0);

        // smaller distance than speed filter
        oldLocation = LocationPredictorTest.GENDARMENMARKT;
        oldLocation.setTimestamp(0);
        newLocation = oldLocation.getShiftedLocation(1, 0);
        newLocation.setTimestamp(1000);
        expectedLocation = oldLocation.getShiftedLocation(1, 0);
        actualLocation = DistanceUtil.speedFilter(oldLocation, newLocation, 2);
        assertEquals(expectedLocation.getLatitude(), actualLocation.getLatitude(), 0);
        assertEquals(expectedLocation.getLongitude(), actualLocation.getLongitude(), 0);
    }

    @Test
    public void getReasonableSmallerEvenDistance() throws Exception {
        long actual = DistanceUtil.getReasonableSmallerEvenDistance(1);
        assertEquals(1, actual);

        actual = DistanceUtil.getReasonableSmallerEvenDistance(12);
        assertEquals(10, actual);

        actual = DistanceUtil.getReasonableSmallerEvenDistance(52);
        assertEquals(50, actual);

        actual = DistanceUtil.getReasonableSmallerEvenDistance(123);
        assertEquals(100, actual);
    }

    @Test
    public void getMaximumEvenIncrement() throws Exception {
        int actual = DistanceUtil.getMaximumEvenIncrement(1);
        assertEquals(1, actual);

        actual = DistanceUtil.getMaximumEvenIncrement(12);
        assertEquals(10, actual);

        actual = DistanceUtil.getMaximumEvenIncrement(52);
        assertEquals(10, actual);

        actual = DistanceUtil.getMaximumEvenIncrement(123);
        assertEquals(100, actual);
    }

    @Test
    public void getClosestEvenDistance() throws Exception {
        long actual = DistanceUtil.getClosestEvenDistance(96, 10);
        assertEquals(100, actual);

        actual = DistanceUtil.getClosestEvenDistance(94, 10);
        assertEquals(90, actual);

        actual = DistanceUtil.getClosestEvenDistance(99, 100);
        assertEquals(100, actual);

        actual = DistanceUtil.getClosestEvenDistance(49, 100);
        assertEquals(0, actual);
    }

}