package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 03.01.18.
 */

public class MeanFilter implements RssiFilter {

    public static final long DURATION_DEFAULT = TimeUnit.SECONDS.toMillis(3);

    private long minimumTimestamp;
    private long maximumTimestamp;

    public MeanFilter() {
        maximumTimestamp = System.currentTimeMillis();
        minimumTimestamp = maximumTimestamp - DURATION_DEFAULT;
    }

    public MeanFilter(long minimumTimestamp, long maximumTimestamp) {
        this.minimumTimestamp = minimumTimestamp;
        this.maximumTimestamp = maximumTimestamp;
    }

    public MeanFilter(long duration, TimeUnit timeUnit) {
        this();
        this.minimumTimestamp = this.maximumTimestamp - timeUnit.toMillis(duration);
    }

    @Override
    public float filter(List<? extends AdvertisingPacket> advertisingPackets) {
        float rssiSum = 0;
        int rssiCount = 0;
        for (AdvertisingPacket advertisingPacket : advertisingPackets) {
            if (advertisingPacket.getTimestamp() < minimumTimestamp) {
                continue;
            }
            rssiSum += advertisingPacket.getRssi();
            rssiCount++;
        }
        return rssiSum / (float) rssiCount;
    }

    /*
        Getter & Setter
     */

    public long getMinimumTimestamp() {
        return minimumTimestamp;
    }

    public void setMinimumTimestamp(long minimumTimestamp) {
        this.minimumTimestamp = minimumTimestamp;
    }

    public long getMaximumTimestamp() {
        return maximumTimestamp;
    }

    public void setMaximumTimestamp(long maximumTimestamp) {
        this.maximumTimestamp = maximumTimestamp;
    }
}
