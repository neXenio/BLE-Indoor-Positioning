package com.nexenio.bleindoorpositioning.ble.advertising;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 15.11.17.
 */

public abstract class AdvertisingPacket {

    public static final long MAXIMUM_PACKET_AGE = TimeUnit.SECONDS.toMillis(30);

    protected byte[] data;
    protected long timestamp;

    public AdvertisingPacket(byte[] data) {
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return AdvertisingPacketUtil.toHexadecimalString(data);
    }

}
