package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 03.01.18.
 */

public class MeanFilter extends WindowFilter {

    public MeanFilter() {
    }

    public MeanFilter(long duration, TimeUnit timeUnit) {
        super(duration, timeUnit);
    }

    @Override
    public float filter(Beacon beacon) {
        float sum = 0;
        int count = 0;
        List<AdvertisingPacket> recentAdvertisingPackets = getRecentAdvertisingPackets(beacon);
        for (AdvertisingPacket advertisingPacket : recentAdvertisingPackets) {
            if (advertisingPacket.getTimestamp() < minimumTimestamp) {
                continue;
            }
            sum += advertisingPacket.getRssi();
            count++;
        }
        return count > 0 ? (sum / (float) count) : 0;
    }
}
