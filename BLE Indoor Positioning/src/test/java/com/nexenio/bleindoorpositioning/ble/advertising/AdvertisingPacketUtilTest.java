package com.nexenio.bleindoorpositioning.ble.advertising;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconTest;

import net.steppschuh.markdowngenerator.MarkdownBuilder;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.table.TableRow;
import net.steppschuh.markdowngenerator.text.TextBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AdvertisingPacketUtilTest {

    private static final int MEASUREMENTS_COUNT = 100000;

    private ArrayList<IBeaconAdvertisingPacket> advertisingPackets;

    @Before
    public void setUp() {
        advertisingPackets = new ArrayList<>();
        IBeaconAdvertisingPacket advertisingPacket;

        int packetsFrequency = 10; // in Hertz
        long packetsCount = TimeUnit.MILLISECONDS.toSeconds(Beacon.MAXIMUM_PACKET_AGE) * packetsFrequency; // number of packets to add
        int timestampDelta = (int) TimeUnit.SECONDS.toMillis(1) / packetsFrequency;
        long latestPacketTimestamp = System.currentTimeMillis();
        long oldestPacketTimestamp = latestPacketTimestamp - ((packetsCount - 1) * timestampDelta);

        for (int packetIndex = 0; packetIndex < packetsCount; packetIndex++) {
            advertisingPacket = new IBeaconAdvertisingPacket(BeaconTest.IBEACON_ADVERTISING_DATA);
            advertisingPacket.setTimestamp(oldestPacketTimestamp + (packetIndex * timestampDelta));
            advertisingPackets.add(advertisingPacket);
        }
    }

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

    private static void getLatestAdvertisingPackets(ArrayList<IBeaconAdvertisingPacket> advertisingPackets, long duration, boolean useReferenceImplementation) {
        long endTimestamp = advertisingPackets.get(advertisingPackets.size() - 1).getTimestamp() + 1;
        long startTimestamp = endTimestamp - duration;
        if (useReferenceImplementation) {
            getAdvertisingPacketsBetweenReference(advertisingPackets, startTimestamp, endTimestamp);
        } else {
            getAdvertisingPacketsBetween(advertisingPackets, startTimestamp, endTimestamp);
        }
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

    /**
     * The new implementation that you want to test against the reference implementation.
     *
     * Currently a very unperformant implementation that iterates over all packets.
     */
    private static ArrayList<IBeaconAdvertisingPacket> getAdvertisingPacketsBetween(ArrayList<IBeaconAdvertisingPacket> advertisingPackets, long startTimestamp, long endTimestamp) {
        ArrayList<IBeaconAdvertisingPacket> matchingAdvertisingPackets = new ArrayList<>();
        for (IBeaconAdvertisingPacket advertisingPacket : new ArrayList<>(advertisingPackets)) {
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
    private static ArrayList<IBeaconAdvertisingPacket> getAdvertisingPacketsBetweenReference(ArrayList<IBeaconAdvertisingPacket> advertisingPackets, long startTimestamp, long endTimestamp) {
        return AdvertisingPacketUtil.getAdvertisingPacketsBetween(advertisingPackets, startTimestamp, endTimestamp);
    }

}