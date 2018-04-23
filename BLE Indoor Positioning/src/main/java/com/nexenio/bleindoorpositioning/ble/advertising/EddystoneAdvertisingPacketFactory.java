package com.nexenio.bleindoorpositioning.ble.advertising;

/**
 * Created by steppschuh on 02.02.18.
 */

public class EddystoneAdvertisingPacketFactory extends AdvertisingPacketFactory {

    @Override
    public boolean canCreateAdvertisingPacket(byte[] advertisingData) {
        return EddystoneAdvertisingPacket.meetsSpecification(advertisingData);
    }

    @Override
    public AdvertisingPacket createAdvertisingPacket(byte[] advertisingData) {
        return new EddystoneAdvertisingPacket(advertisingData);
    }

}
