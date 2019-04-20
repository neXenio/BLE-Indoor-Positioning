package com.nexenio.bleindoorpositioning.testutil.benchmark;

public class BeaconInfo {

    public static final String KEY_BEACON_NAME = "beaconName";
    public static final String KEY_BEACON_MODEL = "beaconModel";
    public static final String KEY_BEACON_MANUFACTURER = "beaconManufacturer";
    public static final String KEY_BEACON_ADVERTISING_FREQUENCY = "beaconAdvertisingFrequency";
    public static final String KEY_BEACON_TRANSMISSION_POWER = "beaconTransmissionPower";

    private String name;

    private String model;

    private String manufacturer;

    private int advertisingFrequency;

    private int transmissionPower;

    public BeaconInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getAdvertisingFrequency() {
        return advertisingFrequency;
    }

    public void setAdvertisingFrequency(int advertisingFrequency) {
        this.advertisingFrequency = advertisingFrequency;
    }

    public int getTransmissionPower() {
        return transmissionPower;
    }

    public void setTransmissionPower(int transmissionPower) {
        this.transmissionPower = transmissionPower;
    }

}
