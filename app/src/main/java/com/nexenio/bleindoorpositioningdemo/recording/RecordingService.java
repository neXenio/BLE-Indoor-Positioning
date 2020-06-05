package com.nexenio.bleindoorpositioningdemo.recording;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.FilteredBeaconUpdateListener;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.IBeaconFilter;
import com.nexenio.bleindoorpositioningdemo.ExternalStorageUtils;
import com.nexenio.bleindoorpositioningdemo.bluetooth.BluetoothClient;
import com.nexenio.bleindoorpositioningdemo.notification.NotificationManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.rxjava3.core.Completable;

import static com.nexenio.bleindoorpositioningdemo.recording.OverallRecordingActivity.RECORDING_FINISHED;

public class RecordingService extends Service {

    private static final String TAG = RecordingService.class.getSimpleName();
    private static final String RECORDING_DIRECTORY_NAME = "Indoor Positioning Recording";

    private final static UUID RECORDING_UUID = UUID.fromString("61a0523a-a733-4789-ae8f-4f55fcff64f2");

    private IndoorPositioningRecording indoorPositioningRecording = new IndoorPositioningRecording();

    private FilteredBeaconUpdateListener<IBeacon<IBeaconAdvertisingPacket>> recordingBeaconUpdateListener;

    long id;

    @Nullable
    ResultReceiver resultReceiver;

    @Nullable
    private List<AdvertisingPacket> advertisingPacketList;

    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = new NotificationManager();
        notificationManager.initialize(this).blockingAwait();
        promoteToForeground();
    }

    protected void promoteToForeground() {
        startForeground(NotificationManager.NOTIFICATION_ID_STATUS, createForegroundNotification());
    }

    protected Notification createForegroundNotification() {
        return notificationManager
                .createStatusNotificationBuilder(this.getClass(), OverallRecordingActivity.class)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if (advertisingPacketList != null) {
        //    resultReceiver = intent.getParcelableExtra("receiverTag");
        // } else {
        super.onStartCommand(intent, flags, startId);
        resultReceiver = intent.getParcelableExtra("receiverTag");
        id = intent.getLongExtra("id", -1);
        long duration = intent.getLongExtra("duration", 0);
        long offset = intent.getLongExtra("offset", 0);

        Handler handler = new Handler();
        handler.postDelayed(this::startRecording, TimeUnit.SECONDS.toMillis(offset));

        if (duration > 0) {
            handler.postDelayed(this::stopRecording, TimeUnit.SECONDS.toMillis(duration + offset));
        }
        // }

        NotificationManager.getBundleFromIntent(intent)
                .flatMap(NotificationManager::getActionFromBundle)
                .flatMapCompletable(this::processNotificationAction)
                .subscribe(
                        () -> Log.d(TAG, "Processed notification action"),
                        throwable -> Log.w(TAG, "Unable to process notification action", throwable)
                );

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopRecording();
        super.onDestroy();
    }

    private void initializeBluetoothScanning() {
        Log.d(TAG, "Initializing Bluetooth scanning");

        BluetoothClient.initialize(this);

        // TODO: 24.05.20 change recording uuid to mp-project-specific-value

        IBeaconFilter<IBeacon<IBeaconAdvertisingPacket>> recordingBeaconFilter = new IBeaconFilter<>(RECORDING_UUID);
        recordingBeaconUpdateListener = new FilteredBeaconUpdateListener<IBeacon<IBeaconAdvertisingPacket>>(recordingBeaconFilter) {
            @Override
            public void onMatchingBeaconUpdated(IBeacon<IBeaconAdvertisingPacket> beacon) {
                onRecordingBeaconUpdated(beacon);
            }
        };
    }

    private void onRecordingBeaconUpdated(IBeacon<IBeaconAdvertisingPacket> beacon) {
        IBeaconAdvertisingPacket latestAdvertisingPacket = beacon.getLatestAdvertisingPacket();
        advertisingPacketList.add(latestAdvertisingPacket);
    }

    private void startRecording() {
        Log.d(TAG, "Starting recording");
        advertisingPacketList = new ArrayList<>();
        indoorPositioningRecording.setStartTimestamp(System.currentTimeMillis());

        initializeBluetoothScanning();
        BluetoothClient.startScanning();
        BeaconManager.registerBeaconUpdateListener(recordingBeaconUpdateListener);
    }

    private void stopRecording() {
        BluetoothClient.stopScanning();
        BeaconManager.unregisterBeaconUpdateListener(recordingBeaconUpdateListener);

        indoorPositioningRecording.setEndTimestamp(System.currentTimeMillis());
        indoorPositioningRecording.setAdvertisingPacketList(advertisingPacketList);

        Log.i(TAG, "Indoor Positioning Recording:\n" + indoorPositioningRecording);

        String jsonString = createJsonString(indoorPositioningRecording);
        String fileName = "indoor-recording_" + id + "_" + indoorPositioningRecording.getStartTimestamp() + "_" + indoorPositioningRecording.getEndTimestamp() + ".json";
        persistJsonFile(fileName, jsonString);

        if (advertisingPacketList != null) {
            advertisingPacketList.clear();
        }

        Bundle bundle = new Bundle();
        bundle.putString("fileName", fileName);
        if (resultReceiver != null) {
            resultReceiver.send(RECORDING_FINISHED, bundle);
        }
    }

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

    protected Completable processNotificationAction(int action) {
        return Completable.fromAction(() -> {
            switch (action) {
                case NotificationManager.ACTION_STOP:
                    stopSelf();
                    System.exit(0);
                    break;
                default:
                    Log.w(TAG, "Unknown notification action: " + action);
            }
        });
    }

}
