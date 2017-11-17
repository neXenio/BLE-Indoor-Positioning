package com.nexenio.bleindoorpositioning.location;

import org.junit.Test;

/**
 * Created by steppschuh on 17.11.17.
 */
public class LocationTest {

    /*
        Olympiastadion Berlin
        https://goo.gl/maps/xSx4gBEpHB42
     */
    public static final Location SOCCER_FIELD_TOP_LEFT = new Location(52.514890, 13.238694);
    public static final Location SOCCER_FIELD_TOP_RIGHT = new Location(52.515024, 13.240219);
    public static final Location SOCCER_FIELD_BOTTOM_LEFT = new Location(52.514285, 13.238834);
    public static final Location SOCCER_FIELD_BOTTOM_RIGHT = new Location(52.514421, 13.240373);
    public static final Location SOCCER_FIELD_CENTER = new Location(52.514658, 13.239525);
    public static final int SOCKER_FIELD_WIDTH = 105;
    public static final int SOCKER_FIELD_HEIGHT = 68;

    public static final Location NEW_YORK_CITY = new Location(40.7127753, -74.0059728);
    public static final Location BERLIN = new Location(52.5200066, 13.404954);
    public static final int DISTANCE_NYC_BERLIN = 6381 * 1000;

    @Test
    public void getDistanceTo() throws Exception {
    }

}