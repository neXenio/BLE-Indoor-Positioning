package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 17.11.17.
 */
public class LocationDistanceCalculatorTest {

    @Test
    public void calculateDistanceBetween_soccerFieldCorners_correctFieldDimensions() throws Exception {
        final double tolerableDelta = 1;

        double calculatedFieldWidth = LocationDistanceCalculator.calculateDistanceBetween(LocationTest.SOCCER_FIELD_TOP_LEFT, LocationTest.SOCCER_FIELD_TOP_RIGHT);
        assertEquals(LocationTest.SOCCER_FIELD_WIDTH, calculatedFieldWidth, tolerableDelta);

        calculatedFieldWidth = LocationDistanceCalculator.calculateDistanceBetween(LocationTest.SOCCER_FIELD_BOTTOM_LEFT, LocationTest.SOCCER_FIELD_BOTTOM_RIGHT);
        assertEquals(LocationTest.SOCCER_FIELD_WIDTH, calculatedFieldWidth, tolerableDelta);

        double calculatedFieldHeight = LocationDistanceCalculator.calculateDistanceBetween(LocationTest.SOCCER_FIELD_TOP_LEFT, LocationTest.SOCCER_FIELD_BOTTOM_LEFT);
        assertEquals(LocationTest.SOCCER_FIELD_HEIGHT, calculatedFieldHeight, tolerableDelta);

        calculatedFieldHeight = LocationDistanceCalculator.calculateDistanceBetween(LocationTest.SOCCER_FIELD_TOP_RIGHT, LocationTest.SOCCER_FIELD_BOTTOM_RIGHT);
        assertEquals(LocationTest.SOCCER_FIELD_HEIGHT, calculatedFieldHeight, tolerableDelta);
    }

    @Test
    public void calculateDistanceBetween_newYorkBerlin_correctDistance() throws Exception {
        final double tolerableDelta = 5000;

        double calculatedDistance = LocationDistanceCalculator.calculateDistanceBetween(LocationTest.NEW_YORK_CITY, LocationTest.BERLIN);
        assertEquals(LocationTest.DISTANCE_NYC_BERLIN, calculatedDistance, tolerableDelta);

        calculatedDistance = LocationDistanceCalculator.calculateDistanceBetween(LocationTest.BERLIN, LocationTest.NEW_YORK_CITY);
        assertEquals(LocationTest.DISTANCE_NYC_BERLIN, calculatedDistance, tolerableDelta);
    }

    @Test
    public void calculateDistanceBetween_swappedLocations_sameDistance() throws Exception {
        double calculatedDistance1 = LocationDistanceCalculator.calculateDistanceBetween(LocationTest.NEW_YORK_CITY, LocationTest.BERLIN);
        double calculatedDistance2 = LocationDistanceCalculator.calculateDistanceBetween(LocationTest.BERLIN, LocationTest.NEW_YORK_CITY);
        assertEquals(calculatedDistance1, calculatedDistance2, 0);
    }

    @Test
    public void calculateDistanceBetween_locationsWithElevation_correctDistance() throws Exception {
        Location lowLocation = new Location(0, 0, 0, 0);
        Location highLocation = new Location(0, 0, 0, 10);
        double calculatedDistance = LocationDistanceCalculator.calculateDistanceBetween(lowLocation, highLocation);
        assertEquals(0, calculatedDistance, 0);
    }

    @Test
    public void calculateDistanceBetween_locationsWithAltitude_correctDistance() throws Exception {
        Location lowLocation = new Location(0, 0, 0, 0);
        Location highLocation = new Location(0, 0, 10, 0);
        double calculatedDistance = LocationDistanceCalculator.calculateDistanceBetween(lowLocation, highLocation);
        assertEquals(10, calculatedDistance, 0);
    }

    @Test
    public void calculateDistanceBetween_locationsWithAltitudeAndElevation_correctDistance() throws Exception {
        Location lowLocation = new Location(0, 0, 0, 0);
        Location highLocation = new Location(0, 0, 10, 10);
        double calculatedDistance = LocationDistanceCalculator.calculateDistanceBetween(lowLocation, highLocation);
        assertEquals(10, calculatedDistance, 0);
    }

}