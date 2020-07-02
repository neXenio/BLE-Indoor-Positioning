package com.nexenio.bleindoorpositioningdemo.recording;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.nexenio.bleindoorpositioningdemo.ExternalStorageUtils;
import com.nexenio.bleindoorpositioningdemo.notification.NotificationManager;

import java.io.File;

import androidx.annotation.Nullable;
import io.reactivex.rxjava3.core.Completable;

import static com.nexenio.bleindoorpositioningdemo.recording.OverallRecordingActivity.ACTION_RECORDING_START;
import static com.nexenio.bleindoorpositioningdemo.recording.OverallRecordingActivity.ACTION_RECORDING_STOP;
import static com.nexenio.bleindoorpositioningdemo.recording.OverallRecordingActivity.RECORDING_DIRECTORY_NAME;
import static com.nexenio.bleindoorpositioningdemo.recording.OverallRecordingActivity.RECORDING_FINISHED;

public class RecordingService extends Service implements AdvertisingPacketRecorder.RecordingObserver {

    private static final String TAG = RecordingService.class.getSimpleName();

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
    }

    protected void promoteToForeground() {
        startForeground(NotificationManager.NOTIFICATION_ID_STATUS, createForegroundNotification());
    }

    protected Notification createForegroundNotification() {
        return notificationManager
                .createStatusNotificationBuilder(this.getClass(), OverallRecordingActivity.class, advertisingPacketRecorder.getOffset())
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_RECORDING_START: {
                    createRecorder(intent);
                    promoteToForeground();
                    advertisingPacketRecorder.initializeBluetoothScanning(this);
                    advertisingPacketRecorder.startRecording();
                    break;
                }
                case ACTION_RECORDING_STOP: {
                    advertisingPacketRecorder.stopRecording();
                }
            }
        } else {
            NotificationManager.getBundleFromIntent(intent)
                    .flatMap(NotificationManager::getActionFromBundle)
                    .flatMapCompletable(this::processNotificationAction)
                    .subscribe(
                            () -> Log.d(TAG, "Processed notification action"),
                            throwable -> Log.w(TAG, "Unable to process notification action", throwable)
                    );
        }

        return START_STICKY;
    }

    private void createRecorder(Intent intent) {
        if (advertisingPacketRecorder != null) {
            return;
        }
        resultReceiver = intent.getParcelableExtra("receiverTag");
        String comment = intent.getStringExtra("comment");
        long duration = intent.getLongExtra("duration", 0);
        long offset = intent.getLongExtra("offset", 0);
        advertisingPacketRecorder = new AdvertisingPacketRecorder(comment, duration, offset, this);
    }

    private Completable recordingStopped(Bundle bundle) {
        return Completable.fromAction(() -> {
            if (bundle != null) {
                String fileName = bundle.getString("fileName");
                File documentsDirectory = ExternalStorageUtils.getDocumentsDirectory(RECORDING_DIRECTORY_NAME);
                File file = new File(documentsDirectory, fileName);
                ExternalStorageUtils.shareFile(file, RecordingService.this);
            }
            if (resultReceiver != null) {
                resultReceiver.send(RECORDING_FINISHED, bundle);
            }
        });
    }

    protected Completable processNotificationAction(int action) {
        return Completable.fromAction(() -> {
            switch (action) {
                case NotificationManager.ACTION_STOP:
                    advertisingPacketRecorder.stopRecording();
                    break;
                default:
                    Log.w(TAG, "Unknown notification action: " + action);
            }
        });
    }

    @Override
    public void onRecordingStopped(@Nullable Bundle bundle) {
        recordingStopped(bundle)
                .doOnComplete(this::stopSelf)
                .subscribe();
    }

}
