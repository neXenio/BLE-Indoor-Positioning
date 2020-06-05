package com.nexenio.bleindoorpositioningdemo.recording;

import com.google.gson.annotations.Expose;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;

import java.util.List;

public class IndoorPositioningRecording {

    @Expose
    private long startTimestamp;

    @Expose
    private long endTimestamp;

    @Expose
    private List<AdvertisingPacket> advertisingPacketList;

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

    public List<AdvertisingPacket> getAdvertisingPacketList() {
        return advertisingPacketList;
    }

    public void setAdvertisingPacketList(List<AdvertisingPacket> advertisingPacketMap) {
        this.advertisingPacketList = advertisingPacketMap;
    }

}
