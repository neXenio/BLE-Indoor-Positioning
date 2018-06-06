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
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.provider.IBeaconLocationProvider;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.scan.ScanResult;
import com.polidea.rxandroidble.scan.ScanSettings;

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
        instance.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        instance.bluetoothAdapter = instance.bluetoothManager.getAdapter();
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
                        Log.e(TAG, "Bluetooth scanning error", e);
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
        AdvertisingPacket advertisingPacket = BeaconManager.processAdvertisingData(macAddress, data, scanResult.getRssi());

        if (advertisingPacket != null) {
            Beacon beacon = BeaconManager.getBeacon(macAddress, advertisingPacket.getBeaconClass());
            if (beacon instanceof IBeacon && !beacon.hasLocation()) {
                beacon.setLocationProvider(createDebuggingLocationProvider((IBeacon) beacon));
            }
        }
    }

    private static IBeaconLocationProvider<IBeacon> createDebuggingLocationProvider(IBeacon iBeacon) {
        final Location beaconLocation = new Location();
        switch (iBeacon.getMinor()) {
            case 1: {
                beaconLocation.setLatitude(52.512437);
                beaconLocation.setLongitude(13.391124);
                beaconLocation.setAltitude(36);
                break;
            }
            case 2: {
                beaconLocation.setLatitude(52.512411788476356);
                beaconLocation.setLongitude(13.390875654442985);
                beaconLocation.setElevation(2.65);
                beaconLocation.setAltitude(36);
                break;
            }
            case 3: {
                beaconLocation.setLatitude(52.51240486636751);
                beaconLocation.setLongitude(13.390770270005437);
                beaconLocation.setElevation(2.65);
                beaconLocation.setAltitude(36);
                break;
            }
            case 4: {
                beaconLocation.setLatitude(52.512426);
                beaconLocation.setLongitude(13.390887);
                beaconLocation.setElevation(2);
                beaconLocation.setAltitude(36);
                break;
            }
            case 5: {
                beaconLocation.setLatitude(52.512347534813834);
                beaconLocation.setLongitude(13.390780437281524);
                beaconLocation.setElevation(2.9);
                beaconLocation.setAltitude(36);
                break;
            }
            case 12: {
                beaconLocation.setLatitude(52.51239708899507);
                beaconLocation.setLongitude(13.390878261276518);
                beaconLocation.setElevation(2.65);
                beaconLocation.setAltitude(36);
                break;
            }
            case 13: {
                beaconLocation.setLatitude(52.51242692608082);
                beaconLocation.setLongitude(13.390872969910035);
                beaconLocation.setElevation(2.65);
                beaconLocation.setAltitude(36);
                break;
            }
            case 14: {
                beaconLocation.setLatitude(52.51240825552749);
                beaconLocation.setLongitude(13.390821867681456);
                beaconLocation.setElevation(2.65);
                beaconLocation.setAltitude(36);
                break;
            }
            case 15: {
                beaconLocation.setLatitude(52.51240194910502);
                beaconLocation.setLongitude(13.390725856632926);
                beaconLocation.setElevation(2.65);
                beaconLocation.setAltitude(36);
                break;
            }
            case 16: {
                beaconLocation.setLatitude(52.512390301005595);
                beaconLocation.setLongitude(13.39077285305359);
                beaconLocation.setElevation(2.65);
                beaconLocation.setAltitude(36);
                break;
            }
            case 17: {
                beaconLocation.setLatitude(52.51241817994876);
                beaconLocation.setLongitude(13.390767908948872);
                beaconLocation.setElevation(2.65);
                beaconLocation.setAltitude(36);
                break;
            }
            case 18: {
                beaconLocation.setLatitude(52.51241494408066);
                beaconLocation.setLongitude(13.390923696709294);
                beaconLocation.setElevation(2.65);
                beaconLocation.setAltitude(36);
                break;
            }
        }
        return new IBeaconLocationProvider<IBeacon>(iBeacon) {
            @Override
            public void updateLocation() {
                this.location = beaconLocation;
            }
        };
    }

}
