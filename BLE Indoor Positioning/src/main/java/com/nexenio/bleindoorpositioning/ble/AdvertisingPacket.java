package com.nexenio.bleindoorpositioning.ble;

import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 15.11.17.
 */

public class AdvertisingPacket {

    public static final long MAXIMUM_PACKET_AGE = TimeUnit.SECONDS.toMillis(30);

    private byte[] data;
    private long timestamp;

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

}
