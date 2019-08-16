[![Travis](https://img.shields.io/travis/neXenio/BLE-Indoor-Positioning/master.svg)](https://travis-ci.org/neXenio/BLE-Indoor-Positioning/builds) [![GitHub release](https://img.shields.io/github/release/neXenio/BLE-Indoor-Positioning.svg)](https://github.com/neXenio/BLE-Indoor-Positioning/releases) [![JitPack](https://img.shields.io/jitpack/v/neXenio/BLE-Indoor-Positioning.svg)](https://jitpack.io/#neXenio/BLE-Indoor-Positioning/) [![Codecov](https://img.shields.io/codecov/c/github/nexenio/BLE-Indoor-Positioning.svg)](https://codecov.io/gh/neXenio/BLE-Indoor-Positioning) [![license](https://img.shields.io/github/license/neXenio/BLE-Indoor-Positioning.svg)](https://github.com/neXenio/BLE-Indoor-Positioning/blob/master/LICENSE)

# BLE Indoor Positioning

This repo contains a [Java library][package_core] that is capable of estimating locations based on advertising packets received from Bluetooth beacons. It also contains an [Android app][package_app] that uses this library to visualize beacon and location data.

![Demo App Screen Recording](https://raw.githubusercontent.com/wiki/neXenio/BLE-Indoor-Positioning/images/demo-app/demo_app.gif)

# Usage

## Integration

#### Gradle

Release artefacts are available through [Bintray][bintray].

```groovy
dependencies {
    compile 'com.nexenio.bleindoorpositioning:core:0.4.0'
}
```

If you want to use branch snapshots or specific commits, use [JitPack][jitpack].

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.neXenio:BLE-Indoor-Positioning:dev-SNAPSHOT'
}
```

#### Maven
```xml
<dependency>
  <groupId>com.nexenio.bleindoorpositioning</groupId>
  <artifactId>core</artifactId>
  <version>0.4.0</version>
</dependency>
```

#### JAR
You can download the latest `.jar` files from [GitHub][releases] or [Bintray][bintray].

## Bluetooth Scanning

You need to implement some sort of Bluetooth scanning in order to get the advertising data from nearby beacons. On Android, you can use the [BluetoothAdapter][androidbluetoothle] or libraries like [RxAndroidBle][rxandroidble] that wrap around the system APIs.

You'll get a scan result, which you can extract the beacon mac address and raw advertising data from. Forward that data to the `BeaconManager` singleton and it will take care of everything else.

```Java
private void processScanResult(ScanResult scanResult) {
    String macAddress = scanResult.getBleDevice().getMacAddress();
    byte[] advertisingData = scanResult.getScanRecord().getBytes();
    int rssi = scanResult.getRssi();
    BeaconManager.processAdvertisingData(macAddress, advertisingData, rssi);
}
```

The `BeaconManager` will create `Beacon` instances for you and hold them in memory. Each `Beacon` will hold a list of recent `AdvertisingPackets`. There are quite a few convenience methods for Eddystone and iBeacon available, too.

You can listen for beacon changes by registering a `BeaconUpdateListener`:

```Java
BeaconManager.registerBeaconUpdateListener(new BeaconUpdateListener() {
    @Override
    public void onBeaconUpdated(Beacon beacon) {
        // have fun with your beacon!
    }
});
```

For some more fine-tuned callbacks, you may want to use a `FilteredBeaconUpdateListener`, which will only emit updates when beacons match a `BeaconFilter` of your choice.

## Distance Estimation

Based on the received `AdvertisingPackets` (which also keep track of the [RSSIs][rssi]), you can get an estimated distance (in meters) to each Beacon. Simply call `beacon.getDistance()` or directly use the `BeaconDistanceCalculator`, which operates using the [Log-distance path loss model][log_distance_path_loss_model].

## Location Estimation

Based on the estimated distances to nearby beacons, the `IndoorPositioning` singleton can calculate the current geo coordinates of the device that received the advertising data. It utilizes the `Multilateration` class for that, which solves  a formulation of n-D space trilateration problem with a nonlinear least squares optimizer (using the [Levenberg–Marquardt algorithm][levenberg_marquardt_algorithm]).

You can listen for location changes by registering a `LocationListener`:

```Java
IndoorPositioning.registerLocationListener(new LocationListener() {
    @Override
    public void onLocationUpdated(LocationProvider locationProvider, Location location) {
        // have fun with your location!
    }
});
```

The `Location` will contain latitude, longitude and altitude, as well as some convenience methods to get the distance or angle to a different location.

> This assumes that the geo coordinates of the beacons are known. You can assign a `BeaconLocationProvider` to any beacon instance, which could read the geo coordinates from the advertising data or some external API.

[releases]: https://github.com/neXenio/BLE-Indoor-Positioning/releases
[jitpack]: https://jitpack.io/#neXenio/BLE-Indoor-Positioning/
[bintray]: https://bintray.com/nexenio/BLE-Indoor-Positioning
[package_core]: https://github.com/neXenio/BLE-Indoor-Positioning/tree/master/BLE%20Indoor%20Positioning/src/main/java/com/nexenio/bleindoorpositioning
[package_app]: https://github.com/neXenio/BLE-Indoor-Positioning/tree/master/app/src/main/java/com/nexenio/bleindoorpositioningdemo
[rxandroidble]: https://github.com/Polidea/RxAndroidBle
[androidbluetoothle]: https://developer.android.com/guide/topics/connectivity/bluetooth-le.html
[rssi]: https://en.wikipedia.org/wiki/Received_signal_strength_indication
[log_distance_path_loss_model]: https://en.wikipedia.org/wiki/Log-distance_path_loss_model
[levenberg_marquardt_algorithm]: https://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm
