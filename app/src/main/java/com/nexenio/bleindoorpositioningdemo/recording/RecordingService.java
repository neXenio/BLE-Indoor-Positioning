package com.nexenio.bleindoorpositioningdemo.recording;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.nexenio.bleindoorpositioningdemo.notification.NotificationManager;

import androidx.annotation.Nullable;
import io.reactivex.rxjava3.core.Completable;

import static com.nexenio.bleindoorpositioningdemo.recording.OverallRecordingActivity.RECORDING_FINISHED;

public class RecordingService extends Service {

    private static final String TAG = RecordingService.class.getSimpleName();

    long id;

    @Nullable
    ResultReceiver resultReceiver;
    private NotificationManager notificationManager;

    AdvertisingPacketRecorder advertisingPacketRecorder;

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
        super.onStartCommand(intent, flags, startId);
        createRecorder(intent);

        NotificationManager.getBundleFromIntent(intent)
                .flatMap(NotificationManager::getActionFromBundle)
                .flatMapCompletable(this::processNotificationAction)
                .subscribe(
                        () -> Log.d(TAG, "Processed notification action"),
                        throwable -> Log.w(TAG, "Unable to process notification action", throwable)
                );
        advertisingPacketRecorder.initializeBluetoothScanning(this);
        advertisingPacketRecorder.startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopRecording();
        super.onDestroy();
    }

    private void createRecorder(Intent intent) {
        resultReceiver = intent.getParcelableExtra("receiverTag");
        id = intent.getLongExtra("id", -1);
        long duration = intent.getLongExtra("duration", 0);
        long offset = intent.getLongExtra("offset", 0);
        advertisingPacketRecorder = new AdvertisingPacketRecorder(id, duration, offset);
    }

    private void stopRecording() {
        Bundle bundle = advertisingPacketRecorder.stopRecording();
        if (resultReceiver != null) {
            resultReceiver.send(RECORDING_FINISHED, bundle);
        }
    }

    protected Completable processNotificationAction(int action) {
        return Completable.fromAction(() -> {
            switch (action) {
                case NotificationManager.ACTION_STOP:
                    onDestroy();
                    stopSelf();
                    System.exit(0);
                    break;
                default:
                    Log.w(TAG, "Unknown notification action: " + action);
            }
        });
    }

}
