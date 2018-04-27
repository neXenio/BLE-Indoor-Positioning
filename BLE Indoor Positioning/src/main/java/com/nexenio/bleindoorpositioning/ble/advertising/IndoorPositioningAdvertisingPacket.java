package com.nexenio.bleindoorpositioning.ble.advertising;

import java.util.Arrays;

/**
 * For advertising packets as specified in Apples <a href="http://www.blueupbeacons.com/docs/dev/Proximity%20Beacon%20Specification%20R1.pdf">Proximity
 * Beacon Specification</a>.
 */

public class IndoorPositioningAdvertisingPacket extends IBeaconAdvertisingPacket {

    public IndoorPositioningAdvertisingPacket(byte[] data) {
        super(data);
    }

    public static boolean meetsSpecification(byte[] data) {
        // TODO: extract position
        if (data == null || data.length < 29) {
            return false;
        }
        if (getTypeBytes(data) != EXPECTED_TYPE) {
            return false;
        }
        if (!Arrays.equals(getFlagsBytes(data), EXPECTED_FLAGS)) {
            return false;
        }
        if (!Arrays.equals(getBeaconTypeBytes(data), EXPECTED_BEACON_TYPE)) {
            return false;
        }
        return true;
    }
}
