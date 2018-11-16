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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BeaconDistanceCalculatorBenchmarkTest {

    @Ignore
    @Test // TODO: integrate annotation like benchmark
    public void calculateDistanceTo() {
        List<BeaconDistanceCalculator> beaconDistanceCalculators = new ArrayList<>();
        beaconDistanceCalculators.add(new PathLossBeaconDistanceCalculator());
        beaconDistanceCalculators.add(new MeanPathLossBeaconDistanceCalculator());

        List<RssiMeasurements> rssiMeasurementsList = new ArrayList<>();
        // TODO: read real values when exist
        // BeaconDistanceCalculatorBenchmarkUtil
        //        .getRssiMeasurements().filter(rssiMeasurements -> rssiMeasurements.getDistance() == 2) // example filter
        //        .subscribe(rssiMeasurements -> rssiMeasurementsList.add(rssiMeasurements));
        rssiMeasurementsList.add(createFakeRssiMeasurements());

        createBenchmark(beaconDistanceCalculators, rssiMeasurementsList, new MeanScoreMerger());
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

    private void createBenchmark(List<BeaconDistanceCalculator> beaconDistanceCalculators, List<RssiMeasurements> rssiMeasurements, ScoreMerger scoreMerger) {
        Map<BeaconDistanceCalculator, List<Double>> scoreMap = new HashMap<>();

        for (RssiMeasurements rssiMeasurement : rssiMeasurements) {
            for (BeaconDistanceCalculator beaconDistanceCalculator : beaconDistanceCalculators) {
                BeaconDistanceCalculatorBenchmark beaconDistanceCalculatorBenchmark = new BeaconDistanceCalculatorBenchmark(beaconDistanceCalculator, rssiMeasurement);
                if (!scoreMap.containsKey(beaconDistanceCalculator)) {
                    scoreMap.put(beaconDistanceCalculator, new ArrayList<>());
                }
                scoreMap.get(beaconDistanceCalculator).add(beaconDistanceCalculatorBenchmark.getScore());
            }
        }

        Map<BeaconDistanceCalculator, Double> finalScoreMap = new HashMap<>();
        for (Map.Entry<BeaconDistanceCalculator, List<Double>> beaconDistanceCalculatorDoubleEntry : scoreMap.entrySet()) {
            finalScoreMap.put(beaconDistanceCalculatorDoubleEntry.getKey(), scoreMerger.mergeScores(beaconDistanceCalculatorDoubleEntry.getValue()));
        }

        finalScoreMap = sortMapByValue(finalScoreMap);

        Table benchmarkTable = createBenchmarkTable(finalScoreMap, scoreMerger.getName());
        System.out.println(benchmarkTable.serialize());
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private Table createBenchmarkTable(Map<BeaconDistanceCalculator, Double> map, String scoreDescriptor) {
        Table.Builder tableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_RIGHT)
                .addRow("Calculator", scoreDescriptor);

        for (Map.Entry<BeaconDistanceCalculator, Double> entry : map.entrySet()) {
            tableBuilder.addRow(entry.getKey().getClass().getSimpleName(), String.format("%.04f", entry.getValue()));
        }

        return tableBuilder.build();
    }

    private RssiMeasurements createFakeRssiMeasurements() {
        RssiMeasurements rssiMeasurements = new RssiMeasurements();

        rssiMeasurements.setBeaconInfo(createFakeBeaconInfo());
        rssiMeasurements.setDeviceInfo(createFakeDeviceInfo());

        int[] rssis = new int[]{-62, -65, -68, -61, -64, -65, -59, -61, -63};
        rssiMeasurements.setRssis(rssis);
        rssiMeasurements.setDistance(2F);
        return rssiMeasurements;
    }

    private BeaconInfo createFakeBeaconInfo() {
        BeaconInfo beaconInfo = new BeaconInfo();
        beaconInfo.setAdvertisingFrequency(100);
        beaconInfo.setManufacturerData(BeaconTest.IBEACON_ADVERTISING_DATA);
        return beaconInfo;
    }

    private DeviceInfo createFakeDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        return deviceInfo;
    }

    private class MeanPathLossBeaconDistanceCalculator extends PathLossBeaconDistanceCalculator {

        @Override
        public float calculateDistanceTo(Beacon beacon) {
            // Change beacon default beacon filter to a mean filter
            return calculateDistanceTo(beacon, new MeanFilter(3, TimeUnit.SECONDS, beacon.getLatestTimestamp()));
        }
    }

}