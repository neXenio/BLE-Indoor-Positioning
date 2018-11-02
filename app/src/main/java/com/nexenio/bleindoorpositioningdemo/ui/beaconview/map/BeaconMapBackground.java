package com.nexenio.bleindoorpositioningdemo.ui.beaconview.map;

import android.graphics.Bitmap;
import android.graphics.Point;
import androidx.annotation.NonNull;

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

    public Location getLocation(@NonNull Point point) {
        return getLocation(point, topLeftLocation, topLeftPoint, metersPerPixel, bearing);
    }

    public Point getPoint(@NonNull Location location) {
        return getPoint(location, topLeftLocation, topLeftPoint, metersPerPixel, bearing);
    }

    public static float getMetersPerPixel(@NonNull Location firstReferenceLocation, @NonNull Point firstReferencePoint, @NonNull Location secondReferenceLocation, @NonNull Point secondReferencePoint) {
        double distanceInPixels = getPixelDistance(firstReferencePoint, secondReferencePoint);
        double distanceInMeters = firstReferenceLocation.getDistanceTo(secondReferenceLocation);
        if (distanceInPixels == 0) {
            throw new IllegalArgumentException("Reference points must be distinct.");
        }
        return (float) (distanceInMeters / distanceInPixels);
    }

    public static double getBearing(@NonNull Location firstReferenceLocation, @NonNull Point firstReferencePoint, @NonNull Location secondReferenceLocation, @NonNull Point secondReferencePoint) {
        double locationBearing = getBearing(firstReferenceLocation, secondReferenceLocation);
        double pointBearing = getBearing(firstReferencePoint, secondReferencePoint);
        return ((locationBearing - pointBearing) + 360) % 360;
    }

    public static double getBearing(@NonNull Point firstPoint, @NonNull Point secondPoint) {
        double angle = Math.atan2(secondPoint.y - firstPoint.y, secondPoint.x - firstPoint.x) * 180 / Math.PI;
        return (angle + 90) % 360;
    }

    public static double getBearing(@NonNull Location firstLocation, @NonNull Location secondLocation) {
        return firstLocation.getAngleTo(secondLocation);
    }

    public static double getPixelDistance(@NonNull Point firstPoint, @NonNull Point secondPoint) {
        int deltaX = Math.abs(secondPoint.x - firstPoint.x);
        int deltaY = Math.abs(secondPoint.y - firstPoint.y);
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    public static Location getLocation(@NonNull Point point, @NonNull Location referenceLocation, @NonNull Point referencePoint, double metersPerPixel, double backgroundBearing) {
        double distance = metersPerPixel * getPixelDistance(referencePoint, point);
        double bearing = (getBearing(referencePoint, point) + backgroundBearing + 360) % 360;
        return referenceLocation.getShiftedLocation(distance, bearing);
    }

    public static Point getPoint(@NonNull Location location, @NonNull Location referenceLocation, @NonNull Point referencePoint, double metersPerPixel, double backgroundBearing) {
        double distanceInMeters = location.getDistanceTo(referenceLocation);
        double distanceInPixels = distanceInMeters / metersPerPixel;
        double locationBearing = getBearing(referenceLocation, location);
        double bearing = (locationBearing - backgroundBearing + 360) % 360;
        return getShiftedPoint(referencePoint, distanceInPixels, bearing);
    }

    public static Point getShiftedPoint(@NonNull Point referencePoint, double distanceInPixels, double bearing) {
        double angleInRadians = Math.toRadians(bearing + 90);
        return new Point(
                (int) (referencePoint.x - (distanceInPixels * Math.cos(angleInRadians))),
                (int) (referencePoint.y - (distanceInPixels * Math.sin(angleInRadians)))
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

    public void setTopLeftLocation(@NonNull Location topLeftLocation) {
        this.topLeftLocation = topLeftLocation;
        projection.setTopLeftLocation(topLeftLocation);
    }

    public Location getBottomRightLocation() {
        return bottomRightLocation;
    }

    public void setBottomRightLocation(@NonNull Location bottomRightLocation) {
        this.bottomRightLocation = bottomRightLocation;
        projection.setBottomRightLocation(bottomRightLocation);
    }

    public Point getTopLeftPoint() {
        return topLeftPoint;
    }

    public void setTopLeftPoint(@NonNull Point topLeftPoint) {
        this.topLeftPoint = topLeftPoint;
    }

    public Point getBottomRightPoint() {
        return bottomRightPoint;
    }

    public void setBottomRightPoint(@NonNull Point bottomRightPoint) {
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

        public Builder withFirstReferenceLocation(@NonNull Location location, @NonNull Point point) {
            firstReferenceLocation = location;
            firstReferencePoint = point;
            return this;
        }

        public Builder withSecondReferenceLocation(@NonNull Location location, @NonNull Point point) {
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
