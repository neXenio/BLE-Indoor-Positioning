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
    public static final int DISTANCE_NYC_BERLIN = 6381 * 1000;

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

}