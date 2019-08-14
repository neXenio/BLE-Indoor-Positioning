package com.nexenio.bleindoorpositioningdemo.ui.beaconview.radar;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexenio.bleindoorpositioning.IndoorPositioning;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconViewFragment;

public class BeaconRadarFragment extends BeaconViewFragment {

    private BeaconRadar beaconRadar;

    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    public BeaconRadarFragment() {
        super();
        beaconFilters.add(uuidFilter);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch (sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER: {
                        System.arraycopy(sensorEvent.values, 0, accelerometerReading, 0, accelerometerReading.length);
                        break;
                    }
                    case Sensor.TYPE_MAGNETIC_FIELD: {
                        System.arraycopy(sensorEvent.values, 0, magnetometerReading, 0, magnetometerReading.length);
                        break;
                    }
                }
                updateOrientationAngles();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDetach() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onDetach();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_beacon_radar;
    }

    @Override
    protected LocationListener createDeviceLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                if (locationProvider == IndoorPositioning.getInstance()) {
                    beaconRadar.setDeviceLocation(location);
                    beaconRadar.fitToCurrentLocations();
                }
            }
        };
    }

    @Override
    protected BeaconUpdateListener createBeaconUpdateListener() {
        return new BeaconUpdateListener() {
            @Override
            public void onBeaconUpdated(Beacon beacon) {
                beaconRadar.setBeacons(getBeacons());
            }
        };
    }

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = super.onCreateView(inflater, container, savedInstanceState);
        beaconRadar = inflatedView.findViewById(R.id.beaconRadar);
        beaconRadar.setBeacons(getBeacons());
        return inflatedView;
    }

    private void updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        beaconRadar.startDeviceAngleAnimation((float) Math.toDegrees(orientationAngles[0]));
    }

}
