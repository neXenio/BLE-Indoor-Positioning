package com.nexenio.bleindoorpositioningdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.nexenio.bleindoorpositioning.ble.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.projection.CanvasProjection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 16.11.17.
 */

public abstract class BeaconView extends View {

    protected Paint backgroundPaint;
    protected Paint textPaint;
    protected Paint primaryFillPaint;
    protected Paint primaryStrokePaint;
    protected Paint whiteFillPaint;
    protected Paint whiteStrokePaint;
    protected Paint deviceRadiusPaint;

    protected Location deviceLocation;
    protected Location topLeftLocation;
    protected Location bottomRightLocation;
    protected List<Beacon> beacons = new ArrayList<>();

    protected double canvasAspectRatio;
    protected int canvasWidth;
    protected int canvasHeight;
    protected Point canvasCenter;

    protected CanvasProjection projection = new CanvasProjection();

    protected double mappedCanvasWidth;
    protected double mappedCanvasHeight;
    protected double offsetOriginWidth;
    protected double offsetOriginHeight;

    protected float deviceRadius;
    protected float pixelsPerDip = DisplayUtil.convertDipToPixels(1);

    ValueAnimator valueAnimator;

    public BeaconView(Context context) {
        super(context);
        initialize();
    }

    public BeaconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public BeaconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public BeaconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    @CallSuper
    public void initialize() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        //textPaint.setTextSize(12);

        primaryFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        primaryFillPaint.setStyle(Paint.Style.FILL);
        primaryFillPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        primaryStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        primaryStrokePaint.setStyle(Paint.Style.STROKE);
        primaryStrokePaint.setStrokeWidth(pixelsPerDip);
        primaryStrokePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        whiteFillPaint = new Paint(primaryFillPaint);
        whiteFillPaint.setColor(Color.WHITE);

        whiteStrokePaint = new Paint(primaryStrokePaint);
        whiteStrokePaint.setColor(Color.WHITE);

        deviceRadiusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        deviceRadiusPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        deviceRadiusPaint.setAlpha(25);
        deviceRadiusPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);

        startAnimating();
    }

    @CallSuper
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.canvasCenter = new Point(width / 2, height / 2);
        this.canvasAspectRatio = width / (float) height;
        //projection.setCanvasWidth(1000 * 1000 * 10);
        //projection.setCanvasHeight(1000 * 1000 * 10);
        updateMapping();
    }

    @CallSuper
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawBeacons(canvas);
        drawDevice(canvas);
    }

    protected void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, canvasWidth, canvasHeight, backgroundPaint);
    }

    protected abstract void drawDevice(Canvas canvas);

    protected void drawBeacons(Canvas canvas) {
        for (Beacon beacon : beacons) {
            drawBeacon(canvas, beacon);
        }
    }

    protected abstract void drawBeacon(Canvas canvas, Beacon beacon);

    protected void startAnimating() {
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(TimeUnit.SECONDS.toMillis(1));
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animationValue = (float) valueAnimator.getAnimatedValue();
                deviceRadius = pixelsPerDip * 25 * animationValue;
                invalidate();
            }
        });
        valueAnimator.start();
    }

    protected abstract void updateMapping();

    protected Point getPointFromLocation(Location location) {
        double locationWidth = projection.getWidthFromLongitude(location.getLongitude());
        double locationHeight = projection.getHeightFromLatitude(location.getLatitude());
        double mappedLocationWidth = locationWidth - offsetOriginWidth;
        double mappedLocationHeight = locationHeight - offsetOriginHeight;
        double x = (mappedLocationWidth * canvasWidth) / mappedCanvasWidth;
        double y = (mappedLocationHeight * canvasHeight) / mappedCanvasHeight;
        return new Point((int) x, (int) y);
    }

    private void updateEdgeLocations() {
        List<Location> locations = new ArrayList<>();
        for (Beacon beacon : beacons) {
            locations.add(beacon.getLocation());
        }
        locations.add(deviceLocation);
        locations.add(topLeftLocation);
        locations.add(bottomRightLocation);
        topLeftLocation = getTopLeftLocation(locations);
        bottomRightLocation = getBottomRightLocation(locations);
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

    /*
        Getter & Setter
     */

    public Location getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(Location deviceLocation) {
        this.deviceLocation = deviceLocation;
        updateEdgeLocations();
        updateMapping();
        invalidate();
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
        updateEdgeLocations();
        updateMapping();
        invalidate();
    }

}
