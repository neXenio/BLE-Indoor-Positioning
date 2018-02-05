package com.nexenio.bleindoorpositioning.ble.advertising;

/**
 * Created by steppschuh on 02.02.18.
 */

public class IBeaconAdvertisingPacketFactory implements AdvertisingPacketFactory {

    @Override
    public boolean couldCreateAdvertisingPacket(byte[] advertisingData) {
        return IBeaconAdvertisingPacket.meetsSpecification(advertisingData);
    }

    @Override
    public AdvertisingPacket createAdvertisingPacket(byte[] advertisingData) {
        return new IBeaconAdvertisingPacket(advertisingData);
    }

}
