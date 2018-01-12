package com.nexenio.bleindoorpositioning.location;

import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

/**
 * Created by steppschuh on 21.11.17.
 */

public interface LocationListener {

    void onLocationUpdated(LocationProvider locationProvider, Location location);

}
