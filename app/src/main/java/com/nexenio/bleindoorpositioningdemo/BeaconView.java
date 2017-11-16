package com.nexenio.bleindoorpositioningdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.nexenio.bleindoorpositioning.ble.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steppschuh on 16.11.17.
 */

public abstract class BeaconView extends View {

    protected Paint backgroundPaint;
    protected Paint textPaint;
    protected int textColor;

    protected Location deviceLocation;
    protected List<Beacon> beacons = new ArrayList<>();

    protected int width;
    protected int height;

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
    }

    @CallSuper
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
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

    }

    protected void drawBeacons(Canvas canvas) {
        for (Beacon beacon : beacons) {
            drawBeacon(canvas, beacon);
        }
    }

    protected void drawBeacon(Canvas canvas, Beacon beacon) {

    }

    protected Point getPointFromLocation(Location location) {
        int x = 0;
        int y = 0;
        return new Point(x, y);
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
