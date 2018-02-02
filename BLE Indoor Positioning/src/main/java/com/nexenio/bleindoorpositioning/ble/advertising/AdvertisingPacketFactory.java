package com.nexenio.bleindoorpositioning.ble.advertising;

/**
 * Created by steppschuh on 02.02.18.
 */

public interface AdvertisingPacketFactory {

    AdvertisingPacket createAdvertisingPacket(byte[] advertisingData);

}
