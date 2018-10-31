package com.nexenio.bleindoorpositioning.location;

import com.nexenio.bleindoorpositioning.location.distance.LocationDistanceCalculator;

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
    public void constructor_location_setsAllFields() {
        Location originalLocation = new Location(52.5200066, 13.404954);
        originalLocation.setElevation(2.5);
        originalLocation.setAltitude(123);
        originalLocation.setAccuracy(5);

        Location newLocation = new Location(originalLocation);
        assertEquals(originalLocation.getLatitude(), newLocation.getLatitude(), 0.1);
        assertEquals(originalLocation.getLongitude(), newLocation.getLongitude(), 0.1);
        assertEquals(originalLocation.getAltitude(), newLocation.getAltitude(), 0.1);
        assertEquals(originalLocation.getElevation(), newLocation.getElevation(), 0.1);
        assertEquals(originalLocation.getAccuracy(), newLocation.getAccuracy(), 0.1);
        assertEquals(originalLocation.getTimestamp(), newLocation.getTimestamp(), 0.1);
        assertEquals(originalLocation.generateGoogleMapsUri(), newLocation.generateGoogleMapsUri());
    }

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

    @Test
    public void shift_initialLocation_correctLocation() throws Exception {
        double distance = 10;
        double angle = 180;
        Location expectedLocation = computeOffset(BERLIN, distance, angle);
        Location actualLocation = BERLIN.getShiftedLocation(distance, angle);
        double delta = expectedLocation.getDistanceTo(actualLocation);
        assertEquals(0, delta, 2);

        expectedLocation = computeOffset(NEW_YORK_CITY, distance, angle);
        actualLocation = NEW_YORK_CITY.getShiftedLocation(distance, angle);
        double deltaLatitude = Math.abs(expectedLocation.getLatitude() - actualLocation.getLatitude());
        double deltaLongitude = Math.abs(expectedLocation.getLongitude() - actualLocation.getLongitude());
        assertEquals(deltaLatitude, 0, 2);
        assertEquals(deltaLongitude, 0, 2);

        distance = 30;
        angle = 80;
        expectedLocation = computeOffset(NEW_YORK_CITY, distance, angle);
        actualLocation = NEW_YORK_CITY.getShiftedLocation(distance, angle);
        deltaLatitude = Math.abs(expectedLocation.getLatitude() - actualLocation.getLatitude());
        deltaLongitude = Math.abs(expectedLocation.getLongitude() - actualLocation.getLongitude());
        assertEquals(deltaLatitude, 0, 2);
        assertEquals(deltaLongitude, 0, 2);
    }

    /**
     * Taken from <a href="https://github.com/googlemaps/android-maps-utils/blob/master/library/src/com/google/maps/android/SphericalUtil.java">Android
     * Maps Util</a>
     *
     * Returns the LatLng resulting from moving a distance from an origin in the specified heading
     * (expressed in degrees clockwise from north).
     *
     * @param from     The LatLng from which to start.
     * @param distance The distance to travel.
     * @param heading  The heading in degrees clockwise from north.
     */
    public static Location computeOffset(Location from, double distance, double heading) {
        // convert to meters
        distance /= LocationDistanceCalculator.EARTH_RADIUS * 1000;
        heading = Math.toRadians(heading);
        double fromLat = Math.toRadians(from.getLatitude());
        double fromLng = Math.toRadians(from.getLongitude());
        double cosDistance = Math.cos(distance);
        double sinDistance = Math.sin(distance);
        double sinFromLat = Math.sin(fromLat);
        double cosFromLat = Math.cos(fromLat);
        double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * Math.cos(heading);
        double dLng = Math.atan2(
                sinDistance * cosFromLat * Math.sin(heading),
                cosDistance - sinFromLat * sinLat);
        return new Location(Math.toDegrees(Math.asin(sinLat)), Math.toDegrees(fromLng + dLng));
    }
}