package com.nexenio.bleindoorpositioningdemo.ui.beaconview.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioningdemo.R;

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

    protected String xAxisLabel;
    protected String yAxisLabel;

    protected float axisMargin;
    protected float axisWidth;

    protected long xAxisRange;
    protected float yAxisRange;

    protected Shader fadeOutShader;

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

        ColorUtil.initialize(getContext());

        xAxisLabel = getContext().getString(R.string.axis_label_time);
        yAxisLabel = getContext().getString(R.string.axis_label_rssi);

        xAxisMaximumAnimator = startValueAnimator(xAxisMaximumAnimator, 0);
        xAxisMinimumAnimator = startValueAnimator(xAxisMinimumAnimator, -TimeUnit.SECONDS.toMillis(30));
        yAxisMaximumAnimator = startValueAnimator(yAxisMaximumAnimator, 0);
        yAxisMinimumAnimator = startValueAnimator(yAxisMinimumAnimator, -100);

        axisMargin = pixelsPerDip * 40;
        axisWidth = pixelsPerDip * 2;

        fadeOutShader = createLineShader(primaryStrokePaint.getColor());
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
        xAxisRange = (long) ((float) xAxisMaximumAnimator.getAnimatedValue() - (float) xAxisMinimumAnimator.getAnimatedValue());
        yAxisRange = (float) yAxisMaximumAnimator.getAnimatedValue() - (float) yAxisMinimumAnimator.getAnimatedValue();
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
        axisPaint.setStrokeWidth(axisWidth);

        Paint gridPaint = new Paint(axisPaint);
        gridPaint.setStrokeWidth(pixelsPerDip);
        gridPaint.setAlpha(15);

        // x axis
        PointF xAxisStartPoint = new PointF(axisMargin, canvasHeight - axisMargin);
        PointF xAxisEndPoint = new PointF(canvasWidth - axisMargin, xAxisStartPoint.y);
        canvas.drawLine(xAxisStartPoint.x, xAxisStartPoint.y, xAxisEndPoint.x, xAxisEndPoint.y, axisPaint);

        // y axis
        PointF yAxisStartPoint = new PointF(xAxisStartPoint.x, axisMargin);
        PointF yAxisEndPoint = new PointF(xAxisStartPoint.x, xAxisStartPoint.y);
        canvas.drawLine(yAxisStartPoint.x, yAxisStartPoint.y, yAxisEndPoint.x, yAxisEndPoint.y, axisPaint);

        // labels
        Paint axisLabelPaint = new Paint(textPaint);
        axisLabelPaint.setAlpha(50);
        axisLabelPaint.setTextSize(pixelsPerDip * 12);

        // x axis label
        PointF xAxisLabelCenter = new PointF(
                xAxisStartPoint.x + ((xAxisEndPoint.x - xAxisStartPoint.x) / 2) - (axisLabelPaint.measureText(xAxisLabel) / 2),
                xAxisStartPoint.y - (pixelsPerDip * 8)
        );
        canvas.drawText(xAxisLabel, xAxisLabelCenter.x, xAxisLabelCenter.y, axisLabelPaint);

        // y axis label
        PointF yAxisLabelCenter = new PointF(
                yAxisStartPoint.x + (pixelsPerDip * 16),
                yAxisStartPoint.y + ((yAxisEndPoint.y - yAxisStartPoint.y) / 2) + (axisLabelPaint.measureText(yAxisLabel) / 2)
        );
        canvas.save();
        canvas.rotate(-90f, yAxisLabelCenter.x, yAxisLabelCenter.y);
        canvas.drawText(yAxisLabel, yAxisLabelCenter.x, yAxisLabelCenter.y, axisLabelPaint);
        canvas.restore();

        // reference values
        int referenceLineCount;
        float referenceValue;
        String referenceText;
        float referenceTextWidth;
        float axisReferencePixelDelta;
        float axisReferenceValueDelta;
        PointF referenceStartPoint = new PointF(xAxisStartPoint.x, xAxisStartPoint.y);
        PointF referenceEndPoint = new PointF(referenceStartPoint.x, referenceStartPoint.y + (pixelsPerDip * 4));

        // x axis reference values
        referenceLineCount = 6;
        axisReferencePixelDelta = (canvasWidth - (2 * axisMargin)) / (float) referenceLineCount;
        axisReferenceValueDelta = xAxisRange / (float) referenceLineCount;
        for (int referenceIndex = 0; referenceIndex <= referenceLineCount; referenceIndex++) {
            referenceStartPoint.x = xAxisStartPoint.x + (referenceIndex * axisReferencePixelDelta);
            referenceEndPoint.x = referenceStartPoint.x;

            canvas.drawLine(
                    referenceStartPoint.x,
                    referenceStartPoint.y,
                    referenceEndPoint.x,
                    referenceEndPoint.y,
                    axisPaint
            );

            canvas.drawLine(
                    referenceStartPoint.x,
                    yAxisStartPoint.y,
                    referenceStartPoint.x,
                    yAxisEndPoint.y,
                    gridPaint
            );

            referenceValue = (float) xAxisMinimumAnimator.getAnimatedValue() + (referenceIndex * axisReferenceValueDelta);
            referenceText = String.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) referenceValue));
            referenceTextWidth = axisLabelPaint.measureText(referenceText);
            canvas.drawText(
                    referenceText,
                    referenceStartPoint.x - (referenceTextWidth / 2),
                    referenceStartPoint.y + (pixelsPerDip * 18),
                    axisLabelPaint
            );
        }

        // y axis reference values
        referenceLineCount = 5;
        referenceStartPoint.x = yAxisStartPoint.x;
        referenceEndPoint.x = referenceStartPoint.x - (pixelsPerDip * 4);
        axisReferencePixelDelta = (canvasHeight - (2 * axisMargin)) / (float) referenceLineCount;
        axisReferenceValueDelta = yAxisRange / (float) referenceLineCount;
        for (int referenceIndex = 0; referenceIndex <= referenceLineCount; referenceIndex++) {
            referenceStartPoint.y = xAxisStartPoint.y - (referenceIndex * axisReferencePixelDelta);
            referenceEndPoint.y = referenceStartPoint.y;

            canvas.drawLine(
                    referenceStartPoint.x,
                    referenceStartPoint.y,
                    referenceEndPoint.x,
                    referenceEndPoint.y,
                    axisPaint
            );

            canvas.drawLine(
                    xAxisStartPoint.x,
                    referenceStartPoint.y,
                    xAxisEndPoint.x,
                    referenceStartPoint.y,
                    gridPaint
            );

            referenceValue = (float) yAxisMinimumAnimator.getAnimatedValue() + (referenceIndex * axisReferenceValueDelta);
            referenceText = String.valueOf(Math.round(referenceValue));
            referenceTextWidth = axisLabelPaint.measureText(referenceText);
            canvas.drawText(
                    referenceText,
                    referenceStartPoint.x - referenceTextWidth - (pixelsPerDip * 10),
                    referenceStartPoint.y + (pixelsPerDip * 4),
                    axisLabelPaint
            );
        }
    }

    @Override
    protected void drawDevice(Canvas canvas) {

    }

    @Override
    protected void drawBeacons(Canvas canvas) {
        super.drawBeacons(canvas);
    }

    @Override
    protected void drawBeacon(Canvas canvas, Beacon beacon) {
        int beaconIndex = beacons.indexOf(beacon);
        long minimumTimestamp = System.currentTimeMillis() - (long) (xAxisRange * 1.25f);

        @ColorInt
        int lineColor = ColorUtil.getBeaconColor(beaconIndex);

        Paint linePaint = new Paint(primaryFillPaint);
        linePaint.setShader(createLineShader(lineColor));
        linePaint.setStrokeWidth(pixelsPerDip);
        linePaint.setAlpha(128);

        PointF currentPoint;
        PointF lastPoint = null;
        AdvertisingPacket lastAdvertisingPacket = null;
        for (AdvertisingPacket advertisingPacket : beacon.getAdvertisingPackets()) {
            if (advertisingPacket.getTimestamp() < minimumTimestamp) {
                continue;
            }
            if (advertisingPacket.getRssi() < (float) yAxisMinimumAnimator.getAnimatedValue()) {
                continue;
            }

            currentPoint = getPointFromAdvertisingPacket(advertisingPacket);
            canvas.drawCircle(currentPoint.x, currentPoint.y, pixelsPerDip * 1.5f, linePaint);

            if (lastPoint != null && lastAdvertisingPacket != null) {
                if (advertisingPacket.getTimestamp() < lastAdvertisingPacket.getTimestamp() + 5000) {
                    canvas.drawLine(
                            lastPoint.x,
                            lastPoint.y,
                            currentPoint.x,
                            currentPoint.y,
                            linePaint
                    );
                }
            }

            lastPoint = currentPoint;
            lastAdvertisingPacket = advertisingPacket;
            beaconIndex++;
        }

        if (lastPoint != null) {
            linePaint.setAlpha(255);
            canvas.drawCircle(lastPoint.x, lastPoint.y, pixelsPerDip * 4, linePaint);
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

    protected Shader createLineShader(@ColorInt int color) {
        return new LinearGradient(axisMargin * 1.5f, 0, axisMargin * 4, 0, Color.TRANSPARENT, color, Shader.TileMode.CLAMP);
    }

}
