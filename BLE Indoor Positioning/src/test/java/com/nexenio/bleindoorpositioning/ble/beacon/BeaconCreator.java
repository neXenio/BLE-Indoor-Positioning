package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.testUtil.AbstractBeaconCreator;

public class BeaconCreator<B extends Beacon> extends AbstractBeaconCreator<B> {

    public BeaconCreator(Class<B> beaconClass) {
        super(beaconClass);
    }

    @Override
    public AdvertisingPacket getAdvertisingPacketForBeaconClass(Class<B> beaconClass) throws InstantiationException {
        String beaconClassName = beaconClass.getSimpleName();
        if (beaconClassName.equals(IBeacon.class.getSimpleName())) {
            return new IBeaconAdvertisingPacket(new byte[30]);
        }
        throw new InstantiationException("No registered advertising packet for beacon class " + beaconClassName);
    }

}
