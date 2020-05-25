package com.nexenio.bleindoorpositioningdemo.recording;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;

import java.util.Map;

public class IndoorPositioningRecording {

    private long startTimestamp;

    private long endTimestamp;

    private Map<Long, AdvertisingPacket> advertisingPacketMap;

    public IndoorPositioningRecording() {
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Map<Long, AdvertisingPacket> getAdvertisingPacketMap() {
        return advertisingPacketMap;
    }

    public void setAdvertisingPacketMap(Map<Long, AdvertisingPacket> advertisingPacketMap) {
        this.advertisingPacketMap = advertisingPacketMap;
    }

}
