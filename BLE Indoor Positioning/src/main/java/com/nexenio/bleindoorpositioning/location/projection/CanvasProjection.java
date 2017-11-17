package com.nexenio.bleindoorpositioning.location.projection;

import com.nexenio.bleindoorpositioning.location.Location;

/**
 * Created by steppschuh on 16.11.17.
 */

public class CanvasProjection {

    private int canvasWidth = 1000;
    private int canvasHeight = 1000;

    private Location topLeftLocation;
    private Location bottomRightLocation;

    public CanvasProjection() {
    }

    public double getWidthFromLongitude(double longitude) {
        return getWidthFromLongitude(longitude, canvasWidth);
    }

    public static double getWidthFromLongitude(double longitude, int canvasWidth) {
        return ((canvasWidth / (float) 360) * (180 + longitude));
    }

    public double getHeightFromLatitude(double latitude) {
        return getHeightFromLatitude(latitude, canvasHeight);
    }

    public static double getHeightFromLatitude(double latitude, int canvasHeight) {
        return ((canvasHeight / (float) 180) * (90 - latitude));
    }

    /*
        Getter & Setter
     */

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public Location getTopLeftLocation() {
        return topLeftLocation;
    }

    public void setTopLeftLocation(Location topLeftLocation) {
        this.topLeftLocation = topLeftLocation;
    }

    public Location getBottomRightLocation() {
        return bottomRightLocation;
    }

    public void setBottomRightLocation(Location bottomRightLocation) {
        this.bottomRightLocation = bottomRightLocation;
    }
}
