package com.nexenio.bleindoorpositioning.ble.advertising;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steppschuh on 05.02.18.
 */

public class AdvertisingPacketFactoryManager {

    /**
     * A list of factories that may be used for creating {@link AdvertisingPacket}s.
     *
     * Note: The order of elements in this list is important! The first matching factory will always
     * be used. See {@link #getAdvertisingPacketFactory(byte[])}.
     */
    private List<AdvertisingPacketFactory> advertisingPacketFactories = new ArrayList<>();

    public AdvertisingPacketFactoryManager() {
        advertisingPacketFactories.add(new EddystoneAdvertisingPacketFactory());
        advertisingPacketFactories.add(new IBeaconAdvertisingPacketFactory());
    }

    public AdvertisingPacket createAdvertisingPacket(byte[] advertisingData) {
        AdvertisingPacketFactory advertisingPacketFactory = getAdvertisingPacketFactory(advertisingData);
        return advertisingPacketFactory != null ? advertisingPacketFactory.createAdvertisingPacket(advertisingData) : null;
    }

    /**
     * Iterates over {@link #advertisingPacketFactories} and returns the first element that returns
     * true when calling {@link AdvertisingPacketFactory#canCreateAdvertisingPacket(byte[])}.
     *
     * Returns null if no matching factory was found.
     */
    public AdvertisingPacketFactory getAdvertisingPacketFactory(byte[] advertisingData) {
        AdvertisingPacketFactory factory = null;
        for (AdvertisingPacketFactory advertisingPacketFactory : advertisingPacketFactories) {
            factory = advertisingPacketFactory.getAdvertisingPacketFactory(advertisingData);
            if (factory != null) {
                break;
            }
        }
        return factory;
    }

    /**
     * Inserts the specified factory into {@link #advertisingPacketFactories}.
     *
     * Note: The specified factory will be set to the first element in the list and thus may be used
     * before the already existing factories.
     */
    public void addAdvertisingPacketFactory(AdvertisingPacketFactory advertisingPacketFactory) {
        advertisingPacketFactories.add(0, advertisingPacketFactory);
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
