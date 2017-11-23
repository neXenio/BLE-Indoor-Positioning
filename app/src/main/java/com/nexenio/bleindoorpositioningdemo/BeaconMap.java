package com.nexenio.bleindoorpositioningdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioning.ble.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.listener.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steppschuh on 16.11.17.
 */

public class BeaconMap extends BeaconView {

    protected ValueAnimator deviceRadiusAnimator;

    protected Location topLeftLocation;
    protected Location bottomRightLocation;
    protected LocationAnimator topLeftLocationAnimator;
    protected LocationAnimator bottomRightLocationAnimator;

    public BeaconMap(Context context) {
        super(context);
    }

    public BeaconMap(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BeaconMap(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BeaconMap(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void drawDevice(Canvas canvas) {
        PointF point = (deviceLocationAnimator == null) ? canvasCenter : getPointFromLocation(deviceLocationAnimator.getLocation());
        float animationValue = (deviceRadiusAnimator == null) ? 0 : (float) deviceRadiusAnimator.getAnimatedValue();
        float deviceRadius = (pixelsPerDip * 8) + (pixelsPerDip * 24 * animationValue);
        canvas.drawCircle(point.x, point.y, deviceRadius, deviceRadiusPaint);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 32, deviceRadiusPaint);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 10, whiteFillPaint);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 10, primaryStrokePaint);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 8, primaryFillPaint);
    }

    @Override
    protected void drawBeacon(Canvas canvas, Beacon beacon) {
        PointF point = getPointFromLocation(beacon.getLocation());
        //canvas.drawCircle(point.x, point.y, pixelsPerDip * 250, deviceRadiusPaint);

        float beaconRadius = pixelsPerDip * 8;
        int beaconCornerRadius = (int) pixelsPerDip * 2;
        RectF rect = new RectF(point.x - beaconRadius, point.y - beaconRadius, point.x + beaconRadius, point.y + beaconRadius);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, whiteFillPaint);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, primaryStrokePaint);

        beaconRadius = beaconRadius - pixelsPerDip * 2;
        rect = new RectF(point.x - beaconRadius, point.y - beaconRadius, point.x + beaconRadius, point.y + beaconRadius);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, primaryFillPaint);
    }

    @Override
    protected void updateMapping() {
        // check if the canvas has been initialized
        if (canvasWidth == 0 || canvasHeight == 0) {
            return;
        }

        // check if edge locations are available
        if (topLeftLocationAnimator == null || bottomRightLocationAnimator == null) {
            return;
        }

        // get the projected width and height for the top left and bottom right location
        double topLeftLocationWidth = projection.getWidthFromLongitude(topLeftLocationAnimator.getLocation().getLongitude());
        double topLeftLocationHeight = projection.getHeightFromLatitude(topLeftLocationAnimator.getLocation().getLatitude());
        double bottomRightLocationWidth = projection.getWidthFromLongitude(bottomRightLocationAnimator.getLocation().getLongitude());
        double bottomRightLocationHeight = projection.getHeightFromLatitude(bottomRightLocationAnimator.getLocation().getLatitude());

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

    public void fitToCurrentLocations() {
        topLeftLocation = null;
        bottomRightLocation = null;
        onLocationsChanged();
    }

    private void updateEdgeLocations() {
        List<Location> locations = new ArrayList<>();
        for (Beacon beacon : beacons) {
            locations.add(beacon.getLocation());
        }
        if (deviceLocationAnimator != null) {
            locations.add(deviceLocationAnimator.getLocation());
        }
        if (topLeftLocationAnimator != null) {
            locations.add(topLeftLocationAnimator.getLocation());
        }
        if (bottomRightLocationAnimator != null) {
            locations.add(bottomRightLocationAnimator.getLocation());
        }
        topLeftLocation = getTopLeftLocation(locations);
        bottomRightLocation = getBottomRightLocation(locations);

        if (edgeLocationsChanged()) {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                    invalidate();
                }
            };
            topLeftLocationAnimator = startLocationAnimation(topLeftLocationAnimator, topLeftLocation, locationListener);
            bottomRightLocationAnimator = startLocationAnimation(bottomRightLocationAnimator, bottomRightLocation, locationListener);
        }
    }

    private boolean edgeLocationsChanged() {
        if (topLeftLocationAnimator == null || bottomRightLocationAnimator == null) {
            return true;
        }
        boolean topLeftChanged = !topLeftLocation.latitudeAndLongitudeEquals(topLeftLocationAnimator.getTargetLocation());
        boolean bottomRightChanged = !bottomRightLocation.latitudeAndLongitudeEquals(bottomRightLocationAnimator.getTargetLocation());
        return topLeftChanged || bottomRightChanged;
    }

    private static Location getTopLeftLocation(List<Location> locations) {
        double maximumLatitude = -Double.MAX_VALUE;
        double minimumLongitude = Double.MAX_VALUE;
        for (Location location : locations) {
            if (location == null) {
                continue;
            }
            maximumLatitude = Math.max(maximumLatitude, location.getLatitude());
            minimumLongitude = Math.min(minimumLongitude, location.getLongitude());
        }
        return new Location(maximumLatitude, minimumLongitude);
    }

    private static Location getBottomRightLocation(List<Location> locations) {
        double minimumLatitude = Double.MAX_VALUE;
        double maximumLongitude = -Double.MAX_VALUE;
        for (Location location : locations) {
            if (location == null) {
                continue;
            }
            maximumLongitude = Math.max(maximumLongitude, location.getLongitude());
            minimumLatitude = Math.min(minimumLatitude, location.getLatitude());
        }
        return new Location(minimumLatitude, maximumLongitude);
    }

    @Override
    public void onLocationsChanged() {
        updateEdgeLocations();
        super.onLocationsChanged();
    }

    @Override
    public void onDeviceLocationChanged() {
        startDeviceRadiusAnimation();
        super.onDeviceLocationChanged();
    }

    protected void startDeviceRadiusAnimation() {
        deviceRadiusAnimator = ValueAnimator.ofFloat(0, 1);
        deviceRadiusAnimator.setDuration(LocationAnimator.ANIMATION_DURATION_LONG);
        deviceRadiusAnimator.setRepeatCount(1);
        deviceRadiusAnimator.setRepeatMode(ValueAnimator.REVERSE);
        deviceRadiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        deviceRadiusAnimator.start();
    }

}
