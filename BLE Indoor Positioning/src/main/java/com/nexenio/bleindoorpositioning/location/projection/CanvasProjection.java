package com.nexenio.bleindoorpositioning.location.projection;

import com.nexenio.bleindoorpositioning.location.Location;

/**
 * Created by steppschuh on 23.11.17.
 */

public class CanvasProjection {

    private EquirectangularProjection geographicProjection;

    private Location topLeftLocation;
    private Location bottomRightLocation;

    private float paddingFactor = 0.1f;

    private float canvasWidth;
    private float canvasHeight;

    private double projectedCanvasWidth;
    private double projectedCanvasHeight;

    private double offsetOriginWidth;
    private double offsetOriginHeight;

    private double metersPerProjectionUnit; // how many meters does on projection unit represent?
    private double metersPerCanvasUnit; // how many meters does one pixel represent?

    private boolean shouldUpdateMapping;

    public CanvasProjection() {
        geographicProjection = new EquirectangularProjection();
    }

    public float getXFromLocation(Location location) {
        updateMappingIfRequired();
        double locationWidth = geographicProjection.getWidthFromLongitude(location.getLongitude());
        double projectedLocationWidth = locationWidth - offsetOriginWidth;
        double x = (projectedLocationWidth * canvasWidth) / projectedCanvasWidth;
        return (float) x;
    }

    public float getYFromLocation(Location location) {
        updateMappingIfRequired();
        double locationHeight = geographicProjection.getHeightFromLatitude(location.getLatitude());
        double projectedLocationHeight = locationHeight - offsetOriginHeight;
        double y = (projectedLocationHeight * canvasHeight) / projectedCanvasHeight;
        return (float) y;
    }

    public double getCanvasUnitsFromMeters(double meters) {
        return meters / metersPerCanvasUnit;
    }

    public double getMetersFromCanvasUnits(double canvasUnits) {
        return canvasUnits * metersPerCanvasUnit;
    }

    public double getProjectionUnitsFromMeters(double meters) {
        return meters / metersPerProjectionUnit;
    }

    public double getMetersFromProjectionUnits(double projectionUnits) {
        return projectionUnits * metersPerProjectionUnit;
    }

    private void updateMappingIfRequired() {
        if (shouldUpdateMapping) {
            updateMapping();
        }
    }

    private void updateMapping() {
        // check if the canvas has been initialized
        if (canvasWidth == 0 || canvasHeight == 0) {
            return;
        }

        // check if edge locations are available
        if (topLeftLocation == null || bottomRightLocation == null) {
            return;
        }

        // get the projected width and height for the top left and bottom right location
        double projectedTopLeftLocationWidth = geographicProjection.getWidthFromLongitude(topLeftLocation.getLongitude());
        double projectedTopLeftLocationHeight = geographicProjection.getHeightFromLatitude(topLeftLocation.getLatitude());
        double projectedBottomRightLocationWidth = geographicProjection.getWidthFromLongitude(bottomRightLocation.getLongitude());
        double projectedBottomRightLocationHeight = geographicProjection.getHeightFromLatitude(bottomRightLocation.getLatitude());

        // get the minimum width and height that should be mapped in order to
        // display the top left and bottom right location on the canvas
        double projectedMinimumWidth = projectedBottomRightLocationWidth - projectedTopLeftLocationWidth;
        double projectedMinimumHeight = projectedBottomRightLocationHeight - projectedTopLeftLocationHeight;
        double projectedMinimumDiagonal = Math.sqrt((projectedMinimumWidth * projectedMinimumWidth) + (projectedMinimumHeight * projectedMinimumHeight));

        // add some padding
        double projectedPadding = 2 * (Math.max(projectedMinimumWidth, projectedMinimumHeight) * paddingFactor);
        projectedMinimumWidth += projectedPadding;
        projectedMinimumHeight += projectedPadding;

        // get the mapped width and height equivalent to the pixel dimensions of the canvas
        projectedCanvasWidth = projectedMinimumWidth;
        projectedCanvasHeight = projectedMinimumHeight;
        float canvasAspectRatio = canvasWidth / canvasHeight;
        if (canvasAspectRatio > (projectedMinimumWidth / projectedMinimumHeight)) {
            projectedCanvasWidth = projectedMinimumHeight * canvasAspectRatio;
        } else {
            projectedCanvasHeight = projectedMinimumWidth / canvasAspectRatio;
        }

        // get the distances that the diagonals represent in order to
        // get the meters per canvas- & projection unit
        double canvasDiagonal = Math.sqrt((canvasWidth * canvasWidth) + (canvasHeight * canvasHeight));
        double projectedCanvasDiagonal = Math.sqrt((projectedCanvasWidth * projectedCanvasWidth) + (projectedCanvasHeight * projectedCanvasHeight));
        double distanceOfProjectedMinimumDiagonal = topLeftLocation.getDistanceTo(bottomRightLocation);
        double distanceOfProjectedCanvasDiagonal = (projectedCanvasDiagonal * distanceOfProjectedMinimumDiagonal) / projectedMinimumDiagonal;
        metersPerProjectionUnit = distanceOfProjectedCanvasDiagonal / projectedCanvasDiagonal;
        metersPerCanvasUnit = distanceOfProjectedCanvasDiagonal / canvasDiagonal;

        // get the offsets that should be applied to mappings in order
        // to center the locations on the canvas
        double offsetWidth = (projectedCanvasWidth - projectedMinimumWidth + projectedPadding) / 2;
        double offsetHeight = (projectedCanvasHeight - projectedMinimumHeight + projectedPadding) / 2;

        // get the origin width and height (equivalent to the canvas origin 0,0) including
        // the calculated mapping offset
        offsetOriginWidth = projectedTopLeftLocationWidth - offsetWidth;
        offsetOriginHeight = projectedTopLeftLocationHeight - offsetHeight;
    }

    /*
        Getter & Setter
     */

    public EquirectangularProjection getGeographicProjection() {
        return geographicProjection;
    }

    public void setGeographicProjection(EquirectangularProjection geographicProjection) {
        this.geographicProjection = geographicProjection;
        shouldUpdateMapping = true;
    }

    public Location getTopLeftLocation() {
        return topLeftLocation;
    }

    public void setTopLeftLocation(Location topLeftLocation) {
        this.topLeftLocation = topLeftLocation;
        shouldUpdateMapping = true;
    }

    public Location getBottomRightLocation() {
        return bottomRightLocation;
    }

    public void setBottomRightLocation(Location bottomRightLocation) {
        this.bottomRightLocation = bottomRightLocation;
        shouldUpdateMapping = true;
    }

    public float getPaddingFactor() {
        return paddingFactor;
    }

    public void setPaddingFactor(float paddingFactor) {
        this.paddingFactor = paddingFactor;
        shouldUpdateMapping = true;
    }

    public float getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(float canvasWidth) {
        this.canvasWidth = canvasWidth;
        shouldUpdateMapping = true;
    }

    public float getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(float canvasHeight) {
        this.canvasHeight = canvasHeight;
        shouldUpdateMapping = true;
    }

    public double getProjectedCanvasWidth() {
        return projectedCanvasWidth;
    }

    public double getProjectedCanvasHeight() {
        return projectedCanvasHeight;
    }

    public double getOffsetOriginWidth() {
        return offsetOriginWidth;
    }

    public double getOffsetOriginHeight() {
        return offsetOriginHeight;
    }

    public double getMetersPerProjectionUnit() {
        updateMappingIfRequired();
        return metersPerProjectionUnit;
    }

    public double getMetersPerCanvasUnit() {
        updateMappingIfRequired();
        return metersPerCanvasUnit;
    }

}
