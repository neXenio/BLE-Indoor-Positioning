package com.nexenio.bleindoorpositioning.ble.beacon.distance.benchmark;

import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconTest;
import com.nexenio.bleindoorpositioning.ble.beacon.distance.BeaconDistanceCalculator;
import com.nexenio.bleindoorpositioning.ble.beacon.distance.PathLossBeaconDistanceCalculator;
import com.nexenio.bleindoorpositioning.ble.beacon.signal.MeanFilter;
import com.nexenio.bleindoorpositioning.testutil.benchmark.BeaconInfo;
import com.nexenio.bleindoorpositioning.testutil.benchmark.DeviceInfo;
import com.nexenio.bleindoorpositioning.testutil.benchmark.RssiMeasurements;

import net.steppschuh.markdowngenerator.table.Table;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BeaconDistanceCalculatorBenchmarkTest {

    @Ignore
    @Test // TODO: integrate annotation like benchmark
    public void calculateDistanceTo() {
        RssiMeasurements rssiMeasurements = new RssiMeasurements();

        BeaconInfo beaconInfo = new BeaconInfo();
        beaconInfo.setAdvertisingFrequency(100);
        beaconInfo.setManufacturerData(BeaconTest.IBEACON_ADVERTISING_DATA);
        rssiMeasurements.setBeaconInfo(beaconInfo);

        DeviceInfo deviceInfo = new DeviceInfo();
        rssiMeasurements.setDeviceInfo(deviceInfo);

        int[] rssis = new int[]{-62, -65, -68, -61, -64, -65, -59, -61, -63}; // fake data
        rssiMeasurements.setRssis(rssis);

        float referenceDistance = 2F;
        rssiMeasurements.setDistance(referenceDistance);

        List<BeaconDistanceCalculator> beaconDistanceCalculators = new ArrayList<>();
        beaconDistanceCalculators.add(new PathLossBeaconDistanceCalculator());
        beaconDistanceCalculators.add(new MeanPathLossBeaconDistanceCalculator());

        List<RssiMeasurements> rssiMeasurementsList = new ArrayList<>();
        rssiMeasurementsList.add(rssiMeasurements);

        createBenchmark(beaconDistanceCalculators, rssiMeasurementsList);
    }

    public void createBenchmark(List<BeaconDistanceCalculator> beaconDistanceCalculators, List<RssiMeasurements> rssiMeasurements) {
        Map<BeaconDistanceCalculator, Float> scoreMap = new HashMap<>();

        for (RssiMeasurements rssiMeasurement : rssiMeasurements) {
            for (BeaconDistanceCalculator beaconDistanceCalculator : beaconDistanceCalculators) {
                BeaconDistanceCalculatorBenchmark beaconDistanceCalculatorBenchmark = new BeaconDistanceCalculatorBenchmark(beaconDistanceCalculator, rssiMeasurement);
                if (!scoreMap.containsKey(beaconDistanceCalculator)) {
                    scoreMap.put(beaconDistanceCalculator, 0F);
                }
                float distanceSum = scoreMap.get(beaconDistanceCalculator);
                distanceSum += beaconDistanceCalculatorBenchmark.getScore();
                scoreMap.put(beaconDistanceCalculator, distanceSum);
            }
        }

        // mean score
        for (Map.Entry<BeaconDistanceCalculator, Float> beaconDistanceCalculatorFloatEntry : scoreMap.entrySet()) {
            scoreMap.put(beaconDistanceCalculatorFloatEntry.getKey(), beaconDistanceCalculatorFloatEntry.getValue() / rssiMeasurements.size());
        }

        scoreMap = sortByValue(scoreMap, false);

        Table benchmarkTable = createBenchmarkTable(scoreMap, "Mean-Score");
        System.out.println(benchmarkTable.serialize());
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean ascending) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        if (ascending) {
            for (Map.Entry<K, V> entry : list) {
                result.put(entry.getKey(), entry.getValue());
            }
        } else {
            for (int i = list.size() - 1; i >= 0; i--) {
                result.put(list.get(i).getKey(), list.get(i).getValue());
            }
        }

        return result;
    }

    private Table createBenchmarkTable(Map<BeaconDistanceCalculator, Float> map, String scoreDescriptor) {
        Table.Builder tableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_RIGHT)
                .addRow("Calculator", scoreDescriptor);

        for (Map.Entry<BeaconDistanceCalculator, Float> entry : map.entrySet()) {
            tableBuilder.addRow(entry.getKey().getClass().getSimpleName(), entry.getValue());
        }

        return tableBuilder.build();
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