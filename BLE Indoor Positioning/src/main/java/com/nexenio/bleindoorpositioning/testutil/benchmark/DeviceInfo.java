package com.nexenio.bleindoorpositioning.testutil.benchmark;

public class DeviceInfo {

    public static final String KEY_DEVICE_ID = "deviceName";
    public static final String KEY_DEVICE_MODEL = "deviceModel";
    public static final String KEY_DEVICE_MANUFACTURER = "deviceManufacturer";
    public static final String KEY_DEVICE_OS_VERSION = "deviceOsVersion";

    private String name;

    private String model;

    private String manufacturer;

    private String osVersion;

    public DeviceInfo() {
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

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

}
