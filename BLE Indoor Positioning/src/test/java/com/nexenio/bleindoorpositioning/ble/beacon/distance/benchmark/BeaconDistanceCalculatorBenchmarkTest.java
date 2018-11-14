package com.nexenio.bleindoorpositioning.ble.beacon.distance.benchmark;

import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconTest;
import com.nexenio.bleindoorpositioning.ble.beacon.distance.PathLossBeaconDistanceCalculator;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.MeanFilter;
import com.nexenio.bleindoorpositioning.testutil.benchmark.BeaconInfo;
import com.nexenio.bleindoorpositioning.testutil.benchmark.DeviceInfo;
import com.nexenio.bleindoorpositioning.testutil.benchmark.RssiMeasurements;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BeaconDistanceCalculatorBenchmarkTest {

    @Ignore
    @Test
    public void calculateDistanceTo() {
        RssiMeasurements rssiMeasurements = new RssiMeasurements();

        BeaconInfo beaconInfo = new BeaconInfo();
        beaconInfo.setAdvertisingFrequency(100); // TODO: needs documentation
        beaconInfo.setManufacturerData(BeaconTest.IBEACON_ADVERTISING_DATA);
        rssiMeasurements.setBeaconInfo(beaconInfo);

        DeviceInfo deviceInfo = new DeviceInfo();
        rssiMeasurements.setDeviceInfo(deviceInfo);

        int[] rssis = new int[]{-62, -65, -68, -61, -64, -65, -59, -61, -63}; // fake data
        rssiMeasurements.setRssis(rssis);

        float referenceDistance = 2F;
        rssiMeasurements.setDistance(referenceDistance);

        BeaconDistanceCalculatorBenchmark beaconDistanceCalculatorBenchmark1 = new BeaconDistanceCalculatorBenchmark(new PathLossBeaconDistanceCalculator(), rssiMeasurements);
        BeaconDistanceCalculatorBenchmark beaconDistanceCalculatorBenchmark2 = new BeaconDistanceCalculatorBenchmark(new MeanPathLossBeaconDistanceCalculator(), rssiMeasurements);

        float distance1 = beaconDistanceCalculatorBenchmark1.calculateDistance();
        float distance2 = beaconDistanceCalculatorBenchmark2.calculateDistance();

        String message = Math.abs(referenceDistance - distance1) < Math.abs(referenceDistance - distance2) ? "Calculator 1 performs better" : "Calculator 2 performs better";
        System.out.println(message);
    }

    @Test
    public void createAdvertisingPacketsForRssis_createsAdvertisingPackets() {
        // arrange
        int[] rssis = new int[]{-62, -65, -68, -61, -64, -65, -59, -61, -63}; // fake data
        int beaconTransmissionPower = -43;
        int advertisingFrequency = 10;

        // act
        List<IBeaconAdvertisingPacket> advertisingPackets = BeaconDistanceCalculatorBenchmark.createAdvertisingPacketsForRssis(rssis, BeaconTest.IBEACON_ADVERTISING_DATA, beaconTransmissionPower, System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(5), advertisingFrequency);

        // assert
        assertEquals(rssis.length, advertisingPackets.size());
        for (int i = 0; i < advertisingPackets.size(); i++) {
            assertArrayEquals(BeaconTest.IBEACON_ADVERTISING_DATA, advertisingPackets.get(i).getData());
            assertEquals(rssis[i], advertisingPackets.get(i).getRssi());
            assertEquals(beaconTransmissionPower, advertisingPackets.get(i).getMeasuredPowerByte());
        }
    }

    private class MeanPathLossBeaconDistanceCalculator extends PathLossBeaconDistanceCalculator {

        @Override
        public float calculateDistanceTo(Beacon beacon) {
            return calculateDistanceTo(beacon, new MeanFilter(3, TimeUnit.SECONDS, beacon.getLatestTimestamp()));
        }
    }

}