package com.nexenio.bleindoorpositioning.ble.advertising;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.Eddystone;

import java.util.Arrays;

/**
 * For advertising packets as specified in Googles <a href="https://github.com/google/eddystone/blob/master/protocol-specification.md">Protocol
 * Specification</a>.
 */

public class EddystoneAdvertisingPacket extends AdvertisingPacket {

    private static final byte[] EXPECTED_FLAGS = {0x02, 0x01, 0x06};
    private static final byte[] EXPECTED_EDDYSTONE_UUID = {0x03, 0x03, (byte) 0xAA, (byte) 0xFE};

    private byte[] flagsBytes;
    private byte[] eddystoneUuidBytes;
    private byte[] frameBytes;


    public EddystoneAdvertisingPacket(byte[] data) {
        super(data);
    }

    private void parseData() {
        flagsBytes = getFlags(data);
        eddystoneUuidBytes = getEddystoneUuid(data);
        frameBytes = getFrameBytes(data);
    }

    @Override
    public Class<? extends Beacon> getBeaconClass() {
        return Eddystone.class;
    }

    public static boolean meetsSpecification(byte[] data) {
        if (data == null || data.length < 15) {
            return false;
        }
        if (!Arrays.equals(getFlags(data), EXPECTED_FLAGS)) {
            return false;
        }
        if (!Arrays.equals(getEddystoneUuid(data), EXPECTED_EDDYSTONE_UUID)) {
            return false;
        }
        return true;
    }

    public static byte[] getFlags(byte[] data) {
        return Arrays.copyOfRange(data, 0, 3);
    }

    public static byte[] getEddystoneUuid(byte[] data) {
        return Arrays.copyOfRange(data, 3, 3 + 4);
    }

    public static byte[] getFrameBytes(byte[] data) {
        return Arrays.copyOfRange(data, 7, data.length);
    }

    /*
        Getter & Setter
     */

    public byte[] getFlagsBytes() {
        if (flagsBytes == null) {
            flagsBytes = getFlags(data);
        }
        return flagsBytes;
    }

    public void setFlagsBytes(byte[] flagsBytes) {
        this.flagsBytes = flagsBytes;
    }

    public byte[] getEddystoneUuidBytes() {
        if (eddystoneUuidBytes == null) {
            eddystoneUuidBytes = getEddystoneUuid(data);
        }
        return eddystoneUuidBytes;
    }

    public void setEddystoneUuidBytes(byte[] eddystoneUuidBytes) {
        this.eddystoneUuidBytes = eddystoneUuidBytes;
    }

    public byte[] getFrameBytes() {
        if (frameBytes == null) {
            frameBytes = getFrameBytes(data);
        }
        return frameBytes;
    }

    public void setFrameBytes(byte[] frameBytes) {
        this.frameBytes = frameBytes;
    }
}
