package com.nexenio.bleindoorpositioning.ble.advertising;

import java.util.Arrays;

/**
 * For advertising packets as specified in Apples <a href="http://www.blueupbeacons.com/docs/dev/Proximity%20Beacon%20Specification%20R1.pdf">Proximity
 * Beacon Specification</a>.
 */

public class IBeaconAdvertisingPacket extends AdvertisingPacket {

    private static final byte[] EXPECTED_FLAGS = {0x02, 0x01, 0x06};
    private static final byte EXPECTED_LENGTH = 0x1A;
    private static final byte EXPECTED_TYPE = (byte) 0xFF;
    private static final byte[] EXPECTED_COMPANY_ID = {0x4C, 0x00};
    private static final byte[] EXPECTED_BEACON_TYPE = {0x02, 0x15};

    private byte[] flagsBytes;
    private byte lengthByte;
    private byte typeByte;
    private byte[] companyIdBytes;
    private byte[] beaconTypeBytes;
    private byte[] proximityUuidBytes;
    private byte[] majorBytes;
    private byte[] minorBytes;
    private byte measuredPowerByte;

    public IBeaconAdvertisingPacket(byte[] data) {
        super(data);
    }

    private void parseData() {
        flagsBytes = getFlags(data);
        lengthByte = getLength(data);
        typeByte = getType(data);
        companyIdBytes = getCompanyId(data);
        beaconTypeBytes = getBeaconType(data);
        proximityUuidBytes = getProximityUuid(data);
        majorBytes = getMajor(data);
        minorBytes = getMinor(data);
        measuredPowerByte = getMeasuredPower(data);
    }

    @Override
    public String toString() {
        return new StringBuilder("iBeacon Advertising Packet (")
                .append("Proximity UUID: ").append(AdvertisingPacketUtil.toUuid(getProximityUuidBytes())).append(" ")
                .append("Major: ").append(AdvertisingPacketUtil.toHexadecimalString(getMajorBytes())).append(" ")
                .append("Minor: ").append(AdvertisingPacketUtil.toHexadecimalString(getMinorBytes())).append(" ")
                .append("RSSI at 1m: ").append(getMeasuredPowerByte())
                .append(")")
                .toString();
    }

    public static boolean meetsSpecification(byte[] data) {
        if (data == null || data.length < 29) {
            return false;
        }
        if (getType(data) != EXPECTED_TYPE) {
            return false;
        }
        if (!Arrays.equals(getFlags(data), EXPECTED_FLAGS)) {
            return false;
        }
        if (!Arrays.equals(getBeaconType(data), EXPECTED_BEACON_TYPE)) {
            return false;
        }
        return true;
    }

    /*
        Getter & Setter
     */

    public static byte[] getFlags(byte[] data) {
        return Arrays.copyOfRange(data, 0, 3);
    }

    public static byte getLength(byte[] data) {
        return data[3];
    }

    public static byte getType(byte[] data) {
        return data[4];
    }

    public static byte[] getCompanyId(byte[] data) {
        return Arrays.copyOfRange(data, 5, 5 + 2);
    }

    public static byte[] getBeaconType(byte[] data) {
        return Arrays.copyOfRange(data, 7, 7 + 2);
    }

    public static byte[] getProximityUuid(byte[] data) {
        return Arrays.copyOfRange(data, 9, 9 + 24);
    }

    public static byte[] getMajor(byte[] data) {
        return Arrays.copyOfRange(data, 25, 25 + 2);
    }

    public static byte[] getMinor(byte[] data) {
        return Arrays.copyOfRange(data, 27, 27 + 2);
    }

    public static byte getMeasuredPower(byte[] data) {
        return data[29];
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

    public byte getLengthByte() {
        if (lengthByte == 0) {
            lengthByte = getLength(data);
        }
        return lengthByte;
    }

    public void setLengthByte(byte lengthByte) {
        this.lengthByte = lengthByte;
    }

    public byte getTypeByte() {
        if (typeByte == 0) {
            typeByte = getType(data);
        }
        return typeByte;
    }

    public void setTypeByte(byte typeByte) {
        this.typeByte = typeByte;
    }

    public byte[] getCompanyIdBytes() {
        if (companyIdBytes == null) {
            companyIdBytes = getCompanyId(data);
        }
        return companyIdBytes;
    }

    public void setCompanyIdBytes(byte[] companyIdBytes) {
        this.companyIdBytes = companyIdBytes;
    }

    public byte[] getBeaconTypeBytes() {
        if (beaconTypeBytes == null) {
            beaconTypeBytes = getBeaconType(data);
        }
        return beaconTypeBytes;
    }

    public void setBeaconTypeBytes(byte[] beaconTypeBytes) {
        this.beaconTypeBytes = beaconTypeBytes;
    }

    public byte[] getProximityUuidBytes() {
        if (proximityUuidBytes == null) {
            proximityUuidBytes = getProximityUuid(data);
        }
        return proximityUuidBytes;
    }

    public void setProximityUuidBytes(byte[] proximityUuidBytes) {
        this.proximityUuidBytes = proximityUuidBytes;
    }

    public byte[] getMajorBytes() {
        if (majorBytes == null) {
            majorBytes = getMajor(data);
        }
        return majorBytes;
    }

    public void setMajorBytes(byte[] majorBytes) {
        this.majorBytes = majorBytes;
    }

    public byte[] getMinorBytes() {
        if (minorBytes == null) {
            minorBytes = getMinor(data);
        }
        return minorBytes;
    }

    public void setMinorBytes(byte[] minorBytes) {
        this.minorBytes = minorBytes;
    }

    public byte getMeasuredPowerByte() {
        if (measuredPowerByte == 0) {
            measuredPowerByte = getMeasuredPower(data);
        }
        return measuredPowerByte;
    }

    public void setMeasuredPowerByte(byte measuredPowerByte) {
        this.measuredPowerByte = measuredPowerByte;
    }
}
