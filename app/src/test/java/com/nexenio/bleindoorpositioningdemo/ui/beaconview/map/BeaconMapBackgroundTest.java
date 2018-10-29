package com.nexenio.bleindoorpositioningdemo.ui.beaconview.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class BeaconMapBackgroundTest {

    private Location firstReferenceLocation;
    private Location secondReferenceLocation;

    private Point firstReferencePoint;
    private Point secondReferencePoint;

    private Bitmap backgroundImage;
    private BeaconMapBackground.Builder beaconMapBackgroundBuilder;
    private BeaconMapBackground beaconMapBackground;

    @Before
    public void setUp() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("map_view_background.png");
        backgroundImage = BitmapFactory.decodeStream(inputStream);

        firstReferenceLocation = new Location(52.51239236816364, 13.390579996297987);
        secondReferenceLocation = new Location(52.51240825552749, 13.390821867681456);

        firstReferencePoint = new Point(953, 1830);
        secondReferencePoint = new Point(1926, 1830);

        beaconMapBackgroundBuilder = BeaconMapBackground.Builder.from(backgroundImage)
                .withFirstReferenceLocation(firstReferenceLocation, firstReferencePoint)
                .withSecondReferenceLocation(secondReferenceLocation, secondReferencePoint);

        beaconMapBackground = beaconMapBackgroundBuilder.build();
    }

    @Test
    public void getLocation_referencePoint_referenceLocation() {
        Location location = beaconMapBackground.getLocation(firstReferencePoint);
        assertEquals(0, firstReferenceLocation.getDistanceTo(location), 0.1);

        location = beaconMapBackground.getLocation(secondReferencePoint);
        assertEquals(0, secondReferenceLocation.getDistanceTo(location), 0.1);

        location = beaconMapBackground.getLocation(beaconMapBackground.getTopLeftPoint());
        assertEquals(0, beaconMapBackground.getTopLeftLocation().getDistanceTo(location), 0.1);

        location = beaconMapBackground.getLocation(beaconMapBackground.getBottomRightPoint());
        assertEquals(0, beaconMapBackground.getBottomRightLocation().getDistanceTo(location), 0.1);
    }

    @Test
    public void getLocation_edgePoints_edgeLocations() {
        double distanceInPixels;
        double distanceInMeters;

        distanceInPixels = BeaconMapBackground.getPixelDistance(firstReferencePoint, secondReferencePoint);
        distanceInMeters = beaconMapBackground.getMetersPerPixel() * distanceInPixels;
        assertEquals(distanceInMeters, firstReferenceLocation.getDistanceTo(secondReferenceLocation), 0.1);

        distanceInPixels = BeaconMapBackground.getPixelDistance(beaconMapBackground.getTopLeftPoint(), beaconMapBackground.getBottomRightPoint());
        distanceInMeters = beaconMapBackground.getMetersPerPixel() * distanceInPixels;
        assertEquals(distanceInMeters, beaconMapBackground.getTopLeftLocation().getDistanceTo(beaconMapBackground.getBottomRightLocation()), 0.1);
    }

    @Test
    public void getPoint_referenceLocation_referencePoint() {
        Point point;

        point = beaconMapBackground.getPoint(beaconMapBackground.getTopLeftLocation());
        assertPointEquals(beaconMapBackground.getTopLeftPoint(), point, 1);

        point = beaconMapBackground.getPoint(beaconMapBackground.getBottomRightLocation());
        assertPointEquals(beaconMapBackground.getBottomRightPoint(), point, 1);

        point = beaconMapBackground.getPoint(firstReferenceLocation);
        assertPointEquals(firstReferencePoint, point, 1);

        point = beaconMapBackground.getPoint(secondReferenceLocation);
        assertPointEquals(secondReferencePoint, point, 1);
    }

    @Test
    public void getPoint_differentReferenceLocations_samePoint() {
        Location centerLocation = LocationUtil.calculateMeanLocation(
                beaconMapBackground.getTopLeftLocation(),
                beaconMapBackground.getBottomRightLocation()
        );

        Point centerPointUsingTopLeft = BeaconMapBackground.getPoint(
                centerLocation,
                beaconMapBackground.getTopLeftLocation(),
                beaconMapBackground.getTopLeftPoint(),
                beaconMapBackground.getMetersPerPixel(),
                beaconMapBackground.getBearing()
        );

        Point centerPointUsingBottomRight = BeaconMapBackground.getPoint(
                centerLocation,
                beaconMapBackground.getBottomRightLocation(),
                beaconMapBackground.getBottomRightPoint(),
                beaconMapBackground.getMetersPerPixel(),
                beaconMapBackground.getBearing()
        );

        Point centerPointUsingFirstReference = BeaconMapBackground.getPoint(
                centerLocation,
                firstReferenceLocation,
                firstReferencePoint,
                beaconMapBackground.getMetersPerPixel(),
                beaconMapBackground.getBearing()
        );

        Point centerPointUsingSecondReference = BeaconMapBackground.getPoint(
                centerLocation,
                secondReferenceLocation,
                secondReferencePoint,
                beaconMapBackground.getMetersPerPixel(),
                beaconMapBackground.getBearing()
        );

        Point expectedPoint = new Point(
                backgroundImage.getWidth() / 2,
                backgroundImage.getHeight() / 2
        );

        assertPointEquals(expectedPoint, centerPointUsingTopLeft, 0);
        assertPointEquals(expectedPoint, centerPointUsingBottomRight, 0);
        assertPointEquals(expectedPoint, centerPointUsingFirstReference, 0);
        assertPointEquals(expectedPoint, centerPointUsingSecondReference, 0);
    }

    @Test
    public void getMetersPerPixel() {
        double metersPerPixel = BeaconMapBackground.getMetersPerPixel(firstReferenceLocation, firstReferencePoint, secondReferenceLocation, secondReferencePoint);
        assertEquals(0.016919836401939392, metersPerPixel, 0.001);
        assertEquals(metersPerPixel, beaconMapBackground.getMetersPerPixel(), 0.001);
    }

    @Test
    public void getBearing() {
        double bearing = BeaconMapBackground.getBearing(firstReferenceLocation, firstReferencePoint, secondReferenceLocation, secondReferencePoint);
        assertEquals(353.84, bearing, 0.001);
        assertEquals(bearing, beaconMapBackground.getBearing(), 0.001);
    }

    @Test
    public void getShiftedPoint_zeroReference_correctPoints() {
        Point referencePoint;
        Point shiftedPoint;
        double distance;
        double angle;

        referencePoint = new Point(0, 0);
        distance = 10;
        angle = 0;
        shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertPointEquals(new Point(0, -10), shiftedPoint, 0);

        angle = 90;
        shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertPointEquals(new Point(10, 0), shiftedPoint, 0);

        angle = 180;
        shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertPointEquals(new Point(0, 10), shiftedPoint, 0);

        angle = 270;
        shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertPointEquals(new Point(-10, 0), shiftedPoint, 0);
    }

    @Test
    public void getShiftedPoint_positiveReference_correctPoints() {
        Point referencePoint;
        Point shiftedPoint;
        double distance;
        double angle;

        referencePoint = new Point(100, 100);
        distance = 50;
        angle = 0;
        shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertPointEquals(new Point(100, 50), shiftedPoint, 0);

        angle = 90;
        shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertPointEquals(new Point(150, 100), shiftedPoint, 0);

        angle = 180;
        shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertPointEquals(new Point(100, 150), shiftedPoint, 0);

        angle = 270;
        shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertPointEquals(new Point(50, 100), shiftedPoint, 0);
    }

    @Test
    public void getTopLeftLocation() {
        Location topLeftLocation = new Location(52.512653658536856, 13.390293996004692);
        assertEquals(0, topLeftLocation.getDistanceTo(beaconMapBackground.getTopLeftLocation()), 0.001);
    }

    @Test
    public void getBottomRightLocation() {
        Location bottomRightLocation = new Location(52.512295922346524, 13.391304257299764);
        assertEquals(0, bottomRightLocation.getDistanceTo(beaconMapBackground.getBottomRightLocation()), 0.001);
    }

    @Test
    public void getTopLeftPoint() {
        assertPointEquals(new Point(0, 0), beaconMapBackground.getTopLeftPoint(), 0);
    }

    @Test
    public void getBottomRightPoint() {
        assertPointEquals(new Point(backgroundImage.getWidth(), backgroundImage.getHeight()), beaconMapBackground.getBottomRightPoint(), 0);
    }

    private static void assertPointEquals(Point expectedPoint, Point actualPoint, double delta) {
        double distance = BeaconMapBackground.getPixelDistance(expectedPoint, actualPoint);
        assertEquals("Distance from expected point: " + expectedPoint + " to actual point: " + actualPoint + " is " + distance, 0, distance, delta);
    }

}