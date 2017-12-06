package com.nexenio.bleindoorpositioning.ble.advertising;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by steppschuh on 06.12.17.
 */

public abstract class AdvertisingPacketUtil {

    public static String toHexadecimalString(byte[] bytes) {
        BigInteger bigInteger = new BigInteger(bytes);
        return "0x" + bigInteger.toString(16).toUpperCase();
    }

    public static UUID toUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }

}
