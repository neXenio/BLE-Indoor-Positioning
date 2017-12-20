package com.nexenio.bleindoorpositioning.ble.advertising;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by steppschuh on 06.12.17.
 */

public abstract class AdvertisingPacketUtil {

    public static String toHexadecimalString(byte[] bytes) {
        BigInteger bigInteger = new BigInteger(bytes);
        return "0x" + bigInteger.toString(16).toUpperCase();
    }

    public static UUID toUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }

    public static int[] getRssisFromAdvertisingPackets(List<AdvertisingPacket> advertisingPackets) {
        int[] rssis = new int[advertisingPackets.size()];
        for (int i = 0; i < advertisingPackets.size(); i++) {
            rssis[i] = advertisingPackets.get(i).getRssi();
        }
        return rssis;
    }

    public static float getMeanRssi(int[] rssis) {
        int rssiSum = 0;
        for (int i = 0; i < rssis.length; i++) {
            rssiSum += rssis[i];
        }
        return rssiSum / (float) rssis.length;
    }

    public static float getMedianRssi(int[] values) {
        // sort values
        Arrays.sort(values);

        // get centered value
        float medianValue;
        int middleIndex = values.length / 2;
        if (values.length % 2 == 1) {
            medianValue = values[middleIndex];
        } else {
            medianValue = (values[middleIndex - 1] + values[middleIndex]) / 2f;
        }
        return medianValue;
    }

}
