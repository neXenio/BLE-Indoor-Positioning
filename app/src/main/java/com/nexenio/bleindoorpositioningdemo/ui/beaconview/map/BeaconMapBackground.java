package com.nexenio.bleindoorpositioningdemo.ui.beaconview.map;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.projection.CanvasProjection;

public class BeaconMapBackground {

    private Bitmap imageBitmap;

    private float metersPerPixel;

    private float bearing;

    private Location topLeftLocation;

    private Location bottomRightLocation;

    private CanvasProjection projection = new CanvasProjection();

    private BeaconMapBackground(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
        projection.setCanvasWidth(imageBitmap.getWidth());
        projection.setCanvasHeight(imageBitmap.getHeight());
    }

    public Location getLocation(float x, float y) {
        //projection.get
    }

    public PointF getPoint(Location location) {

    }

    public static float getMetersPerPixel(Location firstReferenceLocation, Point firstReferencePoint, Location secondReferenceLocation, Point secondReferencePoint) {
        int deltaX = Math.abs(secondReferencePoint.x - firstReferencePoint.x);
        int deltaY = Math.abs(secondReferencePoint.y - firstReferencePoint.y);
        double distanceInPixels = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double distanceInMeters = firstReferenceLocation.getDistanceTo(secondReferenceLocation);
        return (float) (distanceInMeters / distanceInPixels);
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public float getMetersPerPixel() {
        return metersPerPixel;
    }

    public void setMetersPerPixel(float metersPerPixel) {
        this.metersPerPixel = metersPerPixel;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public Location getTopLeftLocation() {
        return topLeftLocation;
    }

    public void setTopLeftLocation(Location topLeftLocation) {
        this.topLeftLocation = topLeftLocation;
        projection.setTopLeftLocation(topLeftLocation);
    }

    public Location getBottomRightLocation() {
        return bottomRightLocation;
    }

    public void setBottomRightLocation(Location bottomRightLocation) {
        this.bottomRightLocation = bottomRightLocation;
        projection.setBottomRightLocation(bottomRightLocation);
    }

    public static class Builder {

        private BeaconMapBackground beaconMapBackground;

        private Location firstReferenceLocation;

        private Point firstReferencePoint;

        private Location secondReferenceLocation;

        private Point secondReferencePoint;

        public static Builder from(@NonNull Bitmap imageBitmap) {
            Builder builder = new Builder();
            builder.beaconMapBackground = new BeaconMapBackground(imageBitmap);
            return builder;
        }

        public Builder withBearing(float bearing) {
            beaconMapBackground.setBearing(bearing);
            return this;
        }

        public Builder withFirstReferenceLocation(Location location, int x, int y) {
            firstReferenceLocation = location;
            firstReferencePoint = new Point(x, y);
            return this;
        }

        public Builder withSecondReferenceLocation(Location location, int x, int y) {
            secondReferenceLocation = location;
            secondReferencePoint = new Point(x, y);
            return this;
        }

        public BeaconMapBackground build() {
            // meters per pixel
            if (firstReferenceLocation == null || secondReferenceLocation == null) {
                throw new IllegalArgumentException("You have to specify two reference locations first.");
            }
            float metersPerPixel = getMetersPerPixel(firstReferenceLocation, firstReferencePoint, secondReferenceLocation, secondReferencePoint);
            beaconMapBackground.setMetersPerPixel(metersPerPixel);

            return beaconMapBackground;
        }
    }

}
