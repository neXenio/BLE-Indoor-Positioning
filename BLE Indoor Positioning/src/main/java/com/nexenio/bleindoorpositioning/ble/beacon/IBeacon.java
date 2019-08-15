package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.location.provider.BeaconLocationProvider;
import com.nexenio.bleindoorpositioning.location.provider.IBeaconLocationProvider;

import java.util.UUID;

/**
 * Created by steppschuh on 15.11.17.
 */

public class IBeacon<P extends IBeaconAdvertisingPacket> extends Beacon<P> {

    public static final int CALIBRATION_DISTANCE_DEFAULT = 1;

    protected UUID proximityUuid;
    protected int major;
    protected int minor;

    public IBeacon() {
        this.calibratedDistance = CALIBRATION_DISTANCE_DEFAULT;
    }

    @Override
    public BeaconLocationProvider<IBeacon<P>> createLocationProvider() {
        return new IBeaconLocationProvider<IBeacon<P>>(this) {
            @Override
            protected void updateLocation() {
                // nope
            }

            @Override
            protected boolean canUpdateLocation() {
                return false;
            }
        };
    }

    @Override
    public void applyPropertiesFromAdvertisingPacket(P advertisingPacket) {
        super.applyPropertiesFromAdvertisingPacket(advertisingPacket);
        setProximityUuid(advertisingPacket.getProximityUuid());
        setMajor(advertisingPacket.getMajor());
        setMinor(advertisingPacket.getMinor());
        setCalibratedRssi(advertisingPacket.getMeasuredPowerByte());
    }

    @Override
    public String toString() {
        return "IBeacon{" +
                "proximityUuid=" + proximityUuid +
                ", major=" + major +
                ", minor=" + minor +
                ", macAddress='" + macAddress + '\'' +
                ", rssi=" + rssi +
                ", calibratedRssi=" + calibratedRssi +
                ", transmissionPower=" + transmissionPower +
                ", advertisingPackets=" + advertisingPackets +
                '}';
    }

    /*
        Getter & Setter
     */

    public UUID getProximityUuid() {
        return proximityUuid;
    }

    public void setProximityUuid(UUID proximityUuid) {
        this.proximityUuid = proximityUuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

}
