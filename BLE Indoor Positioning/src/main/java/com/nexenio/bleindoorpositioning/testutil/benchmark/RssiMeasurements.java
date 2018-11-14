package com.nexenio.bleindoorpositioning.testutil.benchmark;

public class RssiMeasurements {

    // TODO: rename to RssiMeasurement ?
    // TODO: documentation

    private DeviceInfo deviceInfo;

    private BeaconInfo beaconInfo;

    private long timestamp;

    private String notes;

    private float distance;

    private int[] rssis;

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

}
