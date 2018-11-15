package com.nexenio.bleindoorpositioningdemo;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.nexenio.bleindoorpositioning.testutil.benchmark.BeaconInfo;
import com.nexenio.bleindoorpositioning.testutil.benchmark.DeviceInfo;
import com.nexenio.bleindoorpositioning.testutil.benchmark.RssiMeasurements;

import androidx.appcompat.app.AppCompatActivity;

public class RecordingActivity extends AppCompatActivity {

    private MaterialButton recordButton;

    // general
    private TextInputEditText distanceEditText;
    private TextInputEditText notesEditText;

    // device info
    private TextInputEditText deviceIdEditText;
    private TextInputEditText deviceModelEditText;
    private TextInputEditText deviceManufacturerEditText;
    private TextInputEditText deviceOsVersionEditText;

    // beacon info
    private TextInputEditText beaconNameEditText;
    private TextInputEditText beaconModelEditText;
    private TextInputEditText beaconManufacturerEditText;
    private TextInputEditText beaconTransmissionPowerEditText;
    private TextInputEditText beaconAdvertisingFrequencyEditText;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        initializeLayout();
        restoreFormValues();
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

    public void onRecordButtonClicked(View view) {
        persistFormValues();
    }

    private void persistFormValues() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(RssiMeasurements.KEY_DISTANCE, distanceEditText.getText().toString());
        editor.putString(RssiMeasurements.KEY_NOTES, notesEditText.getText().toString());

        editor.putString(DeviceInfo.KEY_DEVICE_ID, deviceIdEditText.getText().toString());
        editor.putString(DeviceInfo.KEY_DEVICE_MODEL, deviceModelEditText.getText().toString());
        editor.putString(DeviceInfo.KEY_DEVICE_MANUFACTURER, deviceManufacturerEditText.getText().toString());
        editor.putString(DeviceInfo.KEY_DEVICE_OS_VERSION, deviceOsVersionEditText.getText().toString());

        editor.putString(BeaconInfo.KEY_BEACON_NAME, beaconNameEditText.getText().toString());
        editor.putString(BeaconInfo.KEY_BEACON_MODEL, beaconModelEditText.getText().toString());
        editor.putString(BeaconInfo.KEY_BEACON_MANUFACTURER, beaconManufacturerEditText.getText().toString());
        editor.putString(BeaconInfo.KEY_BEACON_TRANSMISSION_POWER, beaconTransmissionPowerEditText.getText().toString());
        editor.putString(BeaconInfo.KEY_BEACON_ADVERTISING_FREQUENCY, beaconAdvertisingFrequencyEditText.getText().toString());

        editor.apply();
    }

    private void restoreFormValues() {
        distanceEditText.setText(sharedPreferences.getString(RssiMeasurements.KEY_DISTANCE, "1"));
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

    @SuppressLint("HardwareIds")
    private static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "android_id");
    }

    private static String getOsVersion() {
        return "Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")";
    }

}
