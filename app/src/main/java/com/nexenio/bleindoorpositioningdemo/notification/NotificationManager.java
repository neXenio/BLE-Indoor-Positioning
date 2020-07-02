package com.nexenio.bleindoorpositioningdemo.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.nexenio.bleindoorpositioningdemo.R;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationManager {

    private boolean isInitializing;
    private boolean isInitialized;
    public static final int NOTIFICATION_ID_STATUS = 1;
    private static final String NOTIFICATION_CHANNEL_ID_PREFIX = "channel_";
    private static final String NOTIFICATION_CHANNEL_ID_STATUS = NOTIFICATION_CHANNEL_ID_PREFIX + NOTIFICATION_ID_STATUS;

    private static final String BUNDLE_KEY = "notification_bundle";
    private static final String ACTION_KEY = "notification_action";
    public static final int ACTION_STOP = 1;

    private Context context;
    private android.app.NotificationManager notificationManager;

    public Completable initialize(@NonNull Context context) {
        return Completable.defer(() -> {
            Completable initializationCompletable;
            synchronized (this) {
                if (isInitialized) {
                    // Timber.v("Not initializing %s, initialization already completed", this);
                    initializationCompletable = Completable.complete();
                } else if (isInitializing) {
                    // Timber.v("Deferring initialization of %s, initialization already in progress", this);
                    initializationCompletable = initialize(context)
                            .delaySubscription(10, TimeUnit.MILLISECONDS);
                } else {
                    isInitializing = true;
                    this.context = context;
                    initializationCompletable = doInitialize(context)
                            // .doOnSubscribe(disposable -> Timber.d("Initializing %s", this))
                            .doOnComplete(() -> isInitialized = true)
                            .doFinally(() -> isInitializing = false);
                }
            }
            return initializationCompletable;
        });
    }

    private Completable doInitialize(@NonNull Context context) {
        return Completable.fromAction(() -> {
            this.context = context;
            this.notificationManager = (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannels();
            }
        });
    }

    /**
     * Creates all notification channels that might be used by the app, if targeting Android O or
     * later.
     *
     * @see <a href="https://developer.android.com/preview/features/notification-channels.html">Notification
     *         Channels</a>
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannels() {
        createStatusNotificationChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createStatusNotificationChannel() {
        CharSequence channelName = context.getString(R.string.notification_service_title);
        String channelDescription = context.getString(R.string.notification_service_description);
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_STATUS, channelName, android.app.NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(channelDescription);
        channel.enableLights(false);
        channel.enableVibration(false);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Creates the default status notification builder, intended to serve the ongoing foreground
     * service notification.
     */
    public NotificationCompat.Builder createStatusNotificationBuilder(Class serviceClass, Class activityClass) {
        return createStatusNotificationBuilder(serviceClass, activityClass, 0);
    }

    /**
     * Creates the default status notification builder, intended to serve the ongoing foreground
     * service notification.
     */
    public NotificationCompat.Builder createStatusNotificationBuilder(Class serviceClass, Class activityClass, long offset) {
        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_STATUS)
                .setContentTitle(context.getText(R.string.notification_service_title))
                .setContentText(context.getText(R.string.notification_service_description))
                .setSmallIcon(R.drawable.bluetooth)
                .setContentIntent(createActivityIntent(activityClass))
                .addAction(createStopServiceAction(serviceClass))
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setCategory(Notification.CATEGORY_STATUS)
                .setUsesChronometer(true)
                .setWhen(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(offset));
    }

    /**
     * Creates a pending intent that will start the specified activity when invoked.
     */
    public PendingIntent createActivityIntent(Class intentClass) {
        Intent contentIntent = new Intent(context, intentClass);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(context, 0, contentIntent, 0);
    }

    /**
     * Creates a pending intent that will start the specified service when invoked.
     */
    public PendingIntent createServiceIntent(Class intentClass, @Nullable Bundle notificationBundle) {
        Intent contentIntent = new Intent(context, intentClass);
        if (notificationBundle != null) {
            contentIntent.putExtra(BUNDLE_KEY, notificationBundle);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return PendingIntent.getForegroundService(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            return PendingIntent.getService(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    /**
     * Creates a pending intent that will stop the specified service when invoked.
     */
    public PendingIntent createStopServiceIntent(Class intentClass) {
        Bundle bundle = new Bundle();
        bundle.putInt(ACTION_KEY, ACTION_STOP);
        return createServiceIntent(intentClass, bundle);
    }

    /**
     * Creates an action that will invoke the intent created by {@link
     * #createStopServiceIntent(Class)}.
     */
    private NotificationCompat.Action createStopServiceAction(Class intentClass) {
        return new NotificationCompat.Action.Builder(
                R.drawable.signal,
                context.getString(R.string.action_stop),
                createStopServiceIntent(intentClass)
        ).build();
    }

    /**
     * Attempts to retrieve the bundle that has been added when using {@link
     * #createServiceIntent(Class, Bundle)}.
     */
    public static Maybe<Bundle> getBundleFromIntent(@Nullable Intent intent) {
        return Maybe.defer(() -> {
            if (intent != null && intent.getExtras() != null) {
                Bundle extras = intent.getExtras();
                if (extras.containsKey(BUNDLE_KEY)) {
                    return Maybe.fromCallable(() -> extras.getBundle(BUNDLE_KEY));
                }
            }
            return Maybe.empty();
        });
    }

    public static Maybe<Integer> getActionFromBundle(@Nullable Bundle bundle) {
        return Maybe.defer(() -> {
            if (bundle != null && bundle.containsKey(ACTION_KEY)) {
                return Maybe.fromCallable(() -> bundle.getInt(ACTION_KEY));
            }
            return Maybe.empty();
        });
    }

}
