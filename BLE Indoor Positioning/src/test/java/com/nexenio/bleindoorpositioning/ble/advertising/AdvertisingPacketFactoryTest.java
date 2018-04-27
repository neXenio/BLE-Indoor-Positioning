package com.nexenio.bleindoorpositioning.ble.advertising;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdvertisingPacketFactoryTest {

    @Test
    public void test() throws Exception {
        EddystoneAdvertisingPacketFactory advertisingPacketFactory = new EddystoneAdvertisingPacketFactory(EddystoneAdvertisingPacket.class);
        AdvertisingPacketFactory testFactory = new TestAdvertisingPacketFactory();
        advertisingPacketFactory.addAdvertisingPacketFactory(testFactory);
        assertEquals(1, advertisingPacketFactory.getSubFactoryMap().size());
        assertEquals(testFactory, advertisingPacketFactory.getAdvertisingPacketFactory(TestAdvertisingPacket.class));

        assertFalse(advertisingPacketFactory.canCreateAdvertisingPacket(new byte[5]));
        assertTrue(advertisingPacketFactory.canCreateAdvertisingPacketWithSubFactories(new byte[5]));

        advertisingPacketFactory.removeAdvertisingPacketFactory(testFactory);
        assertEquals(0, advertisingPacketFactory.getSubFactoryMap().size());
        assertEquals(null, advertisingPacketFactory.getAdvertisingPacketFactory(TestAdvertisingPacket.class));
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