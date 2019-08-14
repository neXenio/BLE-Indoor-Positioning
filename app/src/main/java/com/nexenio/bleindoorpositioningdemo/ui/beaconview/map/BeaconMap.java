package com.nexenio.bleindoorpositioningdemo.ui.beaconview.map;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationListener;
import com.nexenio.bleindoorpositioning.location.distance.DistanceUtil;
import com.nexenio.bleindoorpositioning.location.projection.CanvasProjection;
import com.nexenio.bleindoorpositioning.location.projection.EquirectangularProjection;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.nexenio.bleindoorpositioningdemo.ui.LocationAnimator;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 16.11.17.
 */

public class BeaconMap extends BeaconView {

    protected ValueAnimator deviceRadiusAnimator;

    protected Location topLeftLocation;
    protected Location bottomRightLocation;
    protected LocationAnimator topLeftLocationAnimator;
    protected LocationAnimator bottomRightLocationAnimator;

    protected CanvasProjection canvasProjection;

    protected Location predictedDeviceLocation;
    protected LocationAnimator predictedDeviceLocationAnimator;

    protected List<Location> recentLocations = new ArrayList<>();

    protected BeaconMapBackground mapBackground;
    protected Matrix backgroundMatrix;
    protected float matrixScaleFactor;
    protected PointF matrixTranslationPoint;
    protected float matrixRotationDegrees;

    protected Paint historyFillPaint;

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

    @CallSuper
    @Override
    public void initialize() {
        super.initialize();
        canvasProjection = new CanvasProjection();
        historyFillPaint = new Paint(secondaryFillPaint);
    }

    @CallSuper
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        canvasProjection.setCanvasWidth(canvasWidth);
        canvasProjection.setCanvasHeight(canvasHeight);
    }

    @CallSuper
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLegend(canvas);
    }

    @CallSuper
    @Override
    protected void drawBackground(Canvas canvas) {
        super.drawBackground(canvas);

        if (mapBackground == null) {
            return;
        }

        matrixScaleFactor = (float) (mapBackground.getMetersPerPixel() / canvasProjection.getMetersPerCanvasUnit());
        matrixTranslationPoint = getPointFromLocation(mapBackground.getTopLeftLocation());

        matrixRotationDegrees = (float) mapBackground.getBearing();

        backgroundMatrix = new Matrix();
        backgroundMatrix.postScale(matrixScaleFactor, matrixScaleFactor);
        backgroundMatrix.postTranslate(matrixTranslationPoint.x, matrixTranslationPoint.y);
        backgroundMatrix.postRotate(matrixRotationDegrees);

        canvas.drawBitmap(mapBackground.getImageBitmap(), backgroundMatrix, null);
    }

    @Override
    protected void drawDevice(Canvas canvas) {
        drawDeviceHistory(canvas);
        drawDevicePrediction(canvas);

        if (deviceLocationAnimator == null) {
            return;
        }

        PointF deviceCenter = getPointFromLocation(deviceLocationAnimator.getLocation());

        float locationAccuracy = (float) deviceLocationAnimator.getLocation().getAccuracy();
        float deviceAccuracyRadius = (float) canvasProjection.getCanvasUnitsFromMeters(locationAccuracy);

        float animationValue = (deviceRadiusAnimator == null) ? 0 : (float) deviceRadiusAnimator.getAnimatedValue();
        float strokeRadius = (pixelsPerDip * 10) + (pixelsPerDip * 2 * animationValue);

        canvas.drawCircle(deviceCenter.x, deviceCenter.y, deviceAccuracyRadius, deviceRangePaint);
        canvas.drawCircle(deviceCenter.x, deviceCenter.y, strokeRadius, whiteFillPaint);
        canvas.drawCircle(deviceCenter.x, deviceCenter.y, strokeRadius, secondaryStrokePaint);
        canvas.drawCircle(deviceCenter.x, deviceCenter.y, pixelsPerDip * 8, secondaryFillPaint);
    }

    protected void drawDeviceHistory(Canvas canvas) {
        PointF deviceCenter;
        float heatmapRadius = (float) canvasProjection.getCanvasUnitsFromMeters(1);
        float recencyScore;
        int alpha;

        for (Location location : recentLocations) {
            if (location == null || !location.hasLatitudeAndLongitude()) {
                continue;
            }
            deviceCenter = getPointFromLocation(location);
            recencyScore = getRecencyScore(location.getTimestamp(), TimeUnit.SECONDS.toMillis(10));
            alpha = (int) (255 * 0.25 * recencyScore);
            historyFillPaint.setAlpha(alpha);
            RadialGradient gradient = new RadialGradient(deviceCenter.x, deviceCenter.y, heatmapRadius,
                    new int[]{secondaryFillPaint.getColor(), Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
            historyFillPaint.setShader(gradient);
            canvas.drawCircle(deviceCenter.x, deviceCenter.y, heatmapRadius, historyFillPaint);
        }
    }

    protected void drawDevicePrediction(Canvas canvas) {
        if (deviceLocationAnimator == null || predictedDeviceLocationAnimator == null) {
            return;
        }

        PointF predictionCenter = getPointFromLocation(predictedDeviceLocationAnimator.getLocation());
        canvas.drawLine(getPointFromLocation(deviceLocationAnimator.getLocation()).x,
                getPointFromLocation(deviceLocationAnimator.getLocation()).y,
                predictionCenter.x, predictionCenter.y, primaryStrokePaint);
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
        float distance = (float) canvasProjection.getCanvasUnitsFromMeters(beacon.getDistance());
        canvas.drawCircle(beaconCenter.x, beaconCenter.y, distance, beaconRangePaint);

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
        AdvertisingPacket latestAdvertisingPacket = beacon.getLatestAdvertisingPacket();
        long timeSinceLastAdvertisement = latestAdvertisingPacket != null ? System.currentTimeMillis() - latestAdvertisingPacket.getTimestamp() : 0;

        float animationValue = (deviceRadiusAnimator == null) ? 0 : (float) deviceRadiusAnimator.getAnimatedValue();
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
        if (location == null || !location.hasLatitudeAndLongitude()) {
            return new PointF(0, 0);
        }
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

            Location deviceTopLeftLocation = new Location(deviceLocationAnimator.getLocation());
            deviceTopLeftLocation.setLatitude(deviceTopLeftLocation.getLatitude() + 0.00001);
            deviceTopLeftLocation.setLongitude(deviceTopLeftLocation.getLongitude() - 0.00002);
            locations.add(deviceTopLeftLocation);

            Location deviceBottomRightLocation = new Location(deviceLocationAnimator.getLocation());
            deviceBottomRightLocation.setLatitude(deviceBottomRightLocation.getLatitude() - 0.00001);
            deviceBottomRightLocation.setLongitude(deviceBottomRightLocation.getLongitude() + 0.00002);
            locations.add(deviceBottomRightLocation);
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
                    if (!location.hasLatitudeAndLongitude()) {
                        return;
                    }
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
        recentLocations.add(deviceLocation);
        if (recentLocations.size() > 100) {
            recentLocations.remove(0);
        }
        super.onDeviceLocationChanged();
    }

    public void onPredictedDeviceLocationChanged() {
        predictedDeviceLocationAnimator = startLocationAnimation(predictedDeviceLocationAnimator, predictedDeviceLocation, new LocationListener() {
            @Override
            public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                invalidate();
            }
        });
    }

    protected void startDeviceRadiusAnimation() {
        if (deviceRadiusAnimator != null && deviceRadiusAnimator.isRunning()) {
            return;
        }
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

    /**
     * Calculates a score based on the recency of the specified timestamp.
     *
     * @return score between 0 and 1
     */
    public static float getRecencyScore(long timestamp, long maximumAge) {
        long age = System.currentTimeMillis() - timestamp;
        long ageDelta = Math.max(0, maximumAge - age);
        if (ageDelta == 0 || maximumAge == 0) {
            return 0;
        }
        float linearScore = ageDelta / (float) maximumAge;
        return (float) ((Math.log(ageDelta) / Math.log(10)) / ((Math.log(maximumAge)) / Math.log(1 + (9 * linearScore))));
    }

    /*
        Getter & Setter
     */

    public Location getPredictedDeviceLocation() {
        return predictedDeviceLocation;
    }

    public void setPredictedDeviceLocation(Location predictedDeviceLocation) {
        this.predictedDeviceLocation = predictedDeviceLocation;
        onPredictedDeviceLocationChanged();
    }

    public BeaconMapBackground getMapBackground() {
        return mapBackground;
    }

    public void setMapBackground(BeaconMapBackground mapBackground) {
        this.mapBackground = mapBackground;
    }

}
