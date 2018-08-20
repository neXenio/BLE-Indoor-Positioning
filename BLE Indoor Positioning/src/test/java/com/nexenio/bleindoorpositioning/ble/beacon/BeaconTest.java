package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BeaconTest {

    public static final byte[] IBEACON_ADVERTISING_DATA = new byte[]{2, 1, 6, 26, -1, 76, 0, 2, 21, -84, -3, 6, 94, -61, -64, 17, -29, -101, -66, 26, 81, 73, 50, -84, 1, 0, 1, 0, 2, -54};

    private IBeacon<IBeaconAdvertisingPacket> iBeacon;

    @Before
    public void setUp() {
        iBeacon = new IBeacon<>();
        IBeaconAdvertisingPacket advertisingPacket;

        int packetsFrequency = 10; // in Hertz
        int packetsCount = 1000; // number of packets to add
        int timestampDelta = (int) TimeUnit.SECONDS.toMillis(1) / packetsFrequency;
        long latestPacketTimestamp = System.currentTimeMillis();
        long oldestPacketTimestamp = latestPacketTimestamp - ((packetsCount - 1) * timestampDelta);

        for (int packetIndex = 0; packetIndex < packetsCount; packetIndex++) {
            advertisingPacket = new IBeaconAdvertisingPacket(IBEACON_ADVERTISING_DATA);
            advertisingPacket.setTimestamp(oldestPacketTimestamp + (packetIndex * timestampDelta));
            iBeacon.addAdvertisingPacket(advertisingPacket);
        }
    }

    @Test
    public void getOldestAdvertisingPacket() {
        assertEquals(iBeacon.getAdvertisingPackets().get(0), iBeacon.getOldestAdvertisingPacket());
    }

    @Test
    public void getLatestAdvertisingPacket() {
        assertEquals(iBeacon.getAdvertisingPackets().get(iBeacon.getAdvertisingPackets().size() - 1), iBeacon.getLatestAdvertisingPacket());
    }

    @Test
    public void getAdvertisingPacketsBetween_validRange_correctPackets() {
        // start and end timestamps are set to exclude the oldest and the latest packet
        long startTimestamp = iBeacon.getOldestAdvertisingPacket().getTimestamp() + 1;
        long endTimestamp = iBeacon.getLatestAdvertisingPacket().getTimestamp() - 1;
        List<IBeaconAdvertisingPacket> advertisingPackets = iBeacon.getAdvertisingPacketsBetween(startTimestamp, endTimestamp);
        assertFalse(advertisingPackets.contains(iBeacon.getOldestAdvertisingPacket()));
        assertFalse(advertisingPackets.contains(iBeacon.getLatestAdvertisingPacket()));
        assertEquals(iBeacon.getAdvertisingPackets().size() - 2, advertisingPackets.size());
    }

    @Test
    public void getAdvertisingPacketsBetween_exceededRange_allPackets() {
        // start and end timestamps are covering the whole range of packets
        long startTimestamp = iBeacon.getOldestAdvertisingPacket().getTimestamp();
        long endTimestamp = iBeacon.getLatestAdvertisingPacket().getTimestamp() + 1;
        List<IBeaconAdvertisingPacket> advertisingPackets = iBeacon.getAdvertisingPacketsBetween(startTimestamp, endTimestamp);
        assertEquals(iBeacon.getAdvertisingPackets().size(), advertisingPackets.size());
    }

    @Test
    public void getAdvertisingPacketsBetween_invalidRange_noPackets() {
        // start and end timestamps are mixed-up
        long endTimestamp = iBeacon.getOldestAdvertisingPacket().getTimestamp();
        long startTimestamp = iBeacon.getLatestAdvertisingPacket().getTimestamp() + 1;
        List<IBeaconAdvertisingPacket> advertisingPackets = iBeacon.getAdvertisingPacketsBetween(startTimestamp, endTimestamp);
        assertTrue(advertisingPackets.isEmpty());

        // start timestamp is after latest packet timestamp
        startTimestamp = iBeacon.getLatestTimestamp() + 1;
        endTimestamp = Long.MAX_VALUE;
        advertisingPackets = iBeacon.getAdvertisingPacketsBetween(startTimestamp, endTimestamp);
        assertTrue(advertisingPackets.isEmpty());

        // end timestamp is before oldest packet timestamp
        startTimestamp = 0;
        endTimestamp = iBeacon.getOldestAdvertisingPacket().getTimestamp();
        advertisingPackets = iBeacon.getAdvertisingPacketsBetween(startTimestamp, endTimestamp);
        assertTrue(advertisingPackets.isEmpty());
    }

    @Test
    public void getAdvertisingPacketsFromLast() {
        long duration = TimeUnit.SECONDS.toMillis(3);
        long minimumTimestamp = System.currentTimeMillis() - duration;
        List<IBeaconAdvertisingPacket> advertisingPackets = iBeacon.getAdvertisingPacketsFromLast(duration, TimeUnit.MILLISECONDS);
        long maximumTimestamp = System.currentTimeMillis();
        assertFalse(advertisingPackets.isEmpty());
        for (IBeaconAdvertisingPacket advertisingPacket : advertisingPackets) {
            if (advertisingPacket.getTimestamp() < minimumTimestamp) {
                fail("Packet timestamp before minimum timestamp");
            } else if (advertisingPacket.getTimestamp() >= maximumTimestamp) {
                fail("Packet timestamp after maximum timestamp");
            }
        }
    }

    @Test
    public void getAdvertisingPacketsSince() {
        long minimumTimestamp = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(3);
        List<IBeaconAdvertisingPacket> advertisingPackets = iBeacon.getAdvertisingPacketsSince(minimumTimestamp);
        assertFalse(advertisingPackets.isEmpty());
        for (IBeaconAdvertisingPacket advertisingPacket : advertisingPackets) {
            if (advertisingPacket.getTimestamp() < minimumTimestamp) {
                fail("Packet timestamp before minimum timestamp");
            }
        }
    }

    @Test
    public void getAdvertisingPacketsBefore_packetsWithSmallerTimestamp_returnsCorrectPackets() {
        long maximumTimestamp = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(3);
        List<IBeaconAdvertisingPacket> advertisingPackets = iBeacon.getAdvertisingPacketsBefore(maximumTimestamp);
        assertFalse(advertisingPackets.isEmpty());
        for (IBeaconAdvertisingPacket advertisingPacket : advertisingPackets) {
            if (advertisingPacket.getTimestamp() >= maximumTimestamp) {
                fail("Packet timestamp after maximum timestamp");
            }
        }
    }
}