package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 03.01.18.
 */

public class MeanFilter extends SignalFilter {

    public MeanFilter() {
    }

    public MeanFilter(long duration, TimeUnit timeUnit){
        super(duration,timeUnit);
    }

    @Override
    public float filter(List<? extends AdvertisingPacket> advertisingPackets) {
        float sum = 0;
        int count = 0;
        for (AdvertisingPacket advertisingPacket : advertisingPackets) {
            if (advertisingPacket.getTimestamp() < minimumTimestamp) {
                continue;
            }
            sum += advertisingPacket.getRssi();
            count++;
        }
        return count > 0 ? (sum / (float) count) : 0;
    }
}
