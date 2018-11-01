package com.nexenio.bleindoorpositioning.ble.advertising;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconTest;

import net.steppschuh.markdowngenerator.MarkdownBuilder;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.table.TableRow;
import net.steppschuh.markdowngenerator.text.TextBuilder;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdvertisingPacketUtilTest {

    private static final int MEASUREMENTS_COUNT = 100000;

    private ArrayList<AdvertisingPacket> advertisingPackets;

    @Test
    public void getAdvertisingPacketsBetween_startIndexIsCalculatedInFirstHalf_returnsCorrectList() {
        long latestTimestamp = System.currentTimeMillis();
        int numberOfPackets = 2;
        advertisingPackets = getAdvertisingPackets(1, numberOfPackets, latestTimestamp);

        AdvertisingPacket secondToLastPacket = advertisingPackets.get(numberOfPackets - 2);

        long startTimestamp = secondToLastPacket.timestamp;
        long endTimestamp = latestTimestamp - 1;

        List<AdvertisingPacket> packagesBetween = AdvertisingPacketUtil.getAdvertisingPacketsBetween(advertisingPackets, startTimestamp, endTimestamp);

        for (AdvertisingPacket advertisingPacket : packagesBetween) {
            assertTrue("Timestamp is smaller than our start timestamp", advertisingPacket.timestamp >= startTimestamp);
            assertTrue("Timestamp is greater than end timestamp", advertisingPacket.timestamp <= endTimestamp);
        }

        assertEquals("Incorrect number of packages returned", 1, packagesBetween.size());
    }

    @Test
    public void getAdvertisingPacketsBetween_startIndexIsCalculatedInSecondHalf_returnsCorrectList() {
        long latestTimestamp = System.currentTimeMillis();
        int numberOfPackets = 3;
        advertisingPackets = getAdvertisingPackets(1, numberOfPackets, latestTimestamp);

        AdvertisingPacket secondToLastPacket = advertisingPackets.get(numberOfPackets - 2);

        long startTimestamp = secondToLastPacket.timestamp;
        long endTimestamp = latestTimestamp - 1;

        List<AdvertisingPacket> packagesBetween = AdvertisingPacketUtil.getAdvertisingPacketsBetween(advertisingPackets, startTimestamp, endTimestamp);

        for (AdvertisingPacket advertisingPacket : packagesBetween) {
            assertTrue("Timestamp is smaller than our start timestamp", advertisingPacket.timestamp >= startTimestamp);
            assertTrue("Timestamp is greater than end timestamp", advertisingPacket.timestamp <= endTimestamp);
        }

        assertEquals("Incorrect number of packages returned", 1, packagesBetween.size());
    }

    /**
     * @see <a href="https://github.com/neXenio/BLE-Indoor-Positioning/issues/120">Bug resulting
     *         in incorrect list</a>
     */
    @Test
    public void getAdvertisingPacketsBetween_endIndexIsCalculatedInFirstHalf_returnsCorrectList() {
        long latestTimestamp = System.currentTimeMillis();
        int numberOfPackets = 2;
        advertisingPackets = getAdvertisingPackets(1, numberOfPackets, latestTimestamp);

        AdvertisingPacket secondToLastPacket = advertisingPackets.get(numberOfPackets - 2);

        long startTimestamp = secondToLastPacket.timestamp;
        long endTimestamp = latestTimestamp - 1;

        List<AdvertisingPacket> packagesBetween = AdvertisingPacketUtil.getAdvertisingPacketsBetween(advertisingPackets, startTimestamp, endTimestamp);

        for (AdvertisingPacket advertisingPacket : packagesBetween) {
            assertTrue("Timestamp is smaller than our start timestamp", advertisingPacket.timestamp >= startTimestamp);
            assertTrue("Timestamp is greater than end timestamp", advertisingPacket.timestamp <= endTimestamp);
        }

        assertEquals("Incorrect number of packages returned", 1, packagesBetween.size());
    }

    @Test
    public void getAdvertisingPacketsBetween_endIndexIsCalculatedInSecondHalf_returnsCorrectList() {
        long latestTimestamp = System.currentTimeMillis();
        int numberOfPackets = 3;
        advertisingPackets = getAdvertisingPackets(1, numberOfPackets, latestTimestamp);

        AdvertisingPacket secondToLastPacket = advertisingPackets.get(numberOfPackets - 2);

        long startTimestamp = secondToLastPacket.timestamp;
        long endTimestamp = latestTimestamp - 1;

        List<AdvertisingPacket> packagesBetween = AdvertisingPacketUtil.getAdvertisingPacketsBetween(advertisingPackets, startTimestamp, endTimestamp);

        for (AdvertisingPacket advertisingPacket : packagesBetween) {
            assertTrue("Timestamp is smaller than our start timestamp", advertisingPacket.timestamp >= startTimestamp);
            assertTrue("Timestamp is greater than end timestamp", advertisingPacket.timestamp <= endTimestamp);
        }

        assertEquals("Incorrect number of packages returned", 1, packagesBetween.size());
    }

    private List<AdvertisingPacket> getAdvertisingPackets() {
        return getAdvertisingPackets(10, 1000, System.currentTimeMillis());
    }

    /**
     * @param packetsFrequency Frequency in which packets timestamp should be created (in Hertz)
     * @param packetsCount     Number of packets to return
     * @return List of advertising packets
     */
    private ArrayList<AdvertisingPacket> getAdvertisingPackets(int packetsFrequency, int packetsCount, long latestPacketTimestamp) {
        ArrayList<AdvertisingPacket> advertisingPackets = new ArrayList<>();

        int timestampDelta = (int) TimeUnit.SECONDS.toMillis(1) / packetsFrequency;
        long oldestPacketTimestamp = latestPacketTimestamp - ((packetsCount - 1) * timestampDelta);

        for (int packetIndex = 0; packetIndex < packetsCount; packetIndex++) {
            IBeaconAdvertisingPacket advertisingPacket = new IBeaconAdvertisingPacket(BeaconTest.IBEACON_ADVERTISING_DATA);
            advertisingPacket.setTimestamp(oldestPacketTimestamp + (packetIndex * timestampDelta));
            advertisingPackets.add(advertisingPacket);
        }

        return advertisingPackets;
    }

    // Benchmarking

    @Ignore
    @Test
    public void getAdvertisingPacketsBenchmark() {
        MarkdownBuilder markdownBuilder = new TextBuilder()
                .heading("Advertising Packet Benchmarks")
                .append("Working with a list of ")
                .append(advertisingPackets.size()).append(" packets.")
                .newLine()
                .append("Each metric has been measured ")
                .append(MEASUREMENTS_COUNT).append(" times.");

        System.out.println(markdownBuilder);

        int packetsFrequency = 10;
        int packetCount = (int) TimeUnit.MILLISECONDS.toSeconds(Beacon.MAXIMUM_PACKET_AGE) * packetsFrequency;
        advertisingPackets = getAdvertisingPackets(packetsFrequency, packetCount, System.currentTimeMillis());

        getAdvertisingPacketsBetweenBenchmark();
        getLatestAdvertisingPacketsBenchmark();
    }

    private void getAdvertisingPacketsBetweenBenchmark() {
        MarkdownBuilder markdownBuilder = new TextBuilder()
                .subHeading("Advertising packets in range")
                .append("Requesting packets between random timestamps which are in range of the available packets.")
                .newParagraph();

        long[] startTimestamps = new long[MEASUREMENTS_COUNT];
        long[] endTimestamps = new long[MEASUREMENTS_COUNT];
        long minimumTimestamp = advertisingPackets.get(0).getTimestamp();
        long maximumTimestamp = advertisingPackets.get(advertisingPackets.size() - 1).getTimestamp();
        long timestampRange = maximumTimestamp - minimumTimestamp;
        long startTimestamp;
        long endTimestamp;

        // create random time ranges to request later
        for (int measurementIndex = 0; measurementIndex < MEASUREMENTS_COUNT; measurementIndex++) {
            startTimestamp = (minimumTimestamp - timestampRange) + (long) (Math.random() * 2 * timestampRange);
            endTimestamp = startTimestamp + timestampRange + (long) (Math.random() * (maximumTimestamp - startTimestamp));

            startTimestamps[measurementIndex] = startTimestamp;
            endTimestamps[measurementIndex] = endTimestamp;
        }

        // measure new implementation
        long start = System.nanoTime();
        for (int measurementIndex = 0; measurementIndex < MEASUREMENTS_COUNT; measurementIndex++) {
            getAdvertisingPacketsBetween(advertisingPackets, startTimestamps[measurementIndex], endTimestamps[measurementIndex]);
        }
        long averageDuration = (System.nanoTime() - start) / MEASUREMENTS_COUNT;

        // measure reference implementation
        start = System.nanoTime();
        for (int measurementIndex = 0; measurementIndex < MEASUREMENTS_COUNT; measurementIndex++) {
            getAdvertisingPacketsBetweenReference(advertisingPackets, startTimestamps[measurementIndex], endTimestamps[measurementIndex]);
        }
        long averageReferenceDuration = (System.nanoTime() - start) / MEASUREMENTS_COUNT;

        markdownBuilder.append(createBenchmarkTable(averageDuration, averageReferenceDuration));

        System.out.println(markdownBuilder);
    }

    private void getLatestAdvertisingPacketsBenchmark() {
        MarkdownBuilder markdownBuilder = new TextBuilder()
                .subHeading("Recent advertising packets")
                .append("Requesting recent packets using different time ranges.")
                .newParagraph();

        long[] durations = new long[]{
                TimeUnit.SECONDS.toMillis(1),
                TimeUnit.SECONDS.toMillis(2),
                TimeUnit.SECONDS.toMillis(3),
                TimeUnit.SECONDS.toMillis(5),
                TimeUnit.SECONDS.toMillis(10),
                TimeUnit.SECONDS.toMillis(30),
                TimeUnit.MINUTES.toMillis(1),
                TimeUnit.MINUTES.toMillis(2),
                TimeUnit.MINUTES.toMillis(3)
        };

        Table.Builder tableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_RIGHT)
                .addRow("Time Range", "⌀ Duration", "% of reference");

        long averageDurationSum = 0;
        long averageReferenceDurationSum = 0;

        for (long duration : durations) {
            // measure new implementation
            long start = System.nanoTime();
            for (int measurementIndex = 0; measurementIndex < MEASUREMENTS_COUNT; measurementIndex++) {
                getLatestAdvertisingPackets(advertisingPackets, duration, false);
            }
            long averageDuration = (System.nanoTime() - start) / MEASUREMENTS_COUNT;
            averageDurationSum += averageDuration;

            // measure reference implementation
            start = System.nanoTime();
            for (int measurementIndex = 0; measurementIndex < MEASUREMENTS_COUNT; measurementIndex++) {
                getLatestAdvertisingPackets(advertisingPackets, duration, true);
            }
            long averageReferenceDuration = (System.nanoTime() - start) / MEASUREMENTS_COUNT;
            averageReferenceDurationSum += averageReferenceDuration;

            tableBuilder.addRow(new TableRow<>(Arrays.asList(
                    TimeUnit.MILLISECONDS.toSeconds(duration) + "s",
                    getReadableDuration(averageDuration),
                    getReadableDelta(averageDuration, averageReferenceDuration)
            )));
        }

        markdownBuilder.append(tableBuilder.build()).newParagraph();

        long averageDuration = averageDurationSum / durations.length;
        long averageReferenceDuration = averageReferenceDurationSum / durations.length;
        markdownBuilder.append(createBenchmarkTable(averageDuration, averageReferenceDuration));

        System.out.println(markdownBuilder);
    }

    private static String getReadableDuration(long nanoseconds) {
        float milliseconds = (float) nanoseconds / 1000000;
        return String.format(Locale.US, "%.5f", milliseconds) + "ms";
    }

    private static String getReadableDelta(long nanoseconds, long referenceNanoseconds) {
        float improvementFactor = ((float) nanoseconds / referenceNanoseconds);
        return String.format(Locale.US, "%.2f", improvementFactor * 100) + "%";
    }

    private static Table createBenchmarkTable(long averageDuration, long averageReferenceDuration) {
        return new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_RIGHT)
                .addRow("Implementation", "⌀ Duration", "% of reference")
                .addRow("New", getReadableDuration(averageDuration), getReadableDelta(averageDuration, averageReferenceDuration))
                .addRow("Reference", getReadableDuration(averageReferenceDuration), getReadableDelta(averageReferenceDuration, averageReferenceDuration))
                .build();
    }

    private static void getLatestAdvertisingPackets(ArrayList<AdvertisingPacket> advertisingPackets, long duration, boolean useReferenceImplementation) {
        long endTimestamp = advertisingPackets.get(advertisingPackets.size() - 1).getTimestamp() + 1;
        long startTimestamp = endTimestamp - duration;
        if (useReferenceImplementation) {
            getAdvertisingPacketsBetweenReference(advertisingPackets, startTimestamp, endTimestamp);
        } else {
            getAdvertisingPacketsBetween(advertisingPackets, startTimestamp, endTimestamp);
        }
    }

    /**
     * The new implementation that you want to test against the reference implementation.
     * <p>
     * Currently a very unperformant implementation that iterates over all packets.
     */
    private static ArrayList<AdvertisingPacket> getAdvertisingPacketsBetween(ArrayList<AdvertisingPacket> advertisingPackets, long startTimestamp, long endTimestamp) {
        ArrayList<AdvertisingPacket> matchingAdvertisingPackets = new ArrayList<>();
        for (AdvertisingPacket advertisingPacket : new ArrayList<>(advertisingPackets)) {
            if (advertisingPacket.getTimestamp() < startTimestamp) {
                continue;
            }
            if (advertisingPacket.getTimestamp() >= endTimestamp) {
                continue;
            }
            matchingAdvertisingPackets.add(advertisingPacket);
        }
        return matchingAdvertisingPackets;
    }

    /**
     * The reference implementation you are challenging.
     */
    private static ArrayList<AdvertisingPacket> getAdvertisingPacketsBetweenReference(ArrayList<AdvertisingPacket> advertisingPackets, long startTimestamp, long endTimestamp) {
        return AdvertisingPacketUtil.getAdvertisingPacketsBetween(advertisingPackets, startTimestamp, endTimestamp);
    }

}