package com.nexenio.bleindoorpositioning.ble.advertising;

/**
 * Created by steppschuh on 02.02.18.
 */

public class EddystoneAdvertisingPacketFactory implements AdvertisingPacketFactory {

    @Override
    public boolean couldCreateAdvertisingPacket(byte[] advertisingData) {
        return EddystoneAdvertisingPacket.meetsSpecification(advertisingData);
    }

    @Override
    public AdvertisingPacket createAdvertisingPacket(byte[] advertisingData) {
        return new EddystoneAdvertisingPacket(advertisingData);
    }

}
