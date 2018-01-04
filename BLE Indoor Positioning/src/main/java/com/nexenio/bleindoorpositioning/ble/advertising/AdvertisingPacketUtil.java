package com.nexenio.bleindoorpositioning.ble.advertising;

import java.math.BigInteger;
import java.nio.ByteBuffer;
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

    public static float calculateMean(int[] values) {
        int rssiSum = 0;
        for (int i = 0; i < values.length; i++) {
            rssiSum += values[i];
        }
        return rssiSum / (float) values.length;
    }

    public static float calculateMean(float[] values) {
        int rssiSum = 0;
        for (int i = 0; i < values.length; i++) {
            rssiSum += values[i];
        }
        return rssiSum / (float) values.length;
    }

    public static float calculateVariance(float[] values) {
        float average = calculateMean(values);

        float squaredDistanceSum = 0;
        for (int i = 0; i < values.length; i++) {
            squaredDistanceSum += Math.pow(values[i] - average, 2);
        }
        int sampleLength = Math.max(values.length - 1, 1);
        return squaredDistanceSum / sampleLength;
    }

    public static float calculateStandardDeviation(float[] values) {
        float variance = calculateVariance(values);
        return (float) Math.sqrt(variance);
    }

}
