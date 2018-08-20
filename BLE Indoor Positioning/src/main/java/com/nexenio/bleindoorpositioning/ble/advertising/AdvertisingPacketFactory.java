package com.nexenio.bleindoorpositioning.ble.advertising;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steppschuh on 02.02.18.
 */

public abstract class AdvertisingPacketFactory<AP extends AdvertisingPacket> {

    private final Map<Class<AP>, AdvertisingPacketFactory<AP>> subFactoryMap = new HashMap<>();
    private Class<AP> packetClass;

    public AdvertisingPacketFactory(Class<AP> packetClass) {
        this.packetClass = packetClass;
    }

    abstract boolean canCreateAdvertisingPacket(byte[] advertisingData);

    protected boolean canCreateAdvertisingPacketWithSubFactories(byte[] advertisingData) {
        for (AdvertisingPacketFactory<AP> advertisingPacketFactory : subFactoryMap.values()) {
            if (advertisingPacketFactory.canCreateAdvertisingPacketWithSubFactories(advertisingData)) {
                return true;
            }
        }
        return canCreateAdvertisingPacket(advertisingData);
    }

    abstract AP createAdvertisingPacket(byte[] advertisingData);

    protected AP createAdvertisingPacketWithSubFactories(byte[] advertisingData) {
        for (AdvertisingPacketFactory<AP> advertisingPacketFactory : subFactoryMap.values()) {
            if (advertisingPacketFactory.canCreateAdvertisingPacketWithSubFactories(advertisingData)) {
                return advertisingPacketFactory.createAdvertisingPacket(advertisingData);
            }
        }
        return createAdvertisingPacket(advertisingData);
    }

    public AdvertisingPacketFactory<AP> getAdvertisingPacketFactory(Class advertisingPacketClass) {
        if (!packetClass.isAssignableFrom(advertisingPacketClass)) {
            return null;
        }
        if (subFactoryMap.containsKey(advertisingPacketClass)) {
            return subFactoryMap.get(advertisingPacketClass);
        } else {
            for (Map.Entry<Class<AP>, AdvertisingPacketFactory<AP>> classAdvertisingPacketFactoryEntry : subFactoryMap.entrySet()) {
                if (classAdvertisingPacketFactoryEntry.getKey().isAssignableFrom(advertisingPacketClass)) {
                    return classAdvertisingPacketFactoryEntry.getValue().getAdvertisingPacketFactory(advertisingPacketClass);
                }
            }
            return null;
        }
    }

    public AdvertisingPacketFactory<AP> getAdvertisingPacketFactory(byte[] advertisingData) {
        if (!canCreateAdvertisingPacket(advertisingData)) {
            return null;
        }

        for (AdvertisingPacketFactory<AP> advertisingPacketFactory : subFactoryMap.values()) {
            if (advertisingPacketFactory.getAdvertisingPacketFactory(advertisingData) != null) {
                return advertisingPacketFactory;
            }
        }
        return this;
    }

    public <F extends AdvertisingPacketFactory<AP>> void addAdvertisingPacketFactory(F factory) {
        if (!packetClass.isAssignableFrom(factory.getPacketClass())) {
            return;
        }
        if (packetClass == factory.getPacketClass().getSuperclass() && !subFactoryMap.containsKey(factory.getPacketClass())) {
            subFactoryMap.put(factory.getPacketClass(), factory);
        } else {
            for (Map.Entry<Class<AP>, AdvertisingPacketFactory<AP>> classAdvertisingPacketFactoryEntry : subFactoryMap.entrySet()) {
                if (classAdvertisingPacketFactoryEntry.getKey().isAssignableFrom(factory.getPacketClass())) {
                    classAdvertisingPacketFactoryEntry.getValue().addAdvertisingPacketFactory(factory);
                }
            }
        }
    }

    public void removeAdvertisingPacketFactory(Class<AP> advertisingPacketClass) {
        if (!packetClass.isAssignableFrom(advertisingPacketClass)) {
            return;
        }
        if (packetClass == advertisingPacketClass.getSuperclass() && subFactoryMap.containsKey(advertisingPacketClass)) {
            subFactoryMap.remove(advertisingPacketClass);
        } else {
            for (Map.Entry<Class<AP>, AdvertisingPacketFactory<AP>> classAdvertisingPacketFactoryEntry : subFactoryMap.entrySet()) {
                if (classAdvertisingPacketFactoryEntry.getKey().isAssignableFrom(advertisingPacketClass)) {
                    classAdvertisingPacketFactoryEntry.getValue().removeAdvertisingPacketFactory(advertisingPacketClass);
                }
            }
        }
    }

    public <F extends AdvertisingPacketFactory<AP>> void removeAdvertisingPacketFactory(F factory) {
        removeAdvertisingPacketFactory(factory.getPacketClass());
    }

    Map<Class<AP>, AdvertisingPacketFactory<AP>> getSubFactoryMap() {
        return subFactoryMap;
    }

    public Class<AP> getPacketClass() {
        return packetClass;
    }

}
