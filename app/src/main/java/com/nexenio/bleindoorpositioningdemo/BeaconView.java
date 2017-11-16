package com.nexenio.bleindoorpositioningdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.nexenio.bleindoorpositioning.ble.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.projection.PixelProjection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steppschuh on 16.11.17.
 */

public abstract class BeaconView extends View {

    protected Paint backgroundPaint;
    protected Paint textPaint;
    protected Paint devicePaint;
    protected int textColor;

    protected Location deviceLocation;
    protected List<Beacon> beacons = new ArrayList<>();

    protected int width;
    protected int height;
    protected int centerX;
    protected int centerY;

    protected PixelProjection projection = new PixelProjection();

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
        textColor = Color.BLACK;

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        //textPaint.setTextSize(12);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);

        devicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        devicePaint.setColor(Color.BLACK);
        devicePaint.setStyle(Paint.Style.STROKE);
    }

    @CallSuper
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.width = width;
        this.height = height;

        this.centerX = width / 2;
        this.centerY = height / 2;

        //projection.setCanvasWidth(width);
        //projection.setCanvasHeight(height);

        projection.setCanvasWidth(1000 * 1000 * 10);
        projection.setCanvasHeight(1000 * 1000 * 10);
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
        canvas.drawRect(0, 0, width, height, backgroundPaint);
    }

    protected void drawDevice(Canvas canvas) {
        Point point = getPointFromLocation(deviceLocation);
        canvas.drawCircle(point.x, point.y, 10f, devicePaint);
    }

    protected void drawBeacons(Canvas canvas) {
        for (Beacon beacon : beacons) {
            drawBeacon(canvas, beacon);
        }
    }

    protected void drawBeacon(Canvas canvas, Beacon beacon) {
        Point point = getPointFromLocation(beacon.getLocation());
        canvas.drawCircle(point.x, point.y, 5f, devicePaint);
    }

    protected Point getPointFromLocation(Location location) {
        double locationWidth = projection.getWidthFromLongitude(location.getLongitude());
        double locationHeight = projection.getHeightFromLatitude(location.getLatitude());

        double topLeftWidth = projection.getWidthFromLongitude(TestLocations.GENDAMENMARKT_TOP_LEFT.getLongitude());
        double topLeftHeight = projection.getHeightFromLatitude(TestLocations.GENDAMENMARKT_TOP_RIGHT.getLatitude());

        double bottomRightWidth = projection.getWidthFromLongitude(TestLocations.GENDAMENMARKT_BOTTOM_RIGHT.getLongitude());
        double bottomRightHeight = projection.getHeightFromLatitude(TestLocations.GENDAMENMARKT_BOTTOM_RIGHT.getLatitude());

        double mappedWidth = bottomRightWidth - topLeftWidth;
        double mappedHeight = bottomRightHeight - topLeftHeight;

        double mappedLocationWidth = locationWidth - topLeftWidth;
        double mappedLocationHeight = locationHeight - topLeftHeight;

        double x = (mappedLocationWidth * width) / mappedWidth;
        double y = (mappedLocationHeight * height) / mappedHeight;

        return new Point((int) x, (int) y);
    }

    public Location getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(Location deviceLocation) {
        this.deviceLocation = deviceLocation;
        invalidate();
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
        invalidate();
    }

}
