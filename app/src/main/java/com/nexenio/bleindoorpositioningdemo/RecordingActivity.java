package com.nexenio.bleindoorpositioningdemo;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RecordingActivity extends AppCompatActivity {

    private static final String TAG = RecordingActivity.class.getSimpleName();
    private static final String RECORDING_DIRECTORY_NAME = "Rssi_Recordings";
    public static final int REQUEST_CODE_STORAGE_PERMISSIONS = 1;

    private final static UUID RECORDING_UUID = UUID.fromString("61a0523a-a733-4789-ae8f-4f55fcff64f2");

    private RssiMeasurements rssiMeasurements = new RssiMeasurements();

    @Nullable
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

    @Nullable
    protected Snackbar errorSnackBar;
    protected CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        initializeLayout();
        restoreFormValues();
        initializeBluetoothScanning();

        if (!hasStoragePermission()) {
            requestStoragePermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_STORAGE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Storage permission granted");
                } else {
                    showStoragePermissionMissingError();
                }
                break;
            }
        }
    }

    private void initializeLayout() {
        coordinatorLayout = getWindow().getDecorView().findViewById(R.id.coordinatorLayout);

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

        // Limit numbers to avoid overflow
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(6);

        distanceEditText.setFilters(FilterArray);
        beaconTransmissionPowerEditText.setFilters(FilterArray);
        beaconAdvertisingFrequencyEditText.setFilters(FilterArray);
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
        try {
            rssiMeasurements = createRssiMeasurements();
        } catch (IllegalArgumentException | NullPointerException e) {
            if (!showInvalidFields()) {
                throw e;
            }
            return;
        }

        Log.d(TAG, "Starting recording");
        recordedRssiValues = new ArrayList<>();

        BluetoothClient.startScanning();
        BeaconManager.registerBeaconUpdateListener(recordingBeaconUpdateListener);

        isRecording = true;
        recordButton.setText(getString(R.string.action_stop_recording));
    }

    private void stopRecording() {
        if (!isRecording) {
            return;
        }
        Log.d(TAG, "Stopping recording");
        isRecording = false;
        recordButton.setText(getString(R.string.action_start_recording));

        BluetoothClient.stopScanning();
        BeaconManager.unregisterBeaconUpdateListener(recordingBeaconUpdateListener);

        rssiMeasurements.setEndTimestamp(System.currentTimeMillis());
        rssiMeasurements.setRssis(convertIntArray(recordedRssiValues));

        if (recordedRssiValues != null) {
            recordedRssiValues.clear();
        }

        Log.i(TAG, "RSSI measurements:\n" + rssiMeasurements);
        exportMeasurements();
    }

    /**
     * Indicate text edit fields with missing text.
     *
     * @return False if no invalid field was found, else true
     */
    private boolean showInvalidFields() {
        Toast.makeText(this, "Not all information were provided", Toast.LENGTH_LONG).show();
        List<TextInputEditText> textInputEditTexts = getTextInputEditTexts();
        boolean invalidTextField = false;
        for (TextInputEditText textInputEditText : textInputEditTexts) {
            if (textInputEditText.getText() != null && textInputEditText.getText().toString().equals("")) {
                String errorString = "This field cannot be empty";
                textInputEditText.setError(errorString);
                invalidTextField = true;
            }
        }
        return invalidTextField;
    }

    private List<TextInputEditText> getTextInputEditTexts() {
        return Arrays.asList(distanceEditText,
                notesEditText,
                deviceIdEditText,
                deviceModelEditText,
                deviceManufacturerEditText,
                deviceOsVersionEditText,
                beaconNameEditText,
                beaconModelEditText,
                beaconManufacturerEditText,
                beaconTransmissionPowerEditText,
                beaconAdvertisingFrequencyEditText);
    }

    private void exportMeasurements() {
        String jsonString = createJsonString(rssiMeasurements);
        String fileName = "rssiMeasurements_" + rssiMeasurements.getStartTimestamp() + "_" + rssiMeasurements.getEndTimestamp() + ".json";
        File file = persistJsonFile(fileName, jsonString);

        ExternalStorageUtils.shareFile(file, this);
    }

    private static String createJsonString(RssiMeasurements rssiMeasurements) {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setPrettyPrinting();

        Gson gson = gsonBuilder.create();
        return gson.toJson(rssiMeasurements);
    }

    private File persistJsonFile(String fileName, String jsonString) {
        File documentsDirectory = ExternalStorageUtils.getDocumentsDirectory(RECORDING_DIRECTORY_NAME);
        File file = new File(documentsDirectory, fileName);

        System.out.println(jsonString);

        try {
            if (!documentsDirectory.exists()) {
                System.out.println("Creating directory");
                boolean mkdirs = documentsDirectory.mkdirs();
                System.out.println("Directory created: " + mkdirs);
            }
            System.out.println("Writing file");
            ExternalStorageUtils.writeStringToFile(jsonString, file, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private boolean hasStoragePermission() {
        int permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("CheckResult")
    private void requestStoragePermission() {
        Log.d(TAG, "Requesting storage permission");
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_CODE_STORAGE_PERMISSIONS);
    }

    private void showStoragePermissionMissingError() {
        // find a container for the snackbar
        if (errorSnackBar != null) {
            errorSnackBar.dismiss();
            errorSnackBar = null;
        }

        View container = coordinatorLayout;
        if (container == null) {
            container = getWindow().getDecorView();
        }
        // create the snackbar
        errorSnackBar = Snackbar.make(container, "Permission not granted", Snackbar.LENGTH_LONG);

        errorSnackBar.setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setActionTextColor(ContextCompat.getColor(this, R.color.primary))
                .setAction(getString(R.string.record_retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestStoragePermission();
                    }
                }).show();
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

    private static int[] convertIntArray(@Nullable List<Integer> integerList) {
        if (integerList == null) {
            Log.w(TAG, "Can't convert null to int array.");
            return new int[0];
        }

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
