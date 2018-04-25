package com.nexenio.bleindoorpositioning.ble.advertising;

/**
 * Created by steppschuh on 02.02.18.
 */

public class EddystoneAdvertisingPacketFactory extends AdvertisingPacketFactory {

    public EddystoneAdvertisingPacketFactory() {
        this(EddystoneAdvertisingPacket.class);
    }

    public <AP extends AdvertisingPacket> EddystoneAdvertisingPacketFactory(Class<AP> packetClass) {
        super(packetClass);
    }

    @Override
    public boolean canCreateAdvertisingPacket(byte[] advertisingData) {
        return EddystoneAdvertisingPacket.meetsSpecification(advertisingData);
    }

    @Override
    public AdvertisingPacket createAdvertisingPacket(byte[] advertisingData) {
        return new EddystoneAdvertisingPacket(advertisingData);
    }

}
