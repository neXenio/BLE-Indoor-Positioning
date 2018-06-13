package com.nexenio.bleindoorpositioningdemo.ui.beaconview.map;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.NonNull;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.projection.CanvasProjection;

public class BeaconMapBackground {

    private Bitmap imageBitmap;

    private double metersPerPixel;

    private double bearing;

    private Location topLeftLocation;
    private Location bottomRightLocation;

    private Point topLeftPoint;
    private Point bottomRightPoint;

    private CanvasProjection projection = new CanvasProjection();

    private BeaconMapBackground(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
        projection.setCanvasWidth(imageBitmap.getWidth());
        projection.setCanvasHeight(imageBitmap.getHeight());
        topLeftPoint = new Point(0, 0);
        bottomRightPoint = new Point(imageBitmap.getWidth(), imageBitmap.getHeight());
    }

    public Location getLocation(Point point) {
        return getLocation(point, topLeftLocation, topLeftPoint, metersPerPixel, bearing);
    }

    public Point getPoint(Location location) {
        return getPoint(location, topLeftLocation, topLeftPoint, metersPerPixel, bearing);
    }

    public static float getMetersPerPixel(Location firstReferenceLocation, Point firstReferencePoint, Location secondReferenceLocation, Point secondReferencePoint) {
        double distanceInPixels = getPixelDistance(firstReferencePoint, secondReferencePoint);
        double distanceInMeters = firstReferenceLocation.getDistanceTo(secondReferenceLocation);
        return (float) (distanceInMeters / distanceInPixels);
    }

    public static double getBearing(Location firstReferenceLocation, Point firstReferencePoint, Location secondReferenceLocation, Point secondReferencePoint) {
        double locationAngle = getAngle(firstReferenceLocation, secondReferenceLocation);
        double pointAngle = getAngle(firstReferencePoint, secondReferencePoint);
        return ((locationAngle - pointAngle) + 360) % 360;
    }

    public static double getAngle(Point firstPoint, Point secondPoint) {
        double angle = Math.atan2(secondPoint.y - firstPoint.y, secondPoint.x - firstPoint.x) * 180 / Math.PI;
        return (angle + 90) % 360;
    }

    public static double getAngle(Location firstLocation, Location secondLocation) {
        return firstLocation.getAngleTo(secondLocation);
    }

    public static double getPixelDistance(Point firstPoint, Point secondPoint) {
        int deltaX = Math.abs(secondPoint.x - firstPoint.x);
        int deltaY = Math.abs(secondPoint.y - firstPoint.y);
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    public static Location getLocation(Point point, Location referenceLocation, Point referencePoint, double metersPerPixel, double bearing) {
        double distance = metersPerPixel * getPixelDistance(referencePoint, point);
        double angle = (getAngle(referencePoint, point) + bearing + 360) % 360;
        return referenceLocation.getShiftedLocation(distance, angle);
    }

    public static Point getPoint(Location location, Location referenceLocation, Point referencePoint, double metersPerPixel, double bearing) {
        double distance = location.getDistanceTo(referenceLocation) / metersPerPixel;
        double angle = (getAngle(location, referenceLocation) + bearing + 360) % 360;
        return getShiftedPoint(referencePoint, distance, angle);
    }

    public static Point getShiftedPoint(Point referencePoint, double distance, double bearing) {
        double angleInRadians = Math.toRadians(bearing + 90);
        return new Point(
                -(int) (referencePoint.x + (distance * Math.cos(angleInRadians))),
                -(int) (referencePoint.y + (distance * Math.sin(angleInRadians)))
        );
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public double getMetersPerPixel() {
        return metersPerPixel;
    }

    public void setMetersPerPixel(double metersPerPixel) {
        this.metersPerPixel = metersPerPixel;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
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

    public Point getTopLeftPoint() {
        return topLeftPoint;
    }

    public void setTopLeftPoint(Point topLeftPoint) {
        this.topLeftPoint = topLeftPoint;
    }

    public Point getBottomRightPoint() {
        return bottomRightPoint;
    }

    public void setBottomRightPoint(Point bottomRightPoint) {
        this.bottomRightPoint = bottomRightPoint;
    }

    public static class Builder {

        private BeaconMapBackground beaconMapBackground;

        private Location firstReferenceLocation;

        private Point firstReferencePoint;

        private Location secondReferenceLocation;

        private Point secondReferencePoint;

        private Builder() {
        }

        public static Builder from(@NonNull Bitmap imageBitmap) {
            Builder builder = new Builder();
            builder.beaconMapBackground = new BeaconMapBackground(imageBitmap);
            return builder;
        }

        public Builder withBearing(float bearing) {
            beaconMapBackground.setBearing(bearing);
            return this;
        }

        public Builder withFirstReferenceLocation(Location location, Point point) {
            firstReferenceLocation = location;
            firstReferencePoint = point;
            return this;
        }

        public Builder withSecondReferenceLocation(Location location, Point point) {
            secondReferenceLocation = location;
            secondReferencePoint = point;
            return this;
        }

        public BeaconMapBackground build() {
            // meters per pixel
            if (firstReferenceLocation == null || secondReferenceLocation == null) {
                throw new IllegalArgumentException("You have to specify two reference locations first.");
            }
            double metersPerPixel = getMetersPerPixel(firstReferenceLocation, firstReferencePoint, secondReferenceLocation, secondReferencePoint);
            beaconMapBackground.setMetersPerPixel(metersPerPixel);

            // bearing
            double bearing = getBearing(firstReferenceLocation, firstReferencePoint, secondReferenceLocation, secondReferencePoint);
            beaconMapBackground.setBearing(bearing);

            // top left location
            Location topLeftLocation = getLocation(
                    beaconMapBackground.getTopLeftPoint(),
                    firstReferenceLocation,
                    firstReferencePoint,
                    metersPerPixel,
                    bearing
            );
            beaconMapBackground.setTopLeftLocation(topLeftLocation);

            // bottom right location
            Location bottomRightLocation = getLocation(
                    beaconMapBackground.getBottomRightPoint(),
                    firstReferenceLocation,
                    firstReferencePoint,
                    metersPerPixel,
                    bearing
            );
            beaconMapBackground.setBottomRightLocation(bottomRightLocation);

            return beaconMapBackground;
        }
    }

}
