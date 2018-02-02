package com.nexenio.bleindoorpositioning.ble.advertising;

/**
 * Created by steppschuh on 02.02.18.
 */

public class IBeaconAdvertisingPacketFactory implements AdvertisingPacketFactory {

    @Override
    public AdvertisingPacket createAdvertisingPacket(byte[] advertisingData) {
        if (!IBeaconAdvertisingPacket.meetsSpecification(advertisingData)) {
            return null;
        }
        return new IBeaconAdvertisingPacket(advertisingData);
    }

}
