package com.nexenio.bleindoorpositioning.location;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        double angle;
        angle = SOCCER_FIELD_TOP_LEFT.getAngleTo(SOCCER_FIELD_TOP_RIGHT);
        assertEquals(80, angle, 5);

        angle = SOCCER_FIELD_TOP_LEFT.getAngleTo(SOCCER_FIELD_BOTTOM_LEFT);
        assertEquals(170, angle, 5);

        angle = SOCCER_FIELD_BOTTOM_LEFT.getAngleTo(SOCCER_FIELD_TOP_LEFT);
        assertEquals(350, angle, 5);
    }

    @Test
    public void getDistanceTo_validLocations_correctDistance() throws Exception {
        int distance = (int) NEW_YORK_CITY.getDistanceTo(BERLIN);
        assertEquals(distance, DISTANCE_NYC_BERLIN);
    }

    @Test
    public void getNewLocation_validLocations_correctLocation() throws Exception {
        double angle = SOCCER_FIELD_TOP_LEFT.getAngleTo(SOCCER_FIELD_BOTTOM_RIGHT);
        double distance = SOCCER_FIELD_TOP_LEFT.getDistanceTo(SOCCER_FIELD_BOTTOM_RIGHT);
        Location calculatedLocation = SOCCER_FIELD_TOP_LEFT.calculateNextLocation(distance / 1000, angle);
        int delta = (int) calculatedLocation.getDistanceTo(SOCCER_FIELD_BOTTOM_RIGHT);
        assertTrue(delta < 2);
    }

    //TODO make this work by improving accuracy of angle and distance calculation
    /*
    @Test
    public void getNewLocation_validLocations_correctLocation() throws Exception {
        double angle = NEW_YORK_CITY.getAngleTo(BERLIN);
        double distance = NEW_YORK_CITY.getDistanceTo(BERLIN);
        Location calculatedLocation = NEW_YORK_CITY.calculateNextLocation(distance / 1000, angle);
        System.out.println("calc. location: " + calculatedLocation);
        System.out.println("actu. location: " + BERLIN);
        int delta = (int) calculatedLocation.getDistanceTo(BERLIN);
        System.out.println("angle: " + angle + " | distance: " + distance + " | delta: " + delta);
        assertTrue(delta < 1);
    }
    */

}