package com.nexenio.bleindoorpositioning.ble.advertising;

/**
 * Created by steppschuh on 02.02.18.
 */

public interface AdvertisingPacketFactory {

    boolean couldCreateAdvertisingPacket(byte[] advertisingData);

    AdvertisingPacket createAdvertisingPacket(byte[] advertisingData);

}
