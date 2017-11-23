package com.nexenio.bleindoorpositioning.location.projection;

import com.nexenio.bleindoorpositioning.location.Location;

/**
 * Created by steppschuh on 23.11.17.
 */

public class CanvasProjection {

    private EquirectangularProjection geographicProjection;

    private Location topLeftLocation;
    private Location bottomRightLocation;

    private float canvasWidth;
    private float canvasHeight;

    private double projectedCanvasWidth;
    private double projectedCanvasHeight;

    private double offsetOriginWidth;
    private double offsetOriginHeight;

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
        double topLeftLocationWidth = geographicProjection.getWidthFromLongitude(topLeftLocation.getLongitude());
        double topLeftLocationHeight = geographicProjection.getHeightFromLatitude(topLeftLocation.getLatitude());
        double bottomRightLocationWidth = geographicProjection.getWidthFromLongitude(bottomRightLocation.getLongitude());
        double bottomRightLocationHeight = geographicProjection.getHeightFromLatitude(bottomRightLocation.getLatitude());

        // get the minimum width and height that should be mapped in order to
        // display the top left and bottom right location on the canvas
        double minimumWidth = bottomRightLocationWidth - topLeftLocationWidth;
        double minimumHeight = bottomRightLocationHeight - topLeftLocationHeight;

        // add some padding
        double padding = Math.max(minimumWidth, minimumHeight) * 0.2;
        minimumWidth += padding;
        minimumHeight += padding;

        // get the mapped width and height equivalent to the pixel dimensions of the canvas
        projectedCanvasWidth = minimumWidth;
        projectedCanvasHeight = minimumHeight;
        float canvasAspectRatio = canvasWidth / canvasHeight;
        if (canvasAspectRatio > (minimumWidth / minimumHeight)) {
            projectedCanvasWidth = minimumHeight * canvasAspectRatio;
        } else {
            projectedCanvasHeight = minimumWidth / canvasAspectRatio;
        }

        // get the offsets that should be applied to mappings in order
        // to center the locations on the canvas
        double offsetWidth = (projectedCanvasWidth - minimumWidth + padding) / 2;
        double offsetHeight = (projectedCanvasHeight - minimumHeight + padding) / 2;

        // get the origin width and height (equivalent to the canvas origin 0,0) including
        // the calculated mapping offset
        offsetOriginWidth = topLeftLocationWidth - offsetWidth;
        offsetOriginHeight = topLeftLocationHeight - offsetHeight;
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

}
