package com.nexenio.bleindoorpositioning.testutil.benchmark;

public class BeaconInfo {

    // TODO: documentation

    private String name;

    private String model;

    private String manufacturer;

    private int advertisingFrequency;

    private int transmissionPower;

    private byte[] manufacturerData;

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

    public byte[] getManufacturerData() {
        return manufacturerData;
    }

    public void setManufacturerData(byte[] manufacturerData) {
        this.manufacturerData = manufacturerData;
    }
}
