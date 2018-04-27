package com.nexenio.bleindoorpositioning.ble.beacon.factory;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.EddystoneAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconFactory;
import com.nexenio.bleindoorpositioning.ble.beacon.Eddystone;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 02.02.18.
 */
public class BeaconFactoryTest {

    private BeaconFactory beaconFactory;

    @Before
    public void setUp() throws Exception {
        beaconFactory = new BeaconFactory();
    }

    @Test
    public void createBeacon_iBeaconAdvertisingPacket_createsIBeacon() throws Exception {
        byte[] advertisedData = new byte[]{};
        AdvertisingPacket advertisingPacket = new IBeaconAdvertisingPacket(advertisedData);
        Beacon beacon = beaconFactory.createBeacon(advertisingPacket);
        assertEquals(IBeacon.class, beacon.getClass());
    }

    @Test
    public void createBeacon_eddystoneAdvertisingPacket_createsEddystone() throws Exception {
        byte[] advertisedData = new byte[]{};
        AdvertisingPacket advertisingPacket = new EddystoneAdvertisingPacket(advertisedData);
        Beacon beacon = beaconFactory.createBeacon(advertisingPacket);
        assertEquals(Eddystone.class, beacon.getClass());
    }

    @Test
    public void createBeacon_customAdvertisingPacket_createsCustomBeacon() throws Exception {
        beaconFactory.addBeaconClass(CustomAdvertisingPacket.class, CustomBeacon.class);
        byte[] advertisedData = new byte[]{};
        AdvertisingPacket advertisingPacket = new CustomAdvertisingPacket(advertisedData);
        Beacon beacon = beaconFactory.createBeacon(advertisingPacket);
        assertEquals(CustomBeacon.class, beacon.getClass());
    }

    public static class CustomBeacon extends IBeacon<CustomAdvertisingPacket> {

        public CustomBeacon() {
        }

    }

    public static class CustomAdvertisingPacket extends IBeaconAdvertisingPacket {

        public CustomAdvertisingPacket(byte[] data) {
            super(data);
        }

    }

}