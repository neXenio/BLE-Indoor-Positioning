package com.nexenio.bleindoorpositioningdemo.recording;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.FilteredBeaconUpdateListener;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.IBeaconFilter;
import com.nexenio.bleindoorpositioningdemo.ExternalStorageUtils;
import com.nexenio.bleindoorpositioningdemo.bluetooth.BluetoothClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

public class AdvertisingPacketRecorder {

    private static final String TAG = AdvertisingPacketRecorder.class.getSimpleName();
    private static final String RECORDING_DIRECTORY_NAME = "Indoor Positioning Recording";
    private final static UUID RECORDING_UUID = UUID.fromString("61a0523a-a733-4789-ae8f-4f55fcff64f2");

    private IndoorPositioningRecording indoorPositioningRecording = new IndoorPositioningRecording();
    private FilteredBeaconUpdateListener<IBeacon<IBeaconAdvertisingPacket>> recordingBeaconUpdateListener;

    @Nullable
    private List<AdvertisingPacket> advertisingPacketList;

    private long duration;
    private long offset;
    private String comment;
    @Nullable
    private RecordingObserver observer;

    private boolean isRecording = false;

    public AdvertisingPacketRecorder(String comment, long duration, long offset) {
        this.comment = comment;
        this.duration = duration;
        this.offset = offset;
    }

    public AdvertisingPacketRecorder(String comment, long duration, long offset, @Nullable RecordingObserver observer) {
        this(comment, duration, offset);
        this.observer = observer;
    }

    public void initializeBluetoothScanning(Context context) {
        Log.d(TAG, "Initializing Bluetooth scanning");
        BluetoothClient.initialize(context);
    }

    public void startRecording() {
        Handler handler = new Handler();
        handler.postDelayed(this::record, TimeUnit.SECONDS.toMillis(offset));

        if (duration > 0) {
            handler.postDelayed(this::stopRecording, TimeUnit.SECONDS.toMillis(duration + offset));
        }
    }

    public void stopRecording() {
        if (!isRecording) {
            return;
        }
        BluetoothClient.stopScanning();
        BeaconManager.unregisterBeaconUpdateListener(recordingBeaconUpdateListener);

        indoorPositioningRecording.setEndTimestamp(System.currentTimeMillis());
        indoorPositioningRecording.setAdvertisingPacketList(advertisingPacketList);

        isRecording = false;

        Log.i(TAG, "Indoor Positioning Recording:\n" + indoorPositioningRecording);

        String jsonString = createJsonString(indoorPositioningRecording);

        String comment = this.comment.replace(" ", "-");
        comment = comment.replace("_", "-");
        String fileName = "indoor-recording_" + comment + "_" + indoorPositioningRecording.getStartTimestamp() + "_" + indoorPositioningRecording.getEndTimestamp() + ".json";
        persistJsonFile(fileName, jsonString);

        if (advertisingPacketList != null) {
            advertisingPacketList.clear();
        }

        Bundle bundle = new Bundle();
        bundle.putString("fileName", fileName);

        if (observer != null) {
            observer.onRecordingStopped(bundle);
        }
    }

    private void onRecordingBeaconUpdated(IBeacon<IBeaconAdvertisingPacket> beacon) {
        if (advertisingPacketList != null) {
            IBeaconAdvertisingPacket latestAdvertisingPacket = beacon.getLatestAdvertisingPacket();
            advertisingPacketList.add(latestAdvertisingPacket);
        }
    }

    private void record() {
        if (isRecording) {
            return;
        }
        Log.d(TAG, "Starting recording");
        advertisingPacketList = new ArrayList<>();
        indoorPositioningRecording.setStartTimestamp(System.currentTimeMillis());
        setupUuidFilter();
        BluetoothClient.startScanning();
        BeaconManager.registerBeaconUpdateListener(recordingBeaconUpdateListener);
        isRecording = true;
    }

    private void setupUuidFilter() {
        IBeaconFilter<IBeacon<IBeaconAdvertisingPacket>> recordingBeaconFilter = new IBeaconFilter<>(RECORDING_UUID);
        recordingBeaconUpdateListener = new FilteredBeaconUpdateListener<IBeacon<IBeaconAdvertisingPacket>>(recordingBeaconFilter) {
            @Override
            public void onMatchingBeaconUpdated(IBeacon<IBeaconAdvertisingPacket> beacon) {
                onRecordingBeaconUpdated(beacon);
            }
        };
    }

    /*
     * File handling
     */

    private File persistJsonFile(String fileName, String jsonString) {
        File documentsDirectory = ExternalStorageUtils.getDocumentsDirectory(RECORDING_DIRECTORY_NAME);
        File file = new File(documentsDirectory, fileName);
        try {
            if (!documentsDirectory.exists()) {
                documentsDirectory.mkdirs();
            }
            ExternalStorageUtils.writeStringToFile(jsonString, file, false);
        } catch (IOException e) {
            // TODO: log + toast
            e.printStackTrace();
        }
        return file;
    }

    private static String createJsonString(IndoorPositioningRecording indoorPositioningRecording) {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting();

        Gson gson = gsonBuilder.create();
        return gson.toJson(indoorPositioningRecording);
    }

    public long getOffset() {
        return offset;
    }

    public interface RecordingObserver {

        void onRecordingStopped(Bundle bundle);

    }

}
