package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.testutil.AbstractBeaconCreator;

public class BeaconCreator<B extends Beacon> extends AbstractBeaconCreator<B> {

    public BeaconCreator(Class<B> beaconClass) {
        super(beaconClass);
    }

    @Override
    public AdvertisingPacket createAdvertisingPacketForBeaconClass(Class<B> beaconClass) throws InstantiationException {
        if (beaconClass == IBeacon.class) {
            return new IBeaconAdvertisingPacket(new byte[30]);
        }
        throw new InstantiationException("No registered advertising packet for beacon class " + beaconClass.getSimpleName());
    }

}
