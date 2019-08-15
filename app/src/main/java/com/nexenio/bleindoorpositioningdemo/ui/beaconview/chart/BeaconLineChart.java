package com.nexenio.bleindoorpositioningdemo.ui.beaconview.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacketUtil;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.WindowFilter;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.distance.BeaconDistanceCalculator;
import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.ColorUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 11.12.17.
 */

public class BeaconLineChart extends BeaconChart {

    public static final long MINIMUM_WINDOW_LENGTH_FREQUENCY = TimeUnit.SECONDS.toMillis(10);
    public static final long MINIMUM_WINDOW_LENGTH_VARIANCE = TimeUnit.SECONDS.toMillis(10);

    protected long windowLength = WindowFilter.DEFAULT_DURATION;

    protected Paint axisPaint;
    protected Paint axisLabelPaint;
    protected Paint gridPaint;

    protected PointF xAxisStartPoint;
    protected PointF xAxisEndPoint;

    protected PointF yAxisStartPoint;
    protected PointF yAxisEndPoint;

    protected PointF xAxisLabelCenter;
    protected PointF yAxisLabelCenter;

    protected ValueAnimator xAxisMinimumAnimator;
    protected ValueAnimator xAxisMaximumAnimator;

    protected ValueAnimator yAxisMinimumAnimator;
    protected ValueAnimator yAxisMaximumAnimator;

    protected String xAxisLabel;
    protected String yAxisLabel;

    protected float axisMargin;
    protected float axisWidth;

    protected long xAxisRange;
    protected float yAxisRange;

    protected PointF gridLineStartPoint;
    protected PointF gridLineEndPoint;

    protected int gridLineCount;
    protected float gridLineValue;
    protected String gridLineText;
    protected float gridLineTextWidth;
    protected float gridLinePixelDelta;
    protected float gridLineValueDelta;

    protected Shader fadeOutShader;

    protected long minimumAdvertisingTimestamp;
    protected int currentBeaconIndex;
    protected Paint linePaint;
    protected PointF currentLinePoint;
    protected PointF lastLinePoint;
    protected AdvertisingPacket lastAdvertisingPacket;

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

        xAxisMaximumAnimator = startValueAnimator(xAxisMaximumAnimator, 0);
        xAxisMinimumAnimator = startValueAnimator(xAxisMinimumAnimator, -TimeUnit.SECONDS.toMillis(30));
        yAxisMaximumAnimator = startValueAnimator(yAxisMaximumAnimator, 0);
        yAxisMinimumAnimator = startValueAnimator(yAxisMinimumAnimator, -100);

        axisMargin = pixelsPerDip * 40;
        axisWidth = pixelsPerDip * 2;

        axisPaint = new Paint(textPaint);
        axisPaint.setAlpha(50);
        axisPaint.setStrokeWidth(axisWidth);

        axisLabelPaint = new Paint(textPaint);
        axisLabelPaint.setAlpha(50);
        axisLabelPaint.setTextSize(pixelsPerDip * 12);

        gridPaint = new Paint(axisPaint);
        gridPaint.setStrokeWidth(pixelsPerDip);
        gridPaint.setAlpha(15);

        linePaint = new Paint(primaryFillPaint);
        linePaint.setStrokeWidth(pixelsPerDip);

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
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        xAxisStartPoint = new PointF(axisMargin, canvasHeight - axisMargin);
        xAxisEndPoint = new PointF(canvasWidth - axisMargin, xAxisStartPoint.y);

        yAxisStartPoint = new PointF(xAxisStartPoint.x, xAxisStartPoint.y);
        yAxisEndPoint = new PointF(xAxisStartPoint.x, axisMargin);

        xAxisLabelCenter = new PointF(
                xAxisStartPoint.x + ((xAxisEndPoint.x - xAxisStartPoint.x) / 2) - (axisLabelPaint.measureText(xAxisLabel) / 2),
                xAxisStartPoint.y - (pixelsPerDip * 8)
        );

        yAxisLabelCenter = new PointF(
                yAxisStartPoint.x + (pixelsPerDip * 16),
                yAxisStartPoint.y + ((yAxisEndPoint.y - yAxisStartPoint.y) / 2) + (axisLabelPaint.measureText(yAxisLabel) / 2)
        );

        gridLineStartPoint = new PointF(xAxisStartPoint.x, xAxisStartPoint.y);
        gridLineEndPoint = new PointF(gridLineStartPoint.x, gridLineStartPoint.y + (pixelsPerDip * 4));
    }

    @Override
    protected void onValueTypeChanged() {
        super.onValueTypeChanged();
        switch (valueType) {
            case VALUE_TYPE_RSSI: {
                yAxisLabel = getContext().getString(R.string.axis_label_rssi);
                yAxisMaximumAnimator = startValueAnimator(yAxisMaximumAnimator, 0);
                yAxisMinimumAnimator = startValueAnimator(yAxisMinimumAnimator, -100);
                break;
            }
            case VALUE_TYPE_RSSI_FILTERED: {
                yAxisLabel = getContext().getString(R.string.axis_label_rssi_filtered);
                yAxisMaximumAnimator = startValueAnimator(yAxisMaximumAnimator, 0);
                yAxisMinimumAnimator = startValueAnimator(yAxisMinimumAnimator, -100);
                break;
            }
            case VALUE_TYPE_DISTANCE: {
                yAxisLabel = getContext().getString(R.string.axis_label_distance);
                yAxisMaximumAnimator = startValueAnimator(yAxisMaximumAnimator, 25);
                yAxisMinimumAnimator = startValueAnimator(yAxisMinimumAnimator, 0);
                break;
            }
            case VALUE_TYPE_FREQUENCY: {
                yAxisLabel = getContext().getString(R.string.axis_label_frequency);
                yAxisMaximumAnimator = startValueAnimator(yAxisMaximumAnimator, 10);
                yAxisMinimumAnimator = startValueAnimator(yAxisMinimumAnimator, 0);
                break;
            }
            case VALUE_TYPE_VARIANCE: {
                yAxisLabel = getContext().getString(R.string.axis_label_variance);
                yAxisMaximumAnimator = startValueAnimator(yAxisMaximumAnimator, 100);
                yAxisMinimumAnimator = startValueAnimator(yAxisMinimumAnimator, 0);
                break;
            }
        }
        if (yAxisLabelCenter != null) {
            yAxisLabelCenter.y = yAxisStartPoint.y + ((yAxisEndPoint.y - yAxisStartPoint.y) / 2) + (axisLabelPaint.measureText(yAxisLabel) / 2);
        }
    }

    @Override
    protected void onColoringModeChanged() {
        super.onColoringModeChanged();

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
        // x axis
        canvas.drawLine(xAxisStartPoint.x, xAxisStartPoint.y, xAxisEndPoint.x, xAxisEndPoint.y, axisPaint);

        // y axis
        canvas.drawLine(yAxisStartPoint.x, yAxisStartPoint.y, yAxisEndPoint.x, yAxisEndPoint.y, axisPaint);

        // x axis label
        canvas.drawText(xAxisLabel, xAxisLabelCenter.x, xAxisLabelCenter.y, axisLabelPaint);

        // y axis label
        canvas.save();
        canvas.rotate(-90f, yAxisLabelCenter.x, yAxisLabelCenter.y);
        canvas.drawText(yAxisLabel, yAxisLabelCenter.x, yAxisLabelCenter.y, axisLabelPaint);
        canvas.restore();

        // x axis grid values
        gridLineCount = 6;
        gridLineStartPoint.x = xAxisStartPoint.x;
        gridLineStartPoint.y = xAxisStartPoint.y;
        gridLineEndPoint.x = gridLineStartPoint.x;
        gridLineEndPoint.y = gridLineStartPoint.y + (pixelsPerDip * 4);
        gridLinePixelDelta = (canvasWidth - (2 * axisMargin)) / (float) gridLineCount;
        gridLineValueDelta = xAxisRange / (float) gridLineCount;
        for (int referenceIndex = 0; referenceIndex <= gridLineCount; referenceIndex++) {
            gridLineStartPoint.x = xAxisStartPoint.x + (referenceIndex * gridLinePixelDelta);
            gridLineEndPoint.x = gridLineStartPoint.x;

            canvas.drawLine(
                    gridLineStartPoint.x,
                    gridLineStartPoint.y,
                    gridLineEndPoint.x,
                    gridLineEndPoint.y,
                    axisPaint
            );

            canvas.drawLine(
                    gridLineStartPoint.x,
                    yAxisStartPoint.y,
                    gridLineStartPoint.x,
                    yAxisEndPoint.y,
                    gridPaint
            );

            gridLineValue = (float) xAxisMinimumAnimator.getAnimatedValue() + (referenceIndex * gridLineValueDelta);
            gridLineText = String.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) gridLineValue));
            gridLineTextWidth = axisLabelPaint.measureText(gridLineText);
            canvas.drawText(
                    gridLineText,
                    gridLineStartPoint.x - (gridLineTextWidth / 2),
                    gridLineStartPoint.y + (pixelsPerDip * 18),
                    axisLabelPaint
            );
        }

        // y axis grid values
        gridLineCount = 5;
        gridLineStartPoint.x = yAxisStartPoint.x;
        gridLineEndPoint.x = gridLineStartPoint.x - (pixelsPerDip * 4);
        gridLinePixelDelta = (canvasHeight - (2 * axisMargin)) / (float) gridLineCount;
        gridLineValueDelta = yAxisRange / (float) gridLineCount;
        for (int referenceIndex = 0; referenceIndex <= gridLineCount; referenceIndex++) {
            gridLineStartPoint.y = xAxisStartPoint.y - (referenceIndex * gridLinePixelDelta);
            gridLineEndPoint.y = gridLineStartPoint.y;

            canvas.drawLine(
                    gridLineStartPoint.x,
                    gridLineStartPoint.y,
                    gridLineEndPoint.x,
                    gridLineEndPoint.y,
                    axisPaint
            );

            canvas.drawLine(
                    xAxisStartPoint.x,
                    gridLineStartPoint.y,
                    xAxisEndPoint.x,
                    gridLineStartPoint.y,
                    gridPaint
            );

            gridLineValue = (float) yAxisMinimumAnimator.getAnimatedValue() + (referenceIndex * gridLineValueDelta);
            gridLineText = String.valueOf(Math.round(gridLineValue));
            gridLineTextWidth = axisLabelPaint.measureText(gridLineText);
            canvas.drawText(
                    gridLineText,
                    gridLineStartPoint.x - gridLineTextWidth - (pixelsPerDip * 10),
                    gridLineStartPoint.y + (pixelsPerDip * 4),
                    axisLabelPaint
            );
        }
    }

    @Override
    protected void drawDevice(Canvas canvas) {

    }

    @Override
    protected void drawBeacons(Canvas canvas) {
        minimumAdvertisingTimestamp = System.currentTimeMillis() - (long) (xAxisRange * 1.25f);
        super.drawBeacons(canvas);
    }

    @Override
    protected void drawBeacon(Canvas canvas, Beacon beacon) {
        prepareDraw(beacon);
        for (AdvertisingPacket advertisingPacket : (List<AdvertisingPacket>) beacon.getAdvertisingPackets()) {
            if (advertisingPacket.getTimestamp() < minimumAdvertisingTimestamp) {
                continue;
            }
            currentLinePoint = getPointFromAdvertisingPacket(beacon, advertisingPacket, currentLinePoint);
            drawCircleForPreviousPoint(canvas);
            drawNextPoint(canvas, advertisingPacket);
        }
        currentBeaconIndex++;
        fadeLastPoint(canvas);
    }

    protected void drawCircleForPreviousPoint(Canvas canvas) {
        canvas.drawCircle(currentLinePoint.x, currentLinePoint.y, pixelsPerDip * 1.5f, linePaint);
    }

    protected void prepareDraw(Beacon beacon) {
        lastLinePoint = null;
        lastAdvertisingPacket = null;

        linePaint.setShader(createLineShader(getBeaconColor(beacon, coloringMode, currentBeaconIndex)));
        linePaint.setAlpha(128);
    }

    protected void drawNextPoint(Canvas canvas, AdvertisingPacket advertisingPacket) {
        if (lastLinePoint != null && lastAdvertisingPacket != null) {
            if (advertisingPacket.getTimestamp() < lastAdvertisingPacket.getTimestamp() + TimeUnit.SECONDS.toMillis(5)) {
                canvas.drawLine(
                        lastLinePoint.x,
                        lastLinePoint.y,
                        currentLinePoint.x,
                        currentLinePoint.y,
                        linePaint
                );
            }
        } else {
            lastLinePoint = new PointF();
        }

        lastLinePoint.x = currentLinePoint.x;
        lastLinePoint.y = currentLinePoint.y;
        lastAdvertisingPacket = advertisingPacket;
    }

    protected void fadeLastPoint(Canvas canvas) {
        if (lastLinePoint != null) {
            linePaint.setAlpha(255);
            canvas.drawCircle(lastLinePoint.x, lastLinePoint.y, pixelsPerDip * 4, linePaint);
        }
    }

    protected float getValue(Beacon beacon, AdvertisingPacket advertisingPacket) {
        WindowFilter filter = beacon.createSuggestedWindowFilter();
        filter.setMaximumTimestamp(advertisingPacket.getTimestamp());
        filter.setMinimumTimestamp(advertisingPacket.getTimestamp() - windowLength);
        return processReturnValue(beacon, advertisingPacket, beacon.getRssi(filter));
    }

    protected float processReturnValue(Beacon beacon, AdvertisingPacket advertisingPacket, float rssi) {
        switch (valueType) {
            case VALUE_TYPE_RSSI: {
                return advertisingPacket.getRssi();
            }
            case VALUE_TYPE_RSSI_FILTERED: {
                return rssi;
            }
            case VALUE_TYPE_DISTANCE: {
                return BeaconDistanceCalculator.calculateDistanceTo(beacon, rssi);
            }
            case VALUE_TYPE_FREQUENCY: {
                long windowLength = Math.max(this.windowLength, MINIMUM_WINDOW_LENGTH_FREQUENCY);
                List<AdvertisingPacket> recentAdvertisingPackets = beacon.getAdvertisingPacketsBetween(
                        advertisingPacket.getTimestamp() - windowLength,
                        advertisingPacket.getTimestamp()
                );
                // convert frequency from milliseconds to seconds to receive Hertz
                return TimeUnit.SECONDS.toMillis(1) * (recentAdvertisingPackets.size() / (float) windowLength);
            }
            case VALUE_TYPE_VARIANCE: {
                long windowLength = Math.max(this.windowLength, MINIMUM_WINDOW_LENGTH_VARIANCE);
                List<AdvertisingPacket> recentAdvertisingPackets = beacon.getAdvertisingPacketsBetween(
                        advertisingPacket.getTimestamp() - windowLength,
                        advertisingPacket.getTimestamp()
                );
                int[] recentRssis = AdvertisingPacketUtil.getRssisFromAdvertisingPackets(recentAdvertisingPackets);
                return AdvertisingPacketUtil.calculateVariance(recentRssis);
            }
        }
        return 0;
    }

    protected PointF getPointFromAdvertisingPacket(Beacon beacon, AdvertisingPacket advertisingPacket, PointF point) {
        if (point == null) {
            point = new PointF();
        }
        point.x = xAxisStartPoint.x + ((xAxisEndPoint.x - xAxisStartPoint.x) * (xAxisRange - (System.currentTimeMillis() - advertisingPacket.getTimestamp()))) / xAxisRange;
        point.y = yAxisStartPoint.y - ((yAxisStartPoint.y - yAxisEndPoint.y) * (getValue(beacon, advertisingPacket) - (float) yAxisMinimumAnimator.getAnimatedValue())) / yAxisRange;
        return point;
    }

    @Override
    protected PointF getPointFromLocation(Location location) {
        return null;
    }

    protected Shader createLineShader(@ColorInt int color) {
        return new LinearGradient(axisMargin * 1.5f, 0, axisMargin * 4, 0, Color.TRANSPARENT, color, Shader.TileMode.CLAMP);
    }

}
