package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacketFactoryManager;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class IBeaconTest {

    // see https://github.com/neXenio/BLE-Indoor-Positioning/issues/86
    public final static byte[] KONTAKT_FRAME_DATA = new byte[]{2, 1, 6, 26, -1, 76, 0, 2, 21, -111, 20, -42, 26, 103, -47, 17, -24, -83, -64, -6, 122, -32, 27, -66, -68, 100, -16, -113, 90, -59, 8, 9, 75, 111, 110, 116, 97, 107, 116, 2, 10, 4, 10, 22, 13, -48, 119, 99, 74, 111, 52, 50, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public final static byte[] BLUEUP_FRAME_DATA = new byte[]{2, 1, 6, 26, -1, 76, 0, 2, 21, 3, 37, 63, -35, 85, -53, 68, -62, -95, -21, -128, -56, 53, 95, -126, -111, 0, 1, 0, 2, -54};

    private AdvertisingPacketFactoryManager advertisingPacketFactoryManager;
    private IBeaconAdvertisingPacket kontaktAdvertsingPacket;
    private IBeaconAdvertisingPacket blueupAdvertsingPacket;

    @Before
    public void setUp() {
        advertisingPacketFactoryManager = new AdvertisingPacketFactoryManager();
        kontaktAdvertsingPacket = (IBeaconAdvertisingPacket) advertisingPacketFactoryManager.createAdvertisingPacket(KONTAKT_FRAME_DATA);
        blueupAdvertsingPacket = (IBeaconAdvertisingPacket) advertisingPacketFactoryManager.createAdvertisingPacket(BLUEUP_FRAME_DATA);
    }

    @Test
    public void getProximityUuid() {
        assertEquals(UUID.fromString("9114d61a-67d1-11e8-adc0-fa7ae01bbebc"), kontaktAdvertsingPacket.getProximityUuid());
        assertEquals(UUID.fromString("03253fdd-55cb-44c2-a1eb-80c8355f8291"), blueupAdvertsingPacket.getProximityUuid());
    }

    @Test
    public void getMajor() {
        assertEquals(25840, kontaktAdvertsingPacket.getMajor());
        assertEquals(1, blueupAdvertsingPacket.getMajor());
    }

    @Test
    public void getMinor() {
        assertEquals(36698, kontaktAdvertsingPacket.getMinor());
        assertEquals(2, blueupAdvertsingPacket.getMinor());
    }

}