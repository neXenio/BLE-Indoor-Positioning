package com.nexenio.bleindoorpositioning.location.distance;

import com.nexenio.bleindoorpositioning.location.LocationTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 17.11.17.
 */
public class DistanceCalculatorTest {

    @Test
    public void getDistanceBetween_soccerFieldCorners_correctFieldDimensions() throws Exception {
        final double tolerableDelta = 1;

        double calculatedFieldWidth = DistanceCalculator.getDistanceBetween(LocationTest.SOCCER_FIELD_TOP_LEFT, LocationTest.SOCCER_FIELD_TOP_RIGHT);
        assertEquals(LocationTest.SOCKER_FIELD_WIDTH, calculatedFieldWidth, tolerableDelta);

        calculatedFieldWidth = DistanceCalculator.getDistanceBetween(LocationTest.SOCCER_FIELD_BOTTOM_LEFT, LocationTest.SOCCER_FIELD_BOTTOM_RIGHT);
        assertEquals(LocationTest.SOCKER_FIELD_WIDTH, calculatedFieldWidth, tolerableDelta);

        double calculatedFieldHeight = DistanceCalculator.getDistanceBetween(LocationTest.SOCCER_FIELD_TOP_LEFT, LocationTest.SOCCER_FIELD_BOTTOM_LEFT);
        assertEquals(LocationTest.SOCKER_FIELD_HEIGHT, calculatedFieldHeight, tolerableDelta);

        calculatedFieldHeight = DistanceCalculator.getDistanceBetween(LocationTest.SOCCER_FIELD_TOP_RIGHT, LocationTest.SOCCER_FIELD_BOTTOM_RIGHT);
        assertEquals(LocationTest.SOCKER_FIELD_HEIGHT, calculatedFieldHeight, tolerableDelta);
    }

    @Test
    public void getDistanceBetween_newYorkBerlin_correctDistance() throws Exception {
        final double tolerableDelta = 5000;

        double calculatedDistance = DistanceCalculator.getDistanceBetween(LocationTest.NEW_YORK_CITY, LocationTest.BERLIN);
        assertEquals(LocationTest.DISTANCE_NYC_BERLIN, calculatedDistance, tolerableDelta);

        calculatedDistance = DistanceCalculator.getDistanceBetween(LocationTest.BERLIN, LocationTest.NEW_YORK_CITY);
        assertEquals(LocationTest.DISTANCE_NYC_BERLIN, calculatedDistance, tolerableDelta);
    }

    @Test
    public void getDistanceBetween_swappedLocations_sameDistance() throws Exception {
        double calculatedDistance1 = DistanceCalculator.getDistanceBetween(LocationTest.NEW_YORK_CITY, LocationTest.BERLIN);
        double calculatedDistance2 = DistanceCalculator.getDistanceBetween(LocationTest.BERLIN, LocationTest.NEW_YORK_CITY);
        assertEquals(calculatedDistance1, calculatedDistance2, 0);
    }

}