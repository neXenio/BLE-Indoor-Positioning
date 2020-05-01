package com.nexenio.bleindoorpositioningdemo.ui.beaconview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.ui.DisplayUtil;
import com.nexenio.bleindoorpositioningdemo.ui.LocationAnimator;

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
    protected Paint secondaryFillPaint;
    protected Paint secondaryStrokePaint;
    protected Paint whiteFillPaint;
    protected Paint whiteStrokePaint;
    protected Paint deviceRangePaint;
    protected Paint beaconRangePaint;

    protected Location deviceLocation;
    protected LocationAnimator deviceLocationAnimator;
    protected List<Beacon> beacons = new ArrayList<>();

    protected int canvasWidth;
    protected int canvasHeight;
    protected PointF canvasCenter;

    protected float pixelsPerDip = DisplayUtil.convertDipToPixels(1);

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
        textPaint.setTextSize(pixelsPerDip * 8);

        primaryFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        primaryFillPaint.setStyle(Paint.Style.FILL);
        primaryFillPaint.setColor(ContextCompat.getColor(getContext(), R.color.primary));

        primaryStrokePaint = new Paint(primaryFillPaint);
        primaryStrokePaint.setStyle(Paint.Style.STROKE);
        primaryStrokePaint.setStrokeWidth(pixelsPerDip);

        secondaryFillPaint = new Paint(primaryFillPaint);
        secondaryFillPaint.setColor(ContextCompat.getColor(getContext(), R.color.accent));

        secondaryStrokePaint = new Paint(primaryStrokePaint);
        secondaryStrokePaint.setColor(ContextCompat.getColor(getContext(), R.color.accent));

        whiteFillPaint = new Paint(primaryFillPaint);
        whiteFillPaint.setColor(Color.WHITE);

        whiteStrokePaint = new Paint(primaryStrokePaint);
        whiteStrokePaint.setColor(Color.WHITE);

        beaconRangePaint = new Paint(primaryFillPaint);
        beaconRangePaint.setAlpha(25);

        deviceRangePaint = new Paint(secondaryFillPaint);
        deviceRangePaint.setAlpha(25);

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
        this.canvasCenter = new PointF(width / 2, height / 2);
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

    protected abstract PointF getPointFromLocation(Location location);

    public void onLocationsChanged() {
        invalidate();
    }

    public void onDeviceLocationChanged() {
        deviceLocationAnimator = startLocationAnimation(deviceLocationAnimator, deviceLocation, new LocationListener() {
            @Override
            public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                onLocationsChanged();
            }
        });
    }

    protected LocationAnimator startLocationAnimation(LocationAnimator locationAnimator, Location targetLocation, LocationListener locationListener) {
        if (targetLocation == null) {
            return locationAnimator;
        }

        Location originLocation = targetLocation;
        if (locationAnimator != null) {
            locationAnimator.cancel();
            originLocation = locationAnimator.getLocation();
        }

        locationAnimator = new LocationAnimator(originLocation, targetLocation);
        locationAnimator.setLocationListener(locationListener);
        locationAnimator.start();
        return locationAnimator;
    }

    @ColorInt
    public static int getBeaconColor(Beacon beacon, @ColorUtil.ColoringMode int coloringMode, int beaconIndex) {
        int colorIndex = 0;
        switch (coloringMode) {
            case ColorUtil.COLORING_MODE_INSTANCES: {
                if (beacon instanceof IBeacon) {
                    colorIndex = ((IBeacon) beacon).getMinor();
                } else {
                    colorIndex = beaconIndex;
                }
                break;
            }
            case ColorUtil.COLORING_MODE_TYPES: {
                if (beacon instanceof IBeacon) {
                    colorIndex = 0;
                } else {
                    colorIndex = 1;
                }
                break;
            }
            case ColorUtil.COLORING_MODE_PROPERTIES: {
                if (beacon instanceof IBeacon) {
                    colorIndex = ((IBeacon) beacon).getMajor();
                } else {
                    // TODO: use Eddystone UID
                    colorIndex = beaconIndex;
                }
                break;
            }
        }
        return ColorUtil.getBeaconColor(colorIndex);
    }

    /*
        Getter & Setter
     */

    public Location getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(Location deviceLocation) {
        this.deviceLocation = deviceLocation;
        onDeviceLocationChanged();
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
        onLocationsChanged();
    }

}
