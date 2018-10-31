package com.nexenio.bleindoorpositioning.ble.advertising;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    public static int[] getRssisFromAdvertisingPackets(List<? extends AdvertisingPacket> advertisingPackets) {
        int[] rssis = new int[advertisingPackets.size()];
        for (int i = 0; i < advertisingPackets.size(); i++) {
            rssis[i] = advertisingPackets.get(i).getRssi();
        }
        return rssis;
    }

    public static float calculateMean(int[] values) {
        int sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum / (float) values.length;
    }

    public static float calculateVariance(int[] values) {
        float mean = calculateMean(values);
        float squaredDistanceSum = 0;
        for (int i = 0; i < values.length; i++) {
            squaredDistanceSum += Math.pow(values[i] - mean, 2);
        }
        int sampleLength = Math.max(values.length - 1, 1);
        return squaredDistanceSum / sampleLength;
    }

    public static float getPacketFrequency(int packetCount, long duration, TimeUnit timeUnit) {
        if (duration == 0) {
            return 0;
        }
        return packetCount / (float) (timeUnit.toSeconds(duration));
    }

    /**
     * Returns an ArrayList of AdvertisingPackets that have been received in the specified time
     * range. If no packets match, an empty list will be returned. Expects the specified
     * AdvertisingPackets to be sorted by timestamp in ascending order (i.e. the oldest timestamp
     * first)!
     *
     * @param advertisingPackets the packets to filter (sorted by timestamp ascending)
     * @param startTimestamp     minimum timestamp, inclusive
     * @param endTimestamp       maximum timestamp, exclusive
     */
    public static <P extends AdvertisingPacket> ArrayList<P> getAdvertisingPacketsBetween(final ArrayList<P> advertisingPackets, long startTimestamp, long endTimestamp) {
        // check if advertising packets are available
        if (advertisingPackets.isEmpty()) {
            return new ArrayList<>();
        }

        P oldestAdvertisingPacket = advertisingPackets.get(0);
        P latestAdvertisingPacket = advertisingPackets.get(advertisingPackets.size() - 1);

        // check if the timestamps are out of range
        if (endTimestamp <= oldestAdvertisingPacket.getTimestamp() || startTimestamp > latestAdvertisingPacket.getTimestamp()) {
            return new ArrayList<>();
        }

        P midstAdvertisingPacket = advertisingPackets.get(advertisingPackets.size() / 2);

        // find the index of the first advertising packet with a timestamp
        // larger than or equal to the specified startTimestamp
        int startIndex = 0;
        if (startTimestamp > oldestAdvertisingPacket.getTimestamp()) {
            // figure out if the start timestamp is before or after the midst advertising packet
            ListIterator<P> listIterator;
            if (startTimestamp < midstAdvertisingPacket.getTimestamp()) {
                // start timestamp is in the first half of advertising packets
                // start iterating from the beginning
                listIterator = advertisingPackets.listIterator();
                while (listIterator.hasNext()) {
                    if (listIterator.next().getTimestamp() >= startTimestamp) {
                        startIndex = listIterator.previousIndex();
                        break;
                    }
                }
            } else {
                // start timestamp is in the second half of advertising packets
                // start iterating from the end
                listIterator = advertisingPackets.listIterator(advertisingPackets.size());
                while (listIterator.hasPrevious()) {
                    if (listIterator.previous().getTimestamp() < startTimestamp) {
                        startIndex = listIterator.nextIndex() + 1;
                        break;
                    }
                }
            }
        }

        // find the index of the last advertising packet with a timestamp
        // smaller than the specified endTimestamp
        int endIndex = advertisingPackets.size();
        if (endTimestamp < latestAdvertisingPacket.getTimestamp()) {
            // figure out if the end timestamp is before or after the midst advertising packet
            ListIterator<P> listIterator;
            if (endTimestamp < midstAdvertisingPacket.getTimestamp()) {
                // end timestamp is in the first half of advertising packets
                // start iterating from the beginning
                listIterator = advertisingPackets.listIterator(startIndex);
                while (listIterator.hasNext()) {
                    if (listIterator.next().getTimestamp() >= endTimestamp) {
                        endIndex = listIterator.previousIndex();
                        break;
                    }
                }
            } else {
                // end timestamp is in the second half of advertising packets
                // start iterating from the end
                listIterator = advertisingPackets.listIterator(advertisingPackets.size());
                while (listIterator.hasPrevious()) {
                    if (listIterator.previous().getTimestamp() < endTimestamp) {
                        endIndex = listIterator.nextIndex() + 1;
                        break;
                    }
                }
            }
        }

        return new ArrayList<>(advertisingPackets.subList(startIndex, endIndex));
    }

}
