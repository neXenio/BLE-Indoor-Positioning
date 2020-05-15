package com.nexenio.bleindoorpositioning.ble.beacon.distance.benchmark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.nexenio.bleindoorpositioning.testutil.benchmark.RssiMeasurements;
import com.nexenio.bleindoorpositioning.util.ExternalStorageUtil;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;

public class BeaconDistanceCalculatorBenchmarkUtil {

    private static final Gson gson = (new GsonBuilder()).excludeFieldsWithoutExposeAnnotation().create();

    public static File getRssiMeasurementsDirectory() {
        String currentWorkingDirectory = System.getProperty("user.dir");
        String resourcesPath = currentWorkingDirectory + "/src/test/resources/rssimeasurements/";
        return new File(resourcesPath);
    }

    public static Observable<RssiMeasurements> getRssiMeasurements() {
        return ExternalStorageUtil.getObservableForJsonFilesInDirectory(getRssiMeasurementsDirectory())
                .map(BeaconDistanceCalculatorBenchmarkUtil::getRssiMeasurementsFromFile);
    }

    public static RssiMeasurements getRssiMeasurementsFromFile(File file) throws IOException {
        String json = ExternalStorageUtil.readFile(file);
        return gson.fromJson(json, RssiMeasurements.class);
    }

}
