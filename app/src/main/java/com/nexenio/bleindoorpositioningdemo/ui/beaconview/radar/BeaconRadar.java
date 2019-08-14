package com.nexenio.bleindoorpositioningdemo.ui.beaconview.radar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.distance.DistanceUtil;
import com.nexenio.bleindoorpositioningdemo.ui.LocationAnimator;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by steppschuh on 16.11.17.
 */

public class BeaconRadar extends BeaconView {

    /*
        Device drawing related variables
     */
    protected ValueAnimator deviceAngleAnimator;
    protected ValueAnimator deviceAccuracyAnimator;
    protected float deviceAccuracyAnimationValue;
    protected float deviceAdvertisingRange;
    protected float deviceAdvertisingRadius;
    protected float deviceStrokeRadius;

    /*
        Beacon drawing related variables
     */
    protected float beaconAccuracyAnimationValue;
    protected float beaconRadius = pixelsPerDip * 8;
    protected float beaconCornerRadius = pixelsPerDip * 2;
    protected float beaconStrokeRadius;
    protected long timeSinceLastAdvertisement;

    /*
        Legend drawing related variables
     */
    protected Paint legendPaint;
    protected int referenceLineCount = 5;
    protected float referenceDistance;
    protected float referenceDistanceStep;
    protected float currentReferenceDistance;
    protected float currentReferenceCanvasUnits;
    protected String referenceText;
    protected float referenceTextWidth;

    /*
        Location mapping related variables
     */
    protected ValueAnimator maximumDistanceAnimator;
    protected double locationDistance;
    protected double locationRadius;
    protected double locationRotationAngle;

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
        startDeviceAngleAnimation(0);
        legendPaint = new Paint(textPaint);
        legendPaint.setTextSize(pixelsPerDip * 12);
        legendPaint.setStyle(Paint.Style.STROKE);
        legendPaint.setColor(Color.BLACK);
        legendPaint.setAlpha(50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLegend(canvas);
    }

    @Override
    protected void drawDevice(Canvas canvas) {
        deviceAdvertisingRange = 20; // in meters TODO: get real value based on tx power
        deviceAdvertisingRadius = getCanvasUnitsFromMeters(deviceAdvertisingRange);

        deviceAccuracyAnimationValue = (deviceAccuracyAnimator == null) ? 0 : (float) deviceAccuracyAnimator.getAnimatedValue();
        deviceStrokeRadius = (pixelsPerDip * 10) + (pixelsPerDip * 2 * deviceAccuracyAnimationValue);

        canvas.drawCircle(canvasCenter.x, canvasCenter.y, deviceStrokeRadius, whiteFillPaint);
        canvas.drawCircle(canvasCenter.x, canvasCenter.y, deviceStrokeRadius, secondaryStrokePaint);
        canvas.drawCircle(canvasCenter.x, canvasCenter.y, pixelsPerDip * 8, secondaryFillPaint);
    }

    @Override
    protected void drawBeacons(Canvas canvas) {
        Map<Beacon, PointF> beaconCenterMap = new HashMap<>();
        // draw all backgrounds
        for (Beacon beacon : beacons) {
            PointF beaconCenter = getPointFromLocation(beacon.getLocation(), beacon);
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
        PointF beaconCenter = getPointFromLocation(beacon.getLocation(), beacon);
        drawBeaconBackground(canvas, beacon, beaconCenter);
        drawBeaconForeground(canvas, beacon, beaconCenter);
    }

    protected void drawBeaconBackground(Canvas canvas, Beacon beacon, PointF beaconCenter) {

    }

    protected void drawBeaconForeground(Canvas canvas, Beacon beacon, PointF beaconCenter) {
        AdvertisingPacket latestAdvertisingPacket = beacon.getLatestAdvertisingPacket();
        timeSinceLastAdvertisement = latestAdvertisingPacket != null ? System.currentTimeMillis() - latestAdvertisingPacket.getTimestamp() : 0;

        beaconAccuracyAnimationValue = (deviceAccuracyAnimator == null) ? 0 : (float) deviceAccuracyAnimator.getAnimatedValue();
        beaconAccuracyAnimationValue *= Math.max(0, 1 - (timeSinceLastAdvertisement / 1000));
        beaconStrokeRadius = beaconRadius + (pixelsPerDip * 2) + (pixelsPerDip * 2 * beaconAccuracyAnimationValue);

        RectF rect = new RectF(beaconCenter.x - beaconStrokeRadius, beaconCenter.y - beaconStrokeRadius, beaconCenter.x + beaconStrokeRadius, beaconCenter.y + beaconStrokeRadius);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, whiteFillPaint);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, primaryStrokePaint);

        rect = new RectF(beaconCenter.x - beaconRadius, beaconCenter.y - beaconRadius, beaconCenter.x + beaconRadius, beaconCenter.y + beaconRadius);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, primaryFillPaint);
    }

    protected void drawLegend(Canvas canvas) {
        referenceDistance = DistanceUtil.getReasonableSmallerEvenDistance((float) maximumDistanceAnimator.getAnimatedValue());
        referenceDistanceStep = Math.round(referenceDistance / (float) referenceLineCount);

        // include some more (+ 5) lines that are needed to avoid white space
        for (int i = referenceLineCount + 5; i > 0; i--) {
            currentReferenceDistance = Math.round(i * referenceDistanceStep);
            currentReferenceCanvasUnits = getCanvasUnitsFromMeters(currentReferenceDistance);

            canvas.drawCircle(
                    canvasCenter.x,
                    canvasCenter.y,
                    currentReferenceCanvasUnits,
                    legendPaint
            );

            referenceText = String.format(Locale.US, "%.0f", currentReferenceDistance) + "m";
            referenceTextWidth = legendPaint.measureText(referenceText);
            canvas.drawText(
                    referenceText,
                    canvasCenter.x - (referenceTextWidth / 2),
                    canvasCenter.y + currentReferenceCanvasUnits + legendPaint.getTextSize(),
                    legendPaint
            );
        }

    }

    protected PointF getPointFromLocation(Location location) {
        return getPointFromLocation(location, null);
    }

    protected PointF getPointFromLocation(Location location, @Nullable Beacon beacon) {
        if (deviceLocationAnimator == null) {
            return new PointF(canvasCenter.x, canvasCenter.y);
        }
        locationDistance = beacon != null ? beacon.getDistance() : location.getDistanceTo(deviceLocationAnimator.getLocation());
        locationRadius = getCanvasUnitsFromMeters(locationDistance);
        locationRotationAngle = deviceLocationAnimator.getLocation().getAngleTo(location);
        locationRotationAngle = (locationRotationAngle - (float) deviceAngleAnimator.getAnimatedValue()) % 360;
        locationRotationAngle = Math.toRadians(locationRotationAngle) - (Math.PI / 2);
        return new PointF(
                (float) (canvasCenter.x + (locationRadius * Math.cos(locationRotationAngle))),
                (float) (canvasCenter.y + (locationRadius * Math.sin(locationRotationAngle)))
        );
    }

    protected float getCanvasUnitsFromMeters(double meters) {
        return (float) (Math.min(canvasCenter.x, canvasCenter.y) * meters) / (float) maximumDistanceAnimator.getAnimatedValue();
    }

    protected float getMetersFromCanvasUnits(float canvasUnits) {
        return ((float) maximumDistanceAnimator.getAnimatedValue() * canvasUnits) / Math.min(canvasCenter.x, canvasCenter.y);
    }

    public void fitToCurrentLocations() {
        float maximumDistance = 10;
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

    public void startDeviceAngleAnimation(float deviceAngle) {
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
