package com.nexenio.bleindoorpositioningdemo.location;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 21.11.17.
 */

public final class AndroidLocationProvider implements LocationProvider {

    private static final String TAG = AndroidLocationProvider.class.getSimpleName();
    public static final int REQUEST_CODE_LOCATION_PERMISSIONS = 1;
    public static final int REQUEST_CODE_LOCATION_SETTINGS = 2;

    private static AndroidLocationProvider instance;
    private Activity activity;
    private boolean isRequestingLocationUpdates;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    private Location lastKnownLocation;
    private Set<LocationListener> locationListeners = new HashSet<>();

    private AndroidLocationProvider() {

    }

    public static AndroidLocationProvider getInstance() {
        if (instance == null) {
            instance = new AndroidLocationProvider();
        }
        return instance;
    }

    public static void initialize(@NonNull Activity activity) {
        Log.v(TAG, "Initializing with context: " + activity);
        AndroidLocationProvider instance = getInstance();
        instance.activity = activity;
        instance.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        instance.setupLocationService();
    }

    private void setupLocationService() {
        Log.v(TAG, "Setting up location service");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(getLocationRequest());
        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.v(TAG, "Location settings satisfied");
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        Log.w(TAG, "Location settings not satisfied, attempting resolution intent");
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(activity, REQUEST_CODE_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            Log.e(TAG, "Unable to start resolution intent");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.w(TAG, "Location settings not satisfied and can't be changed");
                        break;
                }
            }
        });
    }

    public static boolean registerLocationListener(@NonNull LocationListener locationListener) {
        AndroidLocationProvider instance = getInstance();
        boolean added = instance.locationListeners.add(locationListener);
        if (added && !instance.isRequestingLocationUpdates) {
            startRequestingLocationUpdates();
        }
        return added;
    }

    public static boolean unregisterLocationListener(@NonNull LocationListener locationListener) {
        AndroidLocationProvider instance = getInstance();
        boolean removed = instance.locationListeners.remove(locationListener);
        if (removed && instance.isRequestingLocationUpdates && instance.locationListeners.isEmpty()) {
            stopRequestingLocationUpdates();
        }
        return removed;
    }

    @SuppressLint("MissingPermission")
    public static void startRequestingLocationUpdates() {
        AndroidLocationProvider instance = getInstance();
        if (instance.isRequestingLocationUpdates) {
            return;
        }
        if (!instance.hasLocationPermission()) {
            return;
        }
        Log.d(TAG, "Starting to request location updates");
        if (instance.locationListeners.isEmpty()) {
            Log.w(TAG, "There are no location listeners registered to process location updates");
        }
        instance.fusedLocationClient.requestLocationUpdates(instance.getLocationRequest(), instance.getLocationCallback(), null);
        instance.isRequestingLocationUpdates = true;
    }

    public static void stopRequestingLocationUpdates() {
        AndroidLocationProvider instance = getInstance();
        if (!instance.isRequestingLocationUpdates) {
            return;
        }
        Log.d(TAG, "Stopping to request location updates");
        if (!instance.locationListeners.isEmpty()) {
            Log.w(TAG, "There are still registered location listeners which won't receive new location updates");
        }
        instance.fusedLocationClient.removeLocationUpdates(instance.getLocationCallback());
        instance.isRequestingLocationUpdates = false;
    }

    @SuppressLint("MissingPermission")
    public static void requestLastKnownLocation() {
        final AndroidLocationProvider instance = getInstance();
        if (!instance.hasLocationPermission()) {
            return;
        }
        Log.d(TAG, "Requesting last known location");
        instance.fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getInstance().activity, new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location androidLocation) {
                        if (androidLocation != null) {
                            instance.onLocationUpdateReceived(androidLocation);
                        } else {
                            Log.w(TAG, "Unable to get last known location");
                        }
                    }
                });
    }

    private void onLocationUpdateReceived(@NonNull android.location.Location androidLocation) {
        Location location = convertLocation(androidLocation);
        onLocationUpdateReceived(location);
    }

    private void onLocationUpdateReceived(@NonNull Location location) {
        lastKnownLocation = location;
        Log.v(TAG, "Last known location set to: " + lastKnownLocation);
        for (LocationListener locationListener : locationListeners) {
            locationListener.onLocationUpdated(this, lastKnownLocation);
        }
    }

    private LocationRequest createHighAccuracyLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(3));
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(1));
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private LocationRequest getLocationRequest() {
        if (locationRequest == null) {
            locationRequest = createHighAccuracyLocationRequest();
        }
        return locationRequest;
    }

    private LocationCallback getLocationCallback() {
        if (locationCallback == null) {
            locationCallback = createLocationCallback();
        }
        return locationCallback;
    }

    private LocationCallback createLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.v(TAG, "Received location result with " + locationResult.getLocations().size() + " locations");
                onLocationUpdateReceived(locationResult.getLastLocation());
            }
        };
    }

    public boolean hasLocationPermission() {
        return hasLocationPermission(activity);
    }

    public static boolean hasLocationPermission(@NonNull Context context) {
        boolean fineLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarseLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return fineLocation && coarseLocation;
    }

    public static void requestLocationPermission(@NonNull Activity activity) {
        Log.d(TAG, "Requesting location permission");
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_CODE_LOCATION_PERMISSIONS);
    }

    public static boolean isLocationEnabled(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                int locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                return locationMode != Settings.Secure.LOCATION_MODE_OFF;
            } catch (Settings.SettingNotFoundException e) {
                Log.e(TAG, "Unable to get location mode");
                e.printStackTrace();
                return false;
            }
        } else {
            String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void requestLocationEnabling(@NonNull Activity activity) {
        Log.d(TAG, "Requesting location enabling");
        Intent locationSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivity(locationSettings);
    }

    public static Location convertLocation(android.location.Location androidLocation) {
        Location convertedLocation = new Location();
        convertedLocation.setLatitude(androidLocation.getLatitude());
        convertedLocation.setLongitude(androidLocation.getLongitude());
        convertedLocation.setAltitude(convertedLocation.getAltitude());
        convertedLocation.setElevation(convertedLocation.getElevation());
        return convertedLocation;
    }

    @Override
    public Location getLocation() {
        return lastKnownLocation;
    }

}
