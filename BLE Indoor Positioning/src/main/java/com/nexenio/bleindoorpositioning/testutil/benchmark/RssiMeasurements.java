package com.nexenio.bleindoorpositioning.testutil.benchmark;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;

import java.util.Map;

import io.reactivex.annotations.Nullable;

public class RssiMeasurements {

    public static final String KEY_DEVICE_INFO = "deviceInfo";
    public static final String KEY_BEACON_INFO = "beaconInfo";
    public static final String KEY_START_TIMESTAMP = "startTimestamp";
    public static final String KEY_END_TIMESTAMP = "endTimestamp";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_RSSIS = "rssis";

    private DeviceInfo deviceInfo;

    private BeaconInfo beaconInfo;

    private long startTimestamp;

    private long endTimestamp;

    private String notes;

    private float distance;

    private int[] rssis;

    @Nullable
    private Map<Long, AdvertisingPacket> advertisingPacketMap;

    public RssiMeasurements() {
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public BeaconInfo getBeaconInfo() {
        return beaconInfo;
    }

    public void setBeaconInfo(BeaconInfo beaconInfo) {
        this.beaconInfo = beaconInfo;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int[] getRssis() {
        return rssis;
    }

    public void setRssis(int[] rssis) {
        this.rssis = rssis;
    }

    public Map<Long, AdvertisingPacket> getAdvertisingPacketMap() {
        return advertisingPacketMap;
    }

    public void setAdvertisingPacketMap(Map<Long, AdvertisingPacket> advertisingPacketMap) {
        this.advertisingPacketMap = advertisingPacketMap;
    }

}
