package com.nexenio.bleindoorpositioning.location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 29.03.18.
 */
public class LocationUtilTest {

    @Test
    public void meanLocation_multipleLocations_correctMeanLocation() throws Exception {
        List<Location> locationList = new ArrayList<Location>() {{
            add(LocationTest.NEW_YORK_CITY);
            add(LocationTest.BERLIN);
        }};
        Location expectedLocation = new Location(46.61639095, -30.3005094);
        Location actualLocation = LocationUtil.meanLocation(locationList);
        assertEquals(expectedLocation.getLatitude(), actualLocation.getLatitude(), 0.000001);
        assertEquals(expectedLocation.getLongitude(), actualLocation.getLongitude(), 0.000001);
    }

    @Test
    public void meanLocation_singleLocation_correctLocation() throws Exception {
        List<Location> singleLocationList = new ArrayList<Location>() {{
            add(LocationTest.BERLIN);
        }};
        Location expectedLocation = new Location(LocationTest.BERLIN);
        Location actualLocation = LocationUtil.meanLocation(singleLocationList);
        assertEquals(expectedLocation.getLatitude(), actualLocation.getLatitude(), 0.000001);
        assertEquals(expectedLocation.getLongitude(), actualLocation.getLongitude(), 0.000001);
    }

    @Test
    public void meanLocation_emptyLocationList_correctMeanLocation() throws Exception {
        List<Location> emptyLocationList = new ArrayList<>();
        Location expectedLocation = null;
        Location actualLocation = LocationUtil.meanLocation(emptyLocationList);
        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    public void getLocationsBetween() throws Exception {
        final Location firstLocation = LocationTest.SOCCER_FIELD_TOP_LEFT;
        final Location secondLocation = LocationTest.SOCCER_FIELD_TOP_RIGHT;
        final Location thirdLocation = LocationTest.SOCCER_FIELD_BOTTOM_LEFT;
        firstLocation.setTimestamp(0);
        secondLocation.setTimestamp(1);
        thirdLocation.setTimestamp(2);
        List<Location> timestampLocationList = new ArrayList<Location>() {{
            add(firstLocation);
            add(secondLocation);
            add(thirdLocation);
        }};
        List<Location> actualLocations = LocationUtil.getLocationsBetween(timestampLocationList, 0, 1);
        List<Location> expectedLocations = new ArrayList<Location>() {{
            add(secondLocation);
        }};
        assertEquals(expectedLocations, actualLocations);
    }

}