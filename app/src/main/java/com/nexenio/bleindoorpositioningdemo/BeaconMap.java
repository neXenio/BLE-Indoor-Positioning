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

/**
 * Created by steppschuh on 16.11.17.
 */

public class BeaconMap extends BeaconView {

    protected ValueAnimator deviceRadiusAnimator;
    protected ValueAnimator deviceLocationXAnimator;
    protected ValueAnimator deviceLocationYAnimator;

    protected float deviceRadius;
    protected Location lastRenderedDeviceLocation;

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
        PointF point = (deviceLocation == null) ? canvasCenter : getPointFromLocation(deviceLocation);
        canvas.drawCircle(point.x, point.y, deviceRadius, deviceRadiusPaint);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 32, deviceRadiusPaint);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 10, whiteFillPaint);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 10, primaryStrokePaint);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 8, primaryFillPaint);
        lastRenderedDeviceLocation = deviceLocation;
    }

    @Override
    protected void drawBeacon(Canvas canvas, Beacon beacon) {
        PointF point = getPointFromLocation(beacon.getLocation());
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 250, deviceRadiusPaint);

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
        if (topLeftLocation == null || bottomRightLocation == null) {
            return;
        }

        // get the projected width and height for the top left and bottom right location
        double topLeftLocationWidth = projection.getWidthFromLongitude(topLeftLocation.getLongitude());
        double topLeftLocationHeight = projection.getHeightFromLatitude(topLeftLocation.getLatitude());
        double bottomRightLocationWidth = projection.getWidthFromLongitude(bottomRightLocation.getLongitude());
        double bottomRightLocationHeight = projection.getHeightFromLatitude(bottomRightLocation.getLatitude());

        // get the minimum width and height that should be mapped in order to
        // display the top left and bottom right location on the canvas
        double minimumWidth = bottomRightLocationWidth - topLeftLocationWidth;
        double minimumHeight = bottomRightLocationHeight - topLeftLocationHeight;

        // add some padding
        double padding = Math.max(minimumWidth, minimumHeight) * 0.5;
        minimumWidth += padding;
        minimumHeight += padding;

        // get the mapped width and height equivalent to the pixel dimensions of the canvas
        mappedCanvasWidth = minimumWidth;
        mappedCanvasHeight = minimumHeight;
        if (canvasAspectRatio > (minimumWidth / minimumHeight)) {
            mappedCanvasWidth = minimumHeight * canvasAspectRatio;
        } else {
            mappedCanvasHeight = minimumWidth / canvasAspectRatio;
        }

        // get the offsets that should be applied to mappings in order
        // to center the locations on the canvas
        double offsetWidth = (mappedCanvasWidth - minimumWidth + padding) / 2;
        double offsetHeight = (mappedCanvasHeight - minimumHeight + padding) / 2;

        // get the origin width and height (equivalent to the canvas origin 0,0) including
        // the calculated mapping offset
        offsetOriginWidth = topLeftLocationWidth - offsetWidth;
        offsetOriginHeight = topLeftLocationHeight - offsetHeight;
    }

    @Override
    public void onDeviceLocationChanged() {
        startDeviceRadiusAnimation();
        startDeviceLocationAnimation();
        super.onDeviceLocationChanged();
    }

    protected void startDeviceRadiusAnimation() {
        deviceRadiusAnimator = ValueAnimator.ofFloat(0, 1);
        deviceRadiusAnimator.setDuration(500);
        deviceRadiusAnimator.setRepeatCount(1);
        deviceRadiusAnimator.setRepeatMode(ValueAnimator.REVERSE);
        deviceRadiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animationValue = (float) valueAnimator.getAnimatedValue();
                deviceRadius = (pixelsPerDip * 8) + (pixelsPerDip * 24 * animationValue);
                invalidate();
            }
        });
        deviceRadiusAnimator.start();
    }

    protected void startDeviceLocationAnimation() {
        // cancel ongoing animations, if any
        cancelDeviceLocationAnimation();

        if (lastRenderedDeviceLocation == null || deviceLocation == null) {
            return;
        }

        // x
        deviceLocationXAnimator = ValueAnimator.ofFloat((float) lastRenderedDeviceLocation.getLatitude(), (float) deviceLocation.getLatitude());
        deviceLocationXAnimator.setDuration(1000);
        deviceLocationXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                deviceLocation.setLatitude((float) valueAnimator.getAnimatedValue());
                onLocationsChanged();
            }
        });
        deviceLocationXAnimator.start();

        // y
        deviceLocationYAnimator = ValueAnimator.ofFloat((float) lastRenderedDeviceLocation.getLongitude(), (float) deviceLocation.getLongitude());
        deviceLocationYAnimator.setDuration(1000);
        deviceLocationYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                deviceLocation.setLongitude((float) valueAnimator.getAnimatedValue());
                onLocationsChanged();
            }
        });
        deviceLocationYAnimator.start();
    }

    private void cancelDeviceLocationAnimation() {
        if (deviceLocationYAnimator != null) {
            deviceLocationYAnimator.cancel();
        }
        if (deviceLocationXAnimator != null) {
            deviceLocationXAnimator.cancel();
        }
    }

}
