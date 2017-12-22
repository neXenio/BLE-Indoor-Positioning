package com.nexenio.bleindoorpositioningdemo.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.BeaconFilter;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.scan.ScanResult;
import com.polidea.rxandroidble.scan.ScanSettings;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;

/**
 * Created by steppschuh on 24.11.17.
 */

public class BluetoothClient {

    private static final String TAG = BluetoothClient.class.getSimpleName();
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 10;

    private static BluetoothClient instance;

    private Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BeaconManager beaconManager = BeaconManager.getInstance();

    private RxBleClient rxBleClient;
    private Subscription scanningSubscription;

    private BluetoothClient() {

    }

    public static BluetoothClient getInstance() {
        if (instance == null) {
            instance = new BluetoothClient();
        }
        return instance;
    }

    public static void initialize(@NonNull Context context) {
        Log.v(TAG, "Initializing with context: " + context);
        BluetoothClient instance = getInstance();
        instance.rxBleClient = RxBleClient.create(context);
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        instance.bluetoothAdapter = bluetoothManager.getAdapter();
        if (instance.bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth adapter is not available");
        }
    }

    public static void startScanning() {
        if (isScanning()) {
            return;
        }

        final BluetoothClient instance = getInstance();
        Log.d(TAG, "Starting to scan for beacons");

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build();

        instance.scanningSubscription = instance.rxBleClient.scanBleDevices(scanSettings)
                .subscribe(new Observer<ScanResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Bluetooth scanning error: " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ScanResult scanResult) {
                        instance.processScanResult(scanResult);
                    }
                });
    }

    public static void stopScanning() {
        if (!isScanning()) {
            return;
        }

        BluetoothClient instance = getInstance();
        Log.d(TAG, "Stopping to scan for beacons");
        instance.scanningSubscription.unsubscribe();
    }

    public static boolean isScanning() {
        Subscription subscription = getInstance().scanningSubscription;
        return subscription != null && !subscription.isUnsubscribed();
    }

    public static boolean isBluetoothEnabled() {
        BluetoothClient instance = getInstance();
        return instance.bluetoothAdapter != null && instance.bluetoothAdapter.isEnabled();
    }

    public static void requestBluetoothEnabling(@NonNull Activity activity) {
        Log.d(TAG, "Requesting bluetooth enabling");
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
    }

    private void processScanResult(@NonNull ScanResult scanResult) {
        String macAddress = scanResult.getBleDevice().getMacAddress();

        byte[] data = scanResult.getScanRecord().getBytes();
        AdvertisingPacket advertisingPacket = AdvertisingPacket.from(data);

        if (advertisingPacket != null) {
            String beaconKey = BeaconManager.getBeaconKey(macAddress, advertisingPacket);
            advertisingPacket.setRssi(scanResult.getRssi());

            Beacon beacon = beaconManager.getBeaconMap().get(beaconKey);
            AdvertisingPacket lastAdvertisingPacket = beacon == null ? null : beacon.getLatestAdvertisingPacket();

            boolean isNewBeacon = beacon == null;
            boolean isNewAdvertisingData = lastAdvertisingPacket == null || !advertisingPacket.dataEquals(lastAdvertisingPacket);

            beaconManager.processAdvertisingPacket(macAddress, advertisingPacket);

            if (isNewBeacon) {
                beacon = beaconManager.getBeaconMap().get(beaconKey);
                if (beacon instanceof IBeacon) {
                    beacon.setLocationProvider(createDebuggingLocationProvider((IBeacon) beacon));
                }
                Log.d(TAG, macAddress + " data received for the first time: " + advertisingPacket);
            } else if (isNewAdvertisingData) {
                //Log.v(TAG, macAddress + " data changed to: " + advertisingPacket);
            } else {
                //Log.v(TAG, macAddress + " data unchanged: " + advertisingPacket);
            }

        }

    }

    private static LocationProvider createDebuggingLocationProvider(IBeacon iBeacon) {
        final Location location = new Location();
        switch (iBeacon.getMinor()) {
            case 1: {
                location.setLatitude(52.512437);
                location.setLongitude(13.391124);
                break;
            }
            case 2: {
                location.setLatitude(52.512427);
                location.setLongitude(13.390934);
                break;
            }
            case 3: {
                location.setLatitude(52.512424);
                location.setLongitude(13.390829);
                break;
            }
            case 4: {
                location.setLatitude(52.512426);
                location.setLongitude(13.390887);
                break;
            }
        }
        return new LocationProvider() {
            @Override
            public Location getLocation() {
                return location;
            }
        };
    }

}
