package com.nexenio.bleindoorpositioning.ble.advertising;

/**
 * Created by steppschuh on 02.02.18.
 */

public class IBeaconAdvertisingPacketFactory extends AdvertisingPacketFactory {

    @Override
    public boolean canCreateAdvertisingPacket(byte[] advertisingData) {
        return IBeaconAdvertisingPacket.meetsSpecification(advertisingData);
    }

    @Override
    public AdvertisingPacket createAdvertisingPacket(byte[] advertisingData) {
        return new IBeaconAdvertisingPacket(advertisingData);
    }

}
