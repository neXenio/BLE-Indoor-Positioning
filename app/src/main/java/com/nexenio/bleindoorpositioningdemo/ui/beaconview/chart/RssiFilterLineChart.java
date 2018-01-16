package com.nexenio.bleindoorpositioningdemo.ui.beaconview.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.ArmaFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.KalmanFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.MeanFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.RssiFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.WindowFilter;
import com.nexenio.bleindoorpositioning.location.distance.BeaconDistanceCalculator;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.ColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 16.01.18.
 */

public class RssiFilterLineChart extends BeaconLineChart {

    protected long windowLength = WindowFilter.DEFAULT_DURATION;

    RssiFilter meanFilter = new MeanFilter();
    RssiFilter armaFilter = new ArmaFilter();
    RssiFilter kalmanFilter = new KalmanFilter();

    public RssiFilterLineChart(Context context) {
        super(context);
    }

    public RssiFilterLineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    protected void drawBeacon(Canvas canvas, Beacon beacon) {
        List<RssiFilter> filterList = new ArrayList<>();
        filterList.add(meanFilter);
        filterList.add(armaFilter);
        filterList.add(kalmanFilter);

        currentBeaconIndex = beacons.indexOf(beacon);

        for (RssiFilter filter : filterList) {
            lastLinePoint = null;
            lastAdvertisingPacket = null;

            linePaint.setShader(createLineShader(ColorUtil.getBeaconColor(currentBeaconIndex)));
            linePaint.setAlpha(128);

            for (AdvertisingPacket advertisingPacket : (List<AdvertisingPacket>) beacon.getAdvertisingPackets()) {

                if (advertisingPacket.getTimestamp() < minimumAdvertisingTimestamp) {
                    continue;
                }

                filter.setMinimumTimestamp(advertisingPacket.getTimestamp() - windowLength);
                filter.setMaximumTimestamp(advertisingPacket.getTimestamp());

                currentLinePoint = getPointFromAdvertisingPacket(beacon, advertisingPacket, currentLinePoint, filter);
                canvas.drawCircle(currentLinePoint.x, currentLinePoint.y, pixelsPerDip * 1.5f, linePaint);

                if (lastLinePoint != null && lastAdvertisingPacket != null) {
                    if (advertisingPacket.getTimestamp() < lastAdvertisingPacket.getTimestamp() + 5000) {
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

            currentBeaconIndex++;
            if (lastLinePoint != null) {
                linePaint.setAlpha(255);
                canvas.drawCircle(lastLinePoint.x, lastLinePoint.y, pixelsPerDip * 4, linePaint);
            }
        }
    }

    protected float getValue(Beacon beacon, AdvertisingPacket advertisingPacket, RssiFilter filter) {
        List<AdvertisingPacket> recentAdvertisingPackets = new ArrayList<>();
        float filteredRssi;

        // make sure that the window size is at least 10 seconds when we're looking for the frequency
        windowLength = (valueType != VALUE_TYPE_FREQUENCY) ? windowLength : Math.max(windowLength, 10000);

        if (windowLength == 0) {
            filteredRssi = advertisingPacket.getRssi();
        } else {
            recentAdvertisingPackets = beacon.getAdvertisingPacketsBetween(
                    advertisingPacket.getTimestamp() - windowLength,
                    advertisingPacket.getTimestamp()
            );
            filteredRssi = filter.filter(recentAdvertisingPackets);
        }

        switch (valueType) {
            case VALUE_TYPE_RSSI: {
                return filteredRssi;
            }
            case VALUE_TYPE_DISTANCE: {
                return BeaconDistanceCalculator.calculateDistanceTo(beacon, filteredRssi);
            }
            case VALUE_TYPE_FREQUENCY: {
                return 1000 * (recentAdvertisingPackets.size() / (float) windowLength);
            }
        }
        return 0;
    }

    protected PointF getPointFromAdvertisingPacket(Beacon beacon, AdvertisingPacket advertisingPacket, PointF point, RssiFilter filter) {
        if (point == null) {
            point = new PointF();
        }
        point.x = xAxisStartPoint.x + ((xAxisEndPoint.x - xAxisStartPoint.x) * (xAxisRange - (System.currentTimeMillis() - advertisingPacket.getTimestamp()))) / xAxisRange;
        point.y = yAxisStartPoint.y - ((yAxisStartPoint.y - yAxisEndPoint.y) * (getValue(beacon, advertisingPacket, filter) - (float) yAxisMinimumAnimator.getAnimatedValue())) / yAxisRange;
        return point;
    }

}
