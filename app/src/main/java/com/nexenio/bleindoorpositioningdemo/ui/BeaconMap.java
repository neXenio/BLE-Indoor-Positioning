package com.nexenio.bleindoorpositioningdemo.ui;

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

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.distance.DistanceUtil;
import com.nexenio.bleindoorpositioning.location.listener.LocationListener;
import com.nexenio.bleindoorpositioning.location.projection.CanvasProjection;
import com.nexenio.bleindoorpositioning.location.projection.EquirectangularProjection;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steppschuh on 16.11.17.
 */

public class BeaconMap extends BeaconView {

    protected ValueAnimator deviceAccuracyAnimator;

    protected Location topLeftLocation;
    protected Location bottomRightLocation;
    protected LocationAnimator topLeftLocationAnimator;
    protected LocationAnimator bottomRightLocationAnimator;

    protected CanvasProjection canvasProjection;

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
    public void initialize() {
        super.initialize();
        canvasProjection = new CanvasProjection();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        canvasProjection.setCanvasWidth(canvasWidth);
        canvasProjection.setCanvasHeight(canvasHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLegend(canvas);
    }

    @Override
    protected void drawDevice(Canvas canvas) {
        PointF deviceCenter = (deviceLocationAnimator == null) ? canvasCenter : getPointFromLocation(deviceLocationAnimator.getLocation());

        float deviceAdvertisingRange = 20; // in meters TODO: get real value based on tx power
        float advertisingRadius = (float) canvasProjection.getCanvasUnitsFromMeters(deviceAdvertisingRange);

        float animationValue = (deviceAccuracyAnimator == null) ? 0 : (float) deviceAccuracyAnimator.getAnimatedValue();
        float strokeRadius = (pixelsPerDip * 10) + (pixelsPerDip * 2 * animationValue);

        //canvas.drawCircle(deviceCenter.x, deviceCenter.y, strokeRadius, deviceRangePaint);
        canvas.drawCircle(deviceCenter.x, deviceCenter.y, advertisingRadius, deviceRangePaint);
        canvas.drawCircle(deviceCenter.x, deviceCenter.y, strokeRadius, whiteFillPaint);
        canvas.drawCircle(deviceCenter.x, deviceCenter.y, strokeRadius, secondaryStrokePaint);
        canvas.drawCircle(deviceCenter.x, deviceCenter.y, pixelsPerDip * 8, secondaryFillPaint);
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
        float advertisingRadius = (float) canvasProjection.getCanvasUnitsFromMeters(beacon.getEstimatedAdvertisingRange());

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
        float beaconRadius = pixelsPerDip * 8;
        int beaconCornerRadius = (int) pixelsPerDip * 2;
        RectF rect = new RectF(beaconCenter.x - beaconRadius, beaconCenter.y - beaconRadius, beaconCenter.x + beaconRadius, beaconCenter.y + beaconRadius);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, whiteFillPaint);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, primaryStrokePaint);

        beaconRadius = beaconRadius - pixelsPerDip * 2;
        rect = new RectF(beaconCenter.x - beaconRadius, beaconCenter.y - beaconRadius, beaconCenter.x + beaconRadius, beaconCenter.y + beaconRadius);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, primaryFillPaint);
    }

    protected void drawLegend(Canvas canvas) {
        drawReferenceLine(canvas);
    }

    protected void drawReferenceLine(Canvas canvas) {
        float canvasPadding = canvasWidth * canvasProjection.getPaddingFactor();
        float maximumReferenceLineWidth = canvasWidth - (2 * canvasPadding);
        float maximumReferenceDistance = (float) canvasProjection.getMetersFromCanvasUnits(maximumReferenceLineWidth);
        float referenceDistance = DistanceUtil.getReasonableSmallerEvenDistance(maximumReferenceDistance);
        float referenceLineWidth = (float) canvasProjection.getCanvasUnitsFromMeters(referenceDistance);
        float referenceLinePadding = (canvasWidth - referenceLineWidth) / 2;

        Paint legendPaint = new Paint(textPaint);
        legendPaint.setAlpha(50);
        legendPaint.setTextSize(pixelsPerDip * 12);

        float referenceYOffset = canvasHeight - (pixelsPerDip * 16);
        PointF referenceStartPoint = new PointF(referenceLinePadding, referenceYOffset);
        PointF referenceEndPoint = new PointF(canvasWidth - referenceLinePadding, referenceYOffset);

        // horizontal line
        canvas.drawRect(
                referenceStartPoint.x,
                referenceStartPoint.y,
                referenceEndPoint.x,
                referenceEndPoint.y - pixelsPerDip,
                legendPaint
        );

        // left vertical line
        canvas.drawRect(
                referenceStartPoint.x,
                referenceStartPoint.y - (pixelsPerDip * 8),
                referenceStartPoint.x + pixelsPerDip,
                referenceStartPoint.y,
                legendPaint
        );

        // right vertical line
        canvas.drawRect(
                referenceEndPoint.x,
                referenceEndPoint.y - (pixelsPerDip * 8),
                referenceEndPoint.x + pixelsPerDip,
                referenceEndPoint.y,
                legendPaint
        );

        // text
        String referenceText = String.valueOf(Math.round(referenceDistance)) + " meters";
        float referenceTextWidth = legendPaint.measureText(referenceText);
        canvas.drawText(
                referenceText,
                (canvasWidth / 2) - (referenceTextWidth / 2),
                referenceStartPoint.y - (pixelsPerDip * 4),
                legendPaint
        );
    }

    protected PointF getPointFromLocation(Location location) {
        float x = canvasProjection.getXFromLocation(location);
        float y = canvasProjection.getYFromLocation(location);
        return new PointF(x, y);
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
        topLeftLocation = EquirectangularProjection.getTopLeftLocation(locations);
        bottomRightLocation = EquirectangularProjection.getBottomRightLocation(locations);

        if (edgeLocationsChanged()) {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                    if (locationProvider == topLeftLocationAnimator) {
                        canvasProjection.setTopLeftLocation(location);
                    } else if (locationProvider == bottomRightLocationAnimator) {
                        canvasProjection.setBottomRightLocation(location);
                    }
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

}
