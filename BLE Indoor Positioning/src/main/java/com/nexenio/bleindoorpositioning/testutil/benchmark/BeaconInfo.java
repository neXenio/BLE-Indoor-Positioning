package com.nexenio.bleindoorpositioning.testutil.benchmark;

public class BeaconInfo {

    private String name;

    private String model;

    private String manufacturer;

    private int advertizingFrequency;

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

    public int getAdvertizingFrequency() {
        return advertizingFrequency;
    }

    public void setAdvertizingFrequency(int advertizingFrequency) {
        this.advertizingFrequency = advertizingFrequency;
    }

    public int getTransmissionPower() {
        return transmissionPower;
    }

    public void setTransmissionPower(int transmissionPower) {
        this.transmissionPower = transmissionPower;
    }

}
