package com.nexenio.bleindoorpositioningdemo.ui.beaconview.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioningdemo.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void getLocation() {
        Location location = beaconMapBackground.getLocation(firstReferencePoint.x, firstReferencePoint.y);
        assertTrue(firstReferenceLocation.getDistanceTo(location) < 1);

        location = beaconMapBackground.getLocation(secondReferencePoint.x, secondReferencePoint.y);
        assertTrue(secondReferenceLocation.getDistanceTo(location) < 1);
    }

    @Test
    public void getPoint() {
        PointF point = beaconMapBackground.getPoint(firstReferenceLocation);
        assertEquals(firstReferencePoint.x, point.x, 0.001);
        assertEquals(firstReferencePoint.y, point.y, 0.001);

        point = beaconMapBackground.getPoint(secondReferenceLocation);
        assertEquals(secondReferencePoint.x, point.x, 0.001);
        assertEquals(secondReferencePoint.y, point.y, 0.001);
    }

    @Test
    public void getMetersPerPixel() {
        double metersPerPixel = BeaconMapBackground.getMetersPerPixel(firstReferenceLocation, firstReferencePoint, secondReferenceLocation, secondReferencePoint);
        assertEquals(0.016919836401939392, metersPerPixel, 0.001);
        assertEquals(metersPerPixel, beaconMapBackground.getMetersPerPixel(), 0.001);
    }

    @Test
    public void getBearing() {
        //double bearing = BeaconMapBackground.getBearing(firstReferenceLocation, firstReferencePoint, secondReferenceLocation, secondReferencePoint);

        double pointAngle = BeaconMapBackground.getAngle(firstReferencePoint, secondReferencePoint);
        System.out.println("Point angle: " + String.valueOf(pointAngle));

        double locationAngle = BeaconMapBackground.getAngle(firstReferenceLocation, secondReferenceLocation);
        System.out.println("Location angle: " + String.valueOf(pointAngle));

        double bearing = firstReferenceLocation.getAngleTo(secondReferenceLocation);
        System.out.println("First to second: " + String.valueOf(bearing));

        bearing = secondReferenceLocation.getAngleTo(firstReferenceLocation);
        System.out.println("Second to first: " + String.valueOf(bearing));

        bearing = BeaconMapBackground.getBearing(firstReferenceLocation, firstReferencePoint, secondReferenceLocation, secondReferencePoint);
        System.out.println("Background image bearing: " + String.valueOf(bearing));

        assertEquals(353.84, bearing, 0.001);
        assertEquals(bearing, beaconMapBackground.getBearing(), 0.001);
    }
}