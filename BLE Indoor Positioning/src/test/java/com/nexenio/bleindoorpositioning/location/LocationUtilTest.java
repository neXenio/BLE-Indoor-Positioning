package com.nexenio.bleindoorpositioning.location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by leon on 29.03.18.
 */
public class LocationUtilTest {

    @Test
    public void meanLocation_multipleLocations_correctMeanLocation() throws Exception {
        List<Location> locationList = new ArrayList<>(Arrays.asList(
                LocationTest.NEW_YORK_CITY,
                LocationTest.BERLIN
        ));
        Location expectedLocation = new Location(46.61639095, -30.3005094);
        Location actualLocation = LocationUtil.calculateMeanLocation(locationList);
        assertTrue(actualLocation.latitudeAndLongitudeEquals(expectedLocation, 0.00000001));
    }

    @Test
    public void meanLocation_multipleLocationsCloseDistances_correctMeanLocation() throws Exception {
        List<Location> locationList = new ArrayList<>(Arrays.asList(
                LocationTest.SOCCER_FIELD_BOTTOM_LEFT,
                LocationTest.SOCCER_FIELD_BOTTOM_RIGHT,
                LocationTest.SOCCER_FIELD_TOP_LEFT,
                LocationTest.SOCCER_FIELD_TOP_RIGHT,
                LocationTest.SOCCER_FIELD_CENTER
        ));
        Location expectedLocation = LocationTest.SOCCER_FIELD_CENTER;
        Location actualLocation = LocationUtil.calculateMeanLocation(locationList);
        assertTrue(actualLocation.latitudeAndLongitudeEquals(expectedLocation, 0.00001));
    }

    @Test
    public void meanLocation_singleLocation_correctLocation() throws Exception {
        List<Location> singleLocationList = new ArrayList<>(Arrays.asList(
                LocationTest.BERLIN
        ));
        Location expectedLocation = new Location(LocationTest.BERLIN);
        Location actualLocation = LocationUtil.calculateMeanLocation(singleLocationList);
        assertTrue(actualLocation.latitudeAndLongitudeEquals(expectedLocation));
    }

    @Test
    public void meanLocation_emptyLocationList_correctMeanLocation() throws Exception {
        List<Location> emptyLocationList = new ArrayList<>();
        Location expectedLocation = null;
        Location actualLocation = LocationUtil.calculateMeanLocation(emptyLocationList);
        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    public void getLocationsBetween_locationListWithTimestamps_correctTimestampFilteredLocations() throws Exception {
        final Location firstLocation = LocationTest.SOCCER_FIELD_TOP_LEFT;
        final Location secondLocation = LocationTest.SOCCER_FIELD_TOP_RIGHT;
        final Location thirdLocation = LocationTest.SOCCER_FIELD_BOTTOM_LEFT;
        firstLocation.setTimestamp(0);
        secondLocation.setTimestamp(1);
        thirdLocation.setTimestamp(2);
        List<Location> timestampLocationList = new ArrayList<>(Arrays.asList(
                firstLocation, secondLocation, thirdLocation
        ));
        List<Location> actualLocations = LocationUtil.getLocationsBetween(timestampLocationList, 1, 2);
        List<Location> expectedLocations = new ArrayList<>(Arrays.asList(
                secondLocation
        ));
        assertEquals(expectedLocations, actualLocations);
    }

}