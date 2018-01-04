package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;

import java.util.List;

/**
 * Created by leon on 03.01.18.
 */

public interface RssiFilter {

    float filter(List<? extends AdvertisingPacket> advertisingPackets);

}
