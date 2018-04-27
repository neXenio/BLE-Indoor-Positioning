package com.nexenio.bleindoorpositioning.ble.advertising;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdvertisingPacketFactoryTest {

    public static final byte[] IBEACON_ADVERTISING_DATA = new byte[]{2, 1, 6, 26, -1, 76, 0, 2, 21, -84, -3, 6, 94, -61, -64, 17, -29, -101, -66, 26, 81, 73, 50, -84, 1, 0, 1, 0, 2, -54};

    @Test
    public void addAdvertisingPacketFactory_validFactory_addsCorrectly() {
        EddystoneAdvertisingPacketFactory advertisingPacketFactory = new EddystoneAdvertisingPacketFactory();
        AdvertisingPacketFactory testFactory = new TestAdvertisingPacketFactory();
        advertisingPacketFactory.addAdvertisingPacketFactory(testFactory);

        byte[] testData = new byte[5];
        assertFalse(advertisingPacketFactory.canCreateAdvertisingPacket(testData));
        assertTrue(advertisingPacketFactory.canCreateAdvertisingPacketWithSubFactories(testData));
    }

    @Test
    public void addAdvertisingPacketFactory_incorrectType_addsNothing() {
        IBeaconAdvertisingPacketFactory advertisingPacketFactory = new IBeaconAdvertisingPacketFactory();
        AdvertisingPacketFactory testFactory = new TestAdvertisingPacketFactory();
        advertisingPacketFactory.addAdvertisingPacketFactory(testFactory);

        assertEquals(0, advertisingPacketFactory.getSubFactoryMap().size());
        assertEquals(null, advertisingPacketFactory.getAdvertisingPacketFactory(TestAdvertisingPacket.class));
    }

    @Test
    public void removeAdvertisingPacketFactory_existingFactory_removesCorrectly() {
        EddystoneAdvertisingPacketFactory advertisingPacketFactory = new EddystoneAdvertisingPacketFactory();
        AdvertisingPacketFactory testFactory = new TestAdvertisingPacketFactory();
        advertisingPacketFactory.addAdvertisingPacketFactory(testFactory);

        advertisingPacketFactory.removeAdvertisingPacketFactory(testFactory);
        assertEquals(0, advertisingPacketFactory.getSubFactoryMap().size());
        assertEquals(null, advertisingPacketFactory.getAdvertisingPacketFactory(TestAdvertisingPacket.class));
    }

    @Test
    public void removeAdvertisingPacketFactory_noExistingFactory_removesCorrectly() {
        EddystoneAdvertisingPacketFactory advertisingPacketFactory = new EddystoneAdvertisingPacketFactory();
        AdvertisingPacketFactory testFactory = new TestAdvertisingPacketFactory();

        advertisingPacketFactory.removeAdvertisingPacketFactory(testFactory);
        assertEquals(0, advertisingPacketFactory.getSubFactoryMap().size());
        assertEquals(null, advertisingPacketFactory.getAdvertisingPacketFactory(TestAdvertisingPacket.class));
    }

    @Test
    public void canCreateAdvertisingPacket_validData_returnsTrue() {
        IBeaconAdvertisingPacketFactory advertisingPacketFactory = new IBeaconAdvertisingPacketFactory();
        assertTrue(advertisingPacketFactory.canCreateAdvertisingPacketWithSubFactories(IBEACON_ADVERTISING_DATA));
    }

    @Test
    public void canCreateAdvertisingPacket_invalidData_returnsFalse() {
        IBeaconAdvertisingPacketFactory advertisingPacketFactory = new IBeaconAdvertisingPacketFactory();
        assertFalse(advertisingPacketFactory.canCreateAdvertisingPacket(new byte[5]));
    }

    @Test
    public void canCreateAdvertisingPacketWithSubFactories_validData_returnsTrue() {
        EddystoneAdvertisingPacketFactory advertisingPacketFactory = new EddystoneAdvertisingPacketFactory();
        AdvertisingPacketFactory testFactory = new TestAdvertisingPacketFactory();
        advertisingPacketFactory.addAdvertisingPacketFactory(testFactory);

        byte[] testData = new byte[5];
        assertFalse(advertisingPacketFactory.canCreateAdvertisingPacket(testData));
        assertTrue(advertisingPacketFactory.canCreateAdvertisingPacketWithSubFactories(testData));
    }

    @Test
    public void canCreateAdvertisingPacketWithSubFactories_invalidData_returnsFalse() {
        IBeaconAdvertisingPacketFactory advertisingPacketFactory = new IBeaconAdvertisingPacketFactory();
        AdvertisingPacketFactory testFactory = new IndoorPositioningAdvertisingPacketFactory();
        advertisingPacketFactory.addAdvertisingPacketFactory(testFactory);

        byte[] testData = new byte[5];
        assertFalse(advertisingPacketFactory.canCreateAdvertisingPacket(testData));
        assertFalse(advertisingPacketFactory.canCreateAdvertisingPacketWithSubFactories(testData));
    }

    @Test
    public void createAdvertisingPacket_hasSubFactories_createsCorrectPacket() {
        IBeaconAdvertisingPacketFactory advertisingPacketFactory = new IBeaconAdvertisingPacketFactory();
        IndoorPositioningAdvertisingPacketFactory testFactory = new IndoorPositioningAdvertisingPacketFactory();
        advertisingPacketFactory.addAdvertisingPacketFactory(testFactory);

        AdvertisingPacket advertisingPacket = advertisingPacketFactory.createAdvertisingPacket(IBEACON_ADVERTISING_DATA);
        assertTrue(advertisingPacket instanceof IBeaconAdvertisingPacket);
        assertFalse(advertisingPacket instanceof IndoorPositioningAdvertisingPacket);
    }

    @Test
    public void createAdvertisingPacketWithSubFactories_hasNoSubFactories_createsCorrectPacket() {
        IBeaconAdvertisingPacketFactory advertisingPacketFactory = new IBeaconAdvertisingPacketFactory();

        AdvertisingPacket advertisingPacket = advertisingPacketFactory.createAdvertisingPacketWithSubFactories(IBEACON_ADVERTISING_DATA);
        assertTrue(advertisingPacket instanceof IBeaconAdvertisingPacket);
    }

    @Test
    public void createAdvertisingPacketWithSubFactories_hasSubFactories_createsCorrectPacket() {
        IBeaconAdvertisingPacketFactory advertisingPacketFactory = new IBeaconAdvertisingPacketFactory();
        IndoorPositioningAdvertisingPacketFactory testFactory = new IndoorPositioningAdvertisingPacketFactory();
        advertisingPacketFactory.addAdvertisingPacketFactory(testFactory);

        AdvertisingPacket advertisingPacket = advertisingPacketFactory.createAdvertisingPacketWithSubFactories(IBEACON_ADVERTISING_DATA);
        assertTrue(advertisingPacket instanceof IBeaconAdvertisingPacket);
        assertTrue(advertisingPacket instanceof IndoorPositioningAdvertisingPacket);
    }

    private class TestAdvertisingPacketFactory extends EddystoneAdvertisingPacketFactory {

        private TestAdvertisingPacketFactory() {
            super(TestAdvertisingPacket.class);
        }

        @Override
        public boolean canCreateAdvertisingPacket(byte[] advertisingData) {
            return advertisingData.length == 5;
        }
    }

    private class TestAdvertisingPacket extends EddystoneAdvertisingPacket {

        private TestAdvertisingPacket(byte[] data) {
            super(data);
        }

    }

}