package com.nexenio.bleindoorpositioningdemo.ui.beaconview.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioningdemo.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

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
        firstReferenceLocation = new Location(52.51239236816364, 13.390579996297987);
        secondReferenceLocation = new Location(52.51240825552749, 13.390821867681456);

        firstReferencePoint = new Point(953, 1830);
        secondReferencePoint = new Point(1926, 1830);

        backgroundImage = BitmapFactory.decodeResource(RuntimeEnvironment.application.getResources(), R.mipmap.map_view_background);

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
    public void getPoint_referenceLocation_referencePoint() {
        Point point = beaconMapBackground.getPoint(firstReferenceLocation);
        assertEquals(firstReferencePoint.x, point.x, 0.001);
        assertEquals(firstReferencePoint.y, point.y, 0.001);

        point = beaconMapBackground.getPoint(secondReferenceLocation);
        assertEquals(secondReferencePoint.x, point.x, 0.001);
        assertEquals(secondReferencePoint.y, point.y, 0.001);

        point = beaconMapBackground.getPoint(beaconMapBackground.getTopLeftLocation());
        assertEquals(beaconMapBackground.getTopLeftPoint().x, point.x, 0.001);
        assertEquals(beaconMapBackground.getTopLeftPoint().y, point.y, 0.001);

        point = beaconMapBackground.getPoint(beaconMapBackground.getBottomRightLocation());
        assertEquals(beaconMapBackground.getBottomRightPoint().x, point.x, 0.001);
        assertEquals(beaconMapBackground.getBottomRightPoint().y, point.y, 0.001);
    }

    @Test
    public void getMetersPerPixel() {
        double metersPerPixel = BeaconMapBackground.getMetersPerPixel(firstReferenceLocation, firstReferencePoint, secondReferenceLocation, secondReferencePoint);
        assertEquals(0.016919836401939392, metersPerPixel, 0.001);
        assertEquals(metersPerPixel, beaconMapBackground.getMetersPerPixel(), 0.001);
    }

    @Test
    public void getBearing() {
        double pointAngle = BeaconMapBackground.getAngle(firstReferencePoint, secondReferencePoint);
        System.out.println("Point angle: " + String.valueOf(pointAngle));

        double locationAngle = BeaconMapBackground.getAngle(firstReferenceLocation, secondReferenceLocation);
        System.out.println("Location angle: " + String.valueOf(locationAngle));

        double bearing = firstReferenceLocation.getAngleTo(secondReferenceLocation);
        System.out.println("First to second: " + String.valueOf(bearing));

        bearing = secondReferenceLocation.getAngleTo(firstReferenceLocation);
        System.out.println("Second to first: " + String.valueOf(bearing));

        bearing = BeaconMapBackground.getBearing(firstReferenceLocation, firstReferencePoint, secondReferenceLocation, secondReferencePoint);
        System.out.println("Background image bearing: " + String.valueOf(bearing));

        assertEquals(353.84, bearing, 0.001);
        assertEquals(bearing, beaconMapBackground.getBearing(), 0.001);
    }

    @Test
    public void getShiftedPoint() {
        Point referencePoint = new Point(0, 0);
        double distance = 10;

        double angle = 0;
        Point shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertEquals(0, shiftedPoint.x);
        assertEquals(-10, shiftedPoint.y);

        angle = 90;
        shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertEquals(10, shiftedPoint.x);
        assertEquals(0, shiftedPoint.y);

        angle = 180;
        shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertEquals(0, shiftedPoint.x);
        assertEquals(10, shiftedPoint.y);

        angle = 270;
        shiftedPoint = BeaconMapBackground.getShiftedPoint(referencePoint, distance, angle);
        assertEquals(-10, shiftedPoint.x);
        assertEquals(0, shiftedPoint.y);
    }

}