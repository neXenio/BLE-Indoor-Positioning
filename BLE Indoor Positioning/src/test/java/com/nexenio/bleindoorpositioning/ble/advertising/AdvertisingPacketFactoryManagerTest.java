package com.nexenio.bleindoorpositioning.ble.advertising;

import com.nexenio.bleindoorpositioning.ble.beacon.factory.BeaconFactoryTest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by steppschuh on 05.02.18.
 */
public class AdvertisingPacketFactoryManagerTest {

    private AdvertisingPacketFactoryManager advertisingPacketFactoryManager;

    @Before
    public void setUp() throws Exception {
        advertisingPacketFactoryManager = new AdvertisingPacketFactoryManager();
    }

    @Test
    public void getAdvertisingPacketFactory_defaultFactories_returnsNull() throws Exception {
        AdvertisingPacketFactory factory = advertisingPacketFactoryManager.getAdvertisingPacketFactory(new byte[]{});
        assertNull(factory);
    }

    @Test
    public void getAdvertisingPacketFactory_customFactory_returnsFactory() throws Exception {
        AdvertisingPacketFactory customFactory = new CustomAdvertisingPacketFactory();
        advertisingPacketFactoryManager.addAdvertisingPacketFactory(customFactory);
        AdvertisingPacketFactory factory = advertisingPacketFactoryManager.getAdvertisingPacketFactory(new byte[]{});
        assertEquals(customFactory, factory);
    }

    @Test
    public void addAdvertisingPacketFactory_newFactory_firstElement() throws Exception {
        IBeaconAdvertisingPacketFactory factory = new IBeaconAdvertisingPacketFactory();
        advertisingPacketFactoryManager.addAdvertisingPacketFactory(factory);
        assertEquals(0, advertisingPacketFactoryManager.getAdvertisingPacketFactories().indexOf(factory));
    }

    public static class CustomAdvertisingPacketFactory extends AdvertisingPacketFactory {

        public CustomAdvertisingPacketFactory() {
            super(BeaconFactoryTest.CustomAdvertisingPacket.class);
        }

        public boolean canCreateAdvertisingPacket(byte[] advertisingData) {
            return true;
        }

        public AdvertisingPacket createAdvertisingPacket(byte[] advertisingData) {
            return new BeaconFactoryTest.CustomAdvertisingPacket(advertisingData);
        }

    }

}