package com.nexenio.bleindoorpositioning.location.projection;

import com.nexenio.bleindoorpositioning.location.Location;

/**
 * The equirectangular projection (also called the equidistant cylindrical projection, geographic
 * projection) maps meridians to vertical straight lines of constant spacing (for meridional
 * intervals of constant spacing), and circles of latitude to horizontal straight lines of constant
 * spacing (for constant intervals of parallels).
 *
 * It has become a standard for global raster datasets, such as Celestia and NASA World Wind,
 * because of the particularly simple relationship between the position of an image pixel on the map
 * and its corresponding geographic location on Earth.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Equirectangular_projection">Wikipedia</a>
 */

public class EquirectangularProjection {

    private int canvasWidth = 1000;
    private int canvasHeight = 1000;

    private Location topLeftLocation;
    private Location bottomRightLocation;

    public EquirectangularProjection() {
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
