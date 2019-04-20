package com.nexenio.bleindoorpositioningdemo;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.FilteredBeaconUpdateListener;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.IBeaconFilter;
import com.nexenio.bleindoorpositioning.testutil.benchmark.BeaconInfo;
import com.nexenio.bleindoorpositioning.testutil.benchmark.DeviceInfo;
import com.nexenio.bleindoorpositioning.testutil.benchmark.RssiMeasurements;
import com.nexenio.bleindoorpositioningdemo.bluetooth.BluetoothClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class RecordingActivity extends AppCompatActivity {

    private static final String TAG = RecordingActivity.class.getSimpleName();

    private final static UUID RECORDING_UUID = UUID.fromString("61a0523a-a733-4789-ae8f-4f55fcff64f2");

    private RssiMeasurements rssiMeasurements = new RssiMeasurements();
    private List<Integer> recordedRssiValues;
    private FilteredBeaconUpdateListener<IBeacon<IBeaconAdvertisingPacket>> recordingBeaconUpdateListener;

    private SharedPreferences sharedPreferences;

    private TextInputEditText distanceEditText;
    private TextInputEditText notesEditText;

    private TextInputEditText deviceIdEditText;
    private TextInputEditText deviceModelEditText;
    private TextInputEditText deviceManufacturerEditText;
    private TextInputEditText deviceOsVersionEditText;

    private TextInputEditText beaconNameEditText;
    private TextInputEditText beaconModelEditText;
    private TextInputEditText beaconManufacturerEditText;
    private TextInputEditText beaconTransmissionPowerEditText;
    private TextInputEditText beaconAdvertisingFrequencyEditText;

    private MaterialButton recordButton;

    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        initializeLayout();
        restoreFormValues();
        initializeBluetoothScanning();
    }

    private void initializeLayout() {
        recordButton = findViewById(R.id.recordButton);

        distanceEditText = findViewById(R.id.recordDistanceEditText);
        notesEditText = findViewById(R.id.recordNotesEditText);

        deviceIdEditText = findViewById(R.id.recordDeviceIdEditText);
        deviceModelEditText = findViewById(R.id.recordDeviceModelEditText);
        deviceManufacturerEditText = findViewById(R.id.recordDeviceManufacturerEditText);
        deviceOsVersionEditText = findViewById(R.id.recordDeviceOsVersionEditText);

        beaconNameEditText = findViewById(R.id.recordBeaconNameEditText);
        beaconModelEditText = findViewById(R.id.recordBeaconModelEditText);
        beaconManufacturerEditText = findViewById(R.id.recordBeaconManufacturerEditText);
        beaconTransmissionPowerEditText = findViewById(R.id.recordBeaconTransmissionPowerEditText);
        beaconAdvertisingFrequencyEditText = findViewById(R.id.recordBeaconAdvertisingFrequencyEditText);
    }

    private void initializeBluetoothScanning() {
        Log.d(TAG, "Initializing Bluetooth scanning");

        BluetoothClient.initialize(this);
        IBeaconFilter<IBeacon<IBeaconAdvertisingPacket>> recordingBeaconFilter = new IBeaconFilter<>(RECORDING_UUID);
        recordingBeaconUpdateListener = new FilteredBeaconUpdateListener<IBeacon<IBeaconAdvertisingPacket>>(recordingBeaconFilter) {
            @Override
            public void onMatchingBeaconUpdated(IBeacon<IBeaconAdvertisingPacket> beacon) {
                onRecordingBeaconUpdated(beacon);
            }
        };
    }

    @Override
    protected void onStop() {
        stopRecording();
        super.onStop();
    }

    public void onRecordButtonClicked(View view) {
        persistFormValues();
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private RssiMeasurements createRssiMeasurements() {
        RssiMeasurements rssiMeasurements = new RssiMeasurements();
        rssiMeasurements.setDeviceInfo(createDeviceInfo());
        rssiMeasurements.setBeaconInfo(createBeaconInfo());
        rssiMeasurements.setDistance(Float.parseFloat(distanceEditText.getText().toString()));
        rssiMeasurements.setNotes(notesEditText.getText().toString());
        rssiMeasurements.setStartTimestamp(System.currentTimeMillis());
        return rssiMeasurements;
    }

    private DeviceInfo createDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setId(deviceIdEditText.getText().toString());
        deviceInfo.setModel(deviceModelEditText.getText().toString());
        deviceInfo.setManufacturer(deviceManufacturerEditText.getText().toString());
        deviceInfo.setOsVersion(deviceOsVersionEditText.getText().toString());
        return deviceInfo;
    }

    private BeaconInfo createBeaconInfo() {
        BeaconInfo beaconInfo = new BeaconInfo();
        beaconInfo.setName(beaconNameEditText.getText().toString());
        beaconInfo.setModel(beaconModelEditText.getText().toString());
        beaconInfo.setManufacturer(beaconManufacturerEditText.getText().toString());
        beaconInfo.setTransmissionPower(Integer.valueOf(beaconTransmissionPowerEditText.getText().toString()));
        beaconInfo.setAdvertisingFrequency(Integer.valueOf(beaconAdvertisingFrequencyEditText.getText().toString()));
        return beaconInfo;
    }

    private void startRecording() {
        Log.d(TAG, "Starting recording");

        rssiMeasurements = createRssiMeasurements();
        recordedRssiValues = new ArrayList<>();

        BluetoothClient.startScanning();
        BeaconManager.registerBeaconUpdateListener(recordingBeaconUpdateListener);

        isRecording = true;
        recordButton.setText(getString(R.string.action_stop_recording));
    }

    private void stopRecording() {
        Log.d(TAG, "Stopping recording");
        isRecording = false;
        recordButton.setText(getString(R.string.action_start_recording));

        BluetoothClient.stopScanning();
        BeaconManager.unregisterBeaconUpdateListener(recordingBeaconUpdateListener);

        rssiMeasurements.setEndTimestamp(System.currentTimeMillis());
        rssiMeasurements.setRssis(convertIntArray(recordedRssiValues));
        recordedRssiValues.clear();

        Log.i(TAG, "RSSI measurements:\n" + rssiMeasurements);

        // TODO: persist JSON file

        // TODO: start share intent for persisted JSON file
    }

    private void onRecordingBeaconUpdated(IBeacon<IBeaconAdvertisingPacket> beacon) {
        IBeaconAdvertisingPacket latestAdvertisingPacket = beacon.getLatestAdvertisingPacket();
        recordedRssiValues.add(latestAdvertisingPacket.getRssi());
    }

    private void persistFormValues() {
        Log.d(TAG, "Persisting form values");

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(RssiMeasurements.KEY_DISTANCE, distanceEditText.getText().toString());
        editor.putString(RssiMeasurements.KEY_NOTES, notesEditText.getText().toString());

        // don't persist dynamic fields
        //editor.putString(DeviceInfo.KEY_DEVICE_ID, deviceIdEditText.getText().toString());
        //editor.putString(DeviceInfo.KEY_DEVICE_MODEL, deviceModelEditText.getText().toString());
        //editor.putString(DeviceInfo.KEY_DEVICE_MANUFACTURER, deviceManufacturerEditText.getText().toString());
        //editor.putString(DeviceInfo.KEY_DEVICE_OS_VERSION, deviceOsVersionEditText.getText().toString());

        editor.putString(BeaconInfo.KEY_BEACON_NAME, beaconNameEditText.getText().toString());
        editor.putString(BeaconInfo.KEY_BEACON_MODEL, beaconModelEditText.getText().toString());
        editor.putString(BeaconInfo.KEY_BEACON_MANUFACTURER, beaconManufacturerEditText.getText().toString());
        editor.putString(BeaconInfo.KEY_BEACON_TRANSMISSION_POWER, beaconTransmissionPowerEditText.getText().toString());
        editor.putString(BeaconInfo.KEY_BEACON_ADVERTISING_FREQUENCY, beaconAdvertisingFrequencyEditText.getText().toString());

        editor.apply();
    }

    private void restoreFormValues() {
        Log.d(TAG, "Restoring form values");

        distanceEditText.setText(sharedPreferences.getString(RssiMeasurements.KEY_DISTANCE, ""));
        notesEditText.setText(sharedPreferences.getString(RssiMeasurements.KEY_NOTES, ""));

        deviceIdEditText.setText(sharedPreferences.getString(DeviceInfo.KEY_DEVICE_ID, getDeviceId(this)));
        deviceModelEditText.setText(sharedPreferences.getString(DeviceInfo.KEY_DEVICE_MODEL, Build.MODEL));
        deviceManufacturerEditText.setText(sharedPreferences.getString(DeviceInfo.KEY_DEVICE_MANUFACTURER, Build.MANUFACTURER));
        deviceOsVersionEditText.setText(sharedPreferences.getString(DeviceInfo.KEY_DEVICE_OS_VERSION, getOsVersion()));

        beaconNameEditText.setText(sharedPreferences.getString(BeaconInfo.KEY_BEACON_NAME, ""));
        beaconModelEditText.setText(sharedPreferences.getString(BeaconInfo.KEY_BEACON_MODEL, ""));
        beaconManufacturerEditText.setText(sharedPreferences.getString(BeaconInfo.KEY_BEACON_MANUFACTURER, ""));
        beaconTransmissionPowerEditText.setText(sharedPreferences.getString(BeaconInfo.KEY_BEACON_TRANSMISSION_POWER, ""));
        beaconAdvertisingFrequencyEditText.setText(sharedPreferences.getString(BeaconInfo.KEY_BEACON_ADVERTISING_FREQUENCY, ""));
    }

    private static int[] convertIntArray(List<Integer> integerList) {
        int[] intArray = new int[integerList.size()];
        Iterator<Integer> iterator = integerList.iterator();
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = iterator.next();
        }
        return intArray;
    }

    @SuppressLint("HardwareIds")
    private static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "android_id");
    }

    private static String getOsVersion() {
        return "Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")";
    }

}
