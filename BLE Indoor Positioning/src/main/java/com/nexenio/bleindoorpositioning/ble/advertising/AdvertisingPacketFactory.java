package com.nexenio.bleindoorpositioning.ble.advertising;

/**
 * Created by steppschuh on 02.02.18.
 */

public interface AdvertisingPacketFactory<A extends AdvertisingPacket> {

    boolean couldCreateAdvertisingPacket(byte[] advertisingData);

    A createAdvertisingPacket(byte[] advertisingData);

}
