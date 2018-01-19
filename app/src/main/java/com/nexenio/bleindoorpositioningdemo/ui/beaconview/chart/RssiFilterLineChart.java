package com.nexenio.bleindoorpositioningdemo.ui.beaconview.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.ArmaFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.KalmanFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.RssiFilter;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.ColorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leon on 16.01.18.
 */

public class RssiFilterLineChart extends BeaconLineChart {

    protected BeaconManager beaconManager = BeaconManager.getInstance();

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

        beacon = beaconManager.getClosestBeacon();
        //TODO prevent switches between beacons

        currentBeaconIndex = beacons.indexOf(beacon);
        int filterIndex = 0;
        for (RssiFilter filter : filterList) {
            filterIndex++;
            prepareDraw(beacon);
            linePaint.setShader(createLineShader(ColorUtil.getBeaconColor(currentBeaconIndex + filterIndex)));

            for (AdvertisingPacket advertisingPacket : (List<AdvertisingPacket>) beacon.getAdvertisingPackets()) {
                if (advertisingPacket.getTimestamp() < minimumAdvertisingTimestamp) {
                    continue;
                }

                filter.setMinimumTimestamp(advertisingPacket.getTimestamp() - windowLength);
                filter.setMaximumTimestamp(advertisingPacket.getTimestamp());

                currentLinePoint = getPointFromAdvertisingPacket(beacon, advertisingPacket, currentLinePoint, filter);
                drawNextPoint(canvas, beacon, advertisingPacket);
            }

            currentBeaconIndex++;
            fadeLastPoint(canvas);
        }
    }

}
