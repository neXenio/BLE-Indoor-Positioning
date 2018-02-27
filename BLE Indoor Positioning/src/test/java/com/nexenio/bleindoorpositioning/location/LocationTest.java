package com.nexenio.bleindoorpositioning.location;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 17.11.17.
 */
public class LocationTest {

    /*
        Olympiastadion Berlin
        https://goo.gl/maps/xSx4gBEpHB42
     */
    public static final Location SOCCER_FIELD_TOP_LEFT = new Location(52.514889, 13.238694);
    public static final Location SOCCER_FIELD_TOP_RIGHT = new Location(52.515029, 13.240220);
    public static final Location SOCCER_FIELD_BOTTOM_LEFT = new Location(52.514288, 13.238832);
    public static final Location SOCCER_FIELD_BOTTOM_RIGHT = new Location(52.514430, 13.240369);
    public static final Location SOCCER_FIELD_CENTER = new Location(52.514658, 13.239525);
    public static final int SOCCER_FIELD_WIDTH = 105;
    public static final int SOCCER_FIELD_HEIGHT = 68;

    public static final Location NEW_YORK_CITY = new Location(40.7127753, -74.0059728);
    public static final Location BERLIN = new Location(52.5200066, 13.404954);
    public static final int DISTANCE_NYC_BERLIN = 6385 * 1000;

    @Test
    public void getAngleTo_validLocations_correctAngles() throws Exception {
        // expected values were taken from <a href="http://www.igismap.com/map-tool/bearing-angle">here</a>.
        double angle;
        angle = BERLIN.getAngleTo(NEW_YORK_CITY);
        assertEquals(360 - 63.975, angle, 0.001);

        angle = NEW_YORK_CITY.getAngleTo(BERLIN);
        assertEquals(46.167, angle, 0.001);

        angle = BERLIN.getAngleTo(BERLIN);
        assertEquals(0, angle, 0);

        angle = SOCCER_FIELD_TOP_LEFT.getAngleTo(SOCCER_FIELD_TOP_RIGHT);
        assertEquals(81.426, angle, 0.001);

        angle = SOCCER_FIELD_TOP_LEFT.getAngleTo(SOCCER_FIELD_BOTTOM_LEFT);
        assertEquals(172.045, angle, 0.001);

        angle = SOCCER_FIELD_BOTTOM_LEFT.getAngleTo(SOCCER_FIELD_TOP_LEFT);
        assertEquals(360 - 7.955, angle, 0.001);
    }

    @Test
    public void getDistanceTo_validLocations_correctDistance() throws Exception {
        double distance = NEW_YORK_CITY.getDistanceTo(BERLIN);
        assertEquals(DISTANCE_NYC_BERLIN, distance, 1);
    }

    @Test
    public void getNewLocation_validLocations_correctLocation() throws Exception {
        double angle = SOCCER_FIELD_TOP_LEFT.getAngleTo(SOCCER_FIELD_BOTTOM_RIGHT);
        double distance = SOCCER_FIELD_TOP_LEFT.getDistanceTo(SOCCER_FIELD_BOTTOM_RIGHT);
        Location calculatedLocation = SOCCER_FIELD_TOP_LEFT.getShiftedLocation(distance, angle);
        double delta = calculatedLocation.getDistanceTo(SOCCER_FIELD_BOTTOM_RIGHT);
        assertEquals(0, delta, 2);
    }
}