package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 03.01.18.
 */

public class MeanFilter extends WindowFilter {

    public MeanFilter(long duration, TimeUnit timeUnit) {
        super(duration,timeUnit);
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
}
