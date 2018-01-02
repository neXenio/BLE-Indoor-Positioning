package com.nexenio.bleindoorpositioningdemo.ui.beaconview.radar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioningdemo.ui.LocationAnimator;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steppschuh on 16.11.17.
 */

public class BeaconRadar extends BeaconView {

    protected ValueAnimator deviceAccuracyAnimator;
    protected ValueAnimator maximumDistanceAnimator;
    protected ValueAnimator deviceAngleAnimator;

    public BeaconRadar(Context context) {
        super(context);
    }

    public BeaconRadar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BeaconRadar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BeaconRadar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize() {
        super.initialize();
        startMaximumDistanceAnimation(100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLegend(canvas);
    }

    @Override
    protected void drawDevice(Canvas canvas) {
        float deviceAdvertisingRange = 20; // in meters TODO: get real value based on tx power
        float advertisingRadius = getCanvasUnitsFromMeters(deviceAdvertisingRange);

        float animationValue = (deviceAccuracyAnimator == null) ? 0 : (float) deviceAccuracyAnimator.getAnimatedValue();
        float strokeRadius = (pixelsPerDip * 10) + (pixelsPerDip * 2 * animationValue);

        //canvas.drawCircle(deviceCenter.x, deviceCenter.y, strokeRadius, deviceRangePaint);
        canvas.drawCircle(canvasCenter.x, canvasCenter.y, advertisingRadius, deviceRangePaint);
        canvas.drawCircle(canvasCenter.x, canvasCenter.y, strokeRadius, whiteFillPaint);
        canvas.drawCircle(canvasCenter.x, canvasCenter.y, strokeRadius, secondaryStrokePaint);
        canvas.drawCircle(canvasCenter.x, canvasCenter.y, pixelsPerDip * 8, secondaryFillPaint);
    }

    @Override
    protected void drawBeacons(Canvas canvas) {
        Map<Beacon, PointF> beaconCenterMap = new HashMap<>();
        // draw all backgrounds
        for (Beacon beacon : beacons) {
            PointF beaconCenter = getPointFromLocation(beacon.getLocation());
            beaconCenterMap.put(beacon, beaconCenter);
            drawBeaconBackground(canvas, beacon, beaconCenter);
        }
        // draw all foregrounds
        for (Beacon beacon : beacons) {
            drawBeaconForeground(canvas, beacon, beaconCenterMap.get(beacon));
        }
    }

    /**
     * This shouldn't be called, because the created beacon background may overlay existing beacon
     * foregrounds. Use {@link #drawBeacons(Canvas)} instead.
     */
    @Override
    protected void drawBeacon(Canvas canvas, Beacon beacon) {
        PointF beaconCenter = getPointFromLocation(beacon.getLocation());
        drawBeaconBackground(canvas, beacon, beaconCenter);
        drawBeaconForeground(canvas, beacon, beaconCenter);
    }

    protected void drawBeaconBackground(Canvas canvas, Beacon beacon, PointF beaconCenter) {
        float advertisingRadius = getCanvasUnitsFromMeters(beacon.getEstimatedAdvertisingRange());

        Paint innerBeaconRangePaint = new Paint(beaconRangePaint);
        innerBeaconRangePaint.setAlpha(100);
        Shader rangeShader = new RadialGradient(
                beaconCenter.x,
                beaconCenter.y,
                advertisingRadius - (pixelsPerDip * 0),
                primaryFillPaint.getColor(), beaconRangePaint.getColor(),
                Shader.TileMode.MIRROR);

        innerBeaconRangePaint.setShader(rangeShader);
        //canvas.drawCircle(beaconCenter.x, beaconCenter.y, advertisingRadius, innerBeaconRangePaint);
        canvas.drawCircle(beaconCenter.x, beaconCenter.y, advertisingRadius, beaconRangePaint);
    }

    protected void drawBeaconForeground(Canvas canvas, Beacon beacon, PointF beaconCenter) {
        AdvertisingPacket latestAdvertisingPacket = beacon.getLatestAdvertisingPacket();
        long timeSinceLastAdvertisement = latestAdvertisingPacket != null ? System.currentTimeMillis() - latestAdvertisingPacket.getTimestamp() : 0;

        float animationValue = (deviceAccuracyAnimator == null) ? 0 : (float) deviceAccuracyAnimator.getAnimatedValue();
        animationValue *= Math.max(0, 1 - (timeSinceLastAdvertisement / 1000));
        float beaconRadius = pixelsPerDip * 8;
        float strokeRadius = beaconRadius + (pixelsPerDip * 2) + (pixelsPerDip * 2 * animationValue);

        int beaconCornerRadius = (int) pixelsPerDip * 2;
        RectF rect = new RectF(beaconCenter.x - strokeRadius, beaconCenter.y - strokeRadius, beaconCenter.x + strokeRadius, beaconCenter.y + strokeRadius);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, whiteFillPaint);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, primaryStrokePaint);

        rect = new RectF(beaconCenter.x - beaconRadius, beaconCenter.y - beaconRadius, beaconCenter.x + beaconRadius, beaconCenter.y + beaconRadius);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, primaryFillPaint);
    }

    protected void drawLegend(Canvas canvas) {

    }

    protected PointF getPointFromLocation(Location location) {
        if (deviceLocationAnimator == null) {
            return new PointF(canvasCenter.x, canvasCenter.y);
        }
        double distance = location.getDistanceTo(deviceLocationAnimator.getLocation());
        double radius = getCanvasUnitsFromMeters(distance);
        double angle = deviceLocationAnimator.getLocation().getAngleTo(location);
        angle = (angle - (float) deviceAngleAnimator.getAnimatedValue()) % 360;
        angle = Math.toRadians(angle) - (Math.PI / 2);
        float x = (float) (canvasCenter.x + (radius * Math.cos(angle)));
        float y = (float) (canvasCenter.y + (radius * Math.sin(angle)));
        return new PointF(x, y);
    }

    protected float getCanvasUnitsFromMeters(double meters) {
        return (float) (Math.min(canvasCenter.x, canvasCenter.y) * meters) / (float) maximumDistanceAnimator.getAnimatedValue();
    }

    protected float getMetersFromCanvasUnits(float canvasUnits) {
        return ((float) maximumDistanceAnimator.getAnimatedValue() * canvasUnits) / Math.min(canvasCenter.x, canvasCenter.y);
    }

    public void fitToCurrentLocations() {
        float maximumDistance = 20;
        // TODO: get actual maximum distance
        startMaximumDistanceAnimation(maximumDistance);
    }

    @Override
    public void onDeviceLocationChanged() {
        startDeviceRadiusAnimation();
        super.onDeviceLocationChanged();
    }

    protected void startMaximumDistanceAnimation(float distance) {
        float originValue = distance;
        if (maximumDistanceAnimator != null) {
            originValue = (float) maximumDistanceAnimator.getAnimatedValue();
            maximumDistanceAnimator.cancel();
        }
        maximumDistanceAnimator = ValueAnimator.ofFloat(originValue, distance);
        maximumDistanceAnimator.setDuration(LocationAnimator.ANIMATION_DURATION_LONG);
        maximumDistanceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        maximumDistanceAnimator.start();
    }


    protected void startDeviceRadiusAnimation() {
        if (deviceAccuracyAnimator != null && deviceAccuracyAnimator.isRunning()) {
            return;
        }
        deviceAccuracyAnimator = ValueAnimator.ofFloat(0, 1);
        deviceAccuracyAnimator.setDuration(LocationAnimator.ANIMATION_DURATION_LONG);
        deviceAccuracyAnimator.setRepeatCount(1);
        deviceAccuracyAnimator.setRepeatMode(ValueAnimator.REVERSE);
        deviceAccuracyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        deviceAccuracyAnimator.start();
    }

    public void setDeviceAngle(float deviceAngle) {
        float originValue = deviceAngle;
        if (deviceAngleAnimator != null) {
            originValue = (float) deviceAngleAnimator.getAnimatedValue();
            deviceAngleAnimator.cancel();
        }
        deviceAngleAnimator = ValueAnimator.ofFloat(originValue, deviceAngle);
        deviceAngleAnimator.setDuration(200);
        deviceAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        deviceAngleAnimator.start();
    }

}
