package com.nexenio.bleindoorpositioningdemo.ui.beaconview.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;

import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 11.12.17.
 */

public class BeaconLineChart extends BeaconChart {

    protected ValueAnimator xAxisMinimumAnimator;
    protected ValueAnimator xAxisMaximumAnimator;

    protected ValueAnimator yAxisMinimumAnimator;
    protected ValueAnimator yAxisMaximumAnimator;

    protected ValueAnimator xAxisStepAnimator;
    protected ValueAnimator yAxisStepAnimator;

    protected float axisMargin = pixelsPerDip * 32;
    protected float axisWidth = pixelsPerDip * 2;

    protected long xAxisRange;
    protected float yAxisRange;

    public BeaconLineChart(Context context) {
        super(context);
    }

    public BeaconLineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BeaconLineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BeaconLineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize() {
        super.initialize();
        xAxisMaximumAnimator = startValueAnimator(xAxisMaximumAnimator, 0);
        xAxisMinimumAnimator = startValueAnimator(xAxisMinimumAnimator, -TimeUnit.SECONDS.toMillis(30));
        yAxisMaximumAnimator = startValueAnimator(yAxisMaximumAnimator, 0);
        yAxisMinimumAnimator = startValueAnimator(yAxisMinimumAnimator, -100);
    }

    protected ValueAnimator startValueAnimator(ValueAnimator valueAnimator, float targetValue) {
        float originValue = targetValue;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            originValue = (float) valueAnimator.getAnimatedValue();
        }

        valueAnimator = ValueAnimator.ofFloat(originValue, targetValue);
        valueAnimator.setDuration(200);
        valueAnimator.start();
        return valueAnimator;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        invalidate();
    }

    @Override
    protected void drawBackground(Canvas canvas) {
        super.drawBackground(canvas);
        drawAxis(canvas);
    }

    protected void drawAxis(Canvas canvas) {

        Paint axisPaint = new Paint(textPaint);
        axisPaint.setAlpha(50);

        // x axis
        xAxisRange = (long) ((float) xAxisMaximumAnimator.getAnimatedValue() - (float) xAxisMinimumAnimator.getAnimatedValue());
        PointF xAxisStartPoint = new PointF(axisMargin, canvasHeight - axisMargin - axisWidth);
        PointF xAxisEndPoint = new PointF(canvasWidth - axisMargin, canvasHeight - axisMargin);

        canvas.drawRect(
                xAxisStartPoint.x,
                xAxisStartPoint.y,
                xAxisEndPoint.x,
                xAxisEndPoint.y,
                axisPaint
        );

        // y axis
        yAxisRange = (float) yAxisMaximumAnimator.getAnimatedValue() - (float) yAxisMinimumAnimator.getAnimatedValue();
        PointF yAxisStartPoint = new PointF(xAxisStartPoint.x, axisMargin);
        PointF yAxisEndPoint = new PointF(xAxisStartPoint.x + axisWidth, xAxisStartPoint.y);

        canvas.drawRect(
                yAxisStartPoint.x,
                yAxisStartPoint.y,
                yAxisEndPoint.x,
                yAxisEndPoint.y,
                axisPaint
        );

    }

    @Override
    protected void drawDevice(Canvas canvas) {

    }

    @Override
    protected void drawBeacon(Canvas canvas, Beacon beacon) {
        long minimumTimestamp = System.currentTimeMillis() - xAxisRange;

        Paint linePaint = new Paint(primaryFillPaint);
        linePaint.setStrokeWidth(pixelsPerDip);

        PointF lastPoint = null;

        for (AdvertisingPacket advertisingPacket : beacon.getAdvertisingPackets()) {
            if (advertisingPacket.getTimestamp() < minimumTimestamp) {
                continue;
            }
            PointF point = getPointFromAdvertisingPacket(advertisingPacket);

            if (lastPoint != null) {
                canvas.drawLine(
                        lastPoint.x,
                        lastPoint.y,
                        point.x,
                        point.y,
                        linePaint
                );
            }

            lastPoint = point;
        }

        if (lastPoint != null) {
            canvas.drawCircle(lastPoint.x, lastPoint.y, pixelsPerDip * 4, primaryFillPaint);
        }
    }

    protected PointF getPointFromAdvertisingPacket(AdvertisingPacket advertisingPacket) {
        float x;
        float y;

        // map timestamp to x axis
        float xAxisWidth = canvasWidth - (2 * axisMargin);
        long packetAge = System.currentTimeMillis() - advertisingPacket.getTimestamp();
        x = (xAxisWidth * (xAxisRange - packetAge)) / xAxisRange;
        x += axisMargin;

        // map RSSI to y axis
        float yAxisHeight = canvasHeight - (2 * axisMargin);
        float mappedRssi = advertisingPacket.getRssi() - (float) yAxisMinimumAnimator.getAnimatedValue();
        y = (yAxisHeight * (yAxisRange - mappedRssi)) / yAxisRange;
        y += axisMargin;

        return new PointF(x, y);
    }

    @Override
    protected PointF getPointFromLocation(Location location) {
        return null;
    }

}
