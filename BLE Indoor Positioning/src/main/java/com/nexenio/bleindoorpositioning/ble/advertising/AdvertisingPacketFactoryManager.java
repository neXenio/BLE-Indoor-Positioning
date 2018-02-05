package com.nexenio.bleindoorpositioning.ble.advertising;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steppschuh on 05.02.18.
 */

public class AdvertisingPacketFactoryManager implements AdvertisingPacketFactory {

    /**
     * A list of factories that may be used for creating {@link AdvertisingPacket}s.
     *
     * Note that the order of elements in this list is important! The first matching factory will
     * always be used. See {@link #getAdvertisingPacketFactory(byte[])}.
     */
    private List<AdvertisingPacketFactory> advertisingPacketFactories = new ArrayList<>();

    public AdvertisingPacketFactoryManager() {
        advertisingPacketFactories.add(new EddystoneAdvertisingPacketFactory());
        advertisingPacketFactories.add(new IBeaconAdvertisingPacketFactory());
    }

    @Override
    public boolean couldCreateAdvertisingPacket(byte[] advertisingData) {
        return getAdvertisingPacketFactory(advertisingData) != null;
    }

    @Override
    public AdvertisingPacket createAdvertisingPacket(byte[] advertisingData) {
        AdvertisingPacketFactory advertisingPacketFactory = getAdvertisingPacketFactory(advertisingData);
        return advertisingPacketFactory != null ? advertisingPacketFactory.createAdvertisingPacket(advertisingData) : null;
    }

    /**
     * Iterates over {@link #advertisingPacketFactories} and returns the first element that returns
     * true when calling {@link AdvertisingPacketFactory#couldCreateAdvertisingPacket(byte[])}.
     *
     * Returns null if no matching factory was found.
     */
    public AdvertisingPacketFactory getAdvertisingPacketFactory(byte[] advertisingData) {
        for (AdvertisingPacketFactory advertisingPacketFactory : advertisingPacketFactories) {
            if (advertisingPacketFactory.couldCreateAdvertisingPacket(advertisingData)) {
                return advertisingPacketFactory;
            }
        }
        return null;
    }

    /*
        Getter & Setter
     */

    public List<AdvertisingPacketFactory> getAdvertisingPacketFactories() {
        return advertisingPacketFactories;
    }

    public void setAdvertisingPacketFactories(List<AdvertisingPacketFactory> advertisingPacketFactories) {
        this.advertisingPacketFactories = advertisingPacketFactories;
    }

}
