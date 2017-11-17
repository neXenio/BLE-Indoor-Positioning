package com.nexenio.bleindoorpositioningdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
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

    protected int textColor;

    protected Location deviceLocation;
    protected Location topLeftLocation;
    protected Location bottomRightLocation;
    protected List<Beacon> beacons = new ArrayList<>();

    protected double canvasAspectRatio;
    protected int canvasWidth;
    protected int canvasHeight;
    protected int canvasCenterX;
    protected int canvasCenterY;

    protected double mappedCanvasWidth;
    protected double mappedCanvasHeight;
    protected double offsetOriginWidth;
    protected double offsetOriginHeight;

    protected float pixelsPerDip = DisplayUtil.convertDipToPixels(1);

    protected CanvasProjection projection = new CanvasProjection();

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
        // colors
        textColor = Color.BLACK;

        // paints
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
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
    }

    @CallSuper
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.canvasCenterX = width / 2;
        this.canvasCenterY = height / 2;
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

    protected void drawDevice(Canvas canvas) {
        Point point = getPointFromLocation(deviceLocation);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 25, deviceRadiusPaint);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 10, whiteFillPaint);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 10, primaryStrokePaint);
        canvas.drawCircle(point.x, point.y, pixelsPerDip * 8, primaryFillPaint);
    }

    protected void drawBeacons(Canvas canvas) {
        for (Beacon beacon : beacons) {
            drawBeacon(canvas, beacon);
        }
    }

    protected void drawBeacon(Canvas canvas, Beacon beacon) {
        Point point = getPointFromLocation(beacon.getLocation());
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

    protected void updateMapping() {
        if (canvasWidth == 0 && canvasHeight == 0) {
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
