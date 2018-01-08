package com.nexenio.bleindoorpositioningdemo.location;

import com.nexenio.bleindoorpositioning.location.Location;

/**
 * Some locations that might be useful for testing. Latitudes and longitudes are real values
 * obtained from Google Maps.
 */

public abstract class TestLocations {

    /*
        Berlin Genamenmarkt
        https://goo.gl/maps/PmmdX3sQAXR2
     */
    public static final Location GENDAMENMARKT_COURT_TOP_LEFT = new Location(52.513925, 13.392239);
    public static final Location GENDAMENMARKT_COURT_TOP_RIGHT = new Location(52.513968, 13.392971);
    public static final Location GENDAMENMARKT_COURT_BOTTOM_LEFT = new Location(52.513298, 13.392340);
    public static final Location GENDAMENMARKT_COURT_BOTTOM_RIGHT = new Location(52.513341, 13.393071);
    public static final Location GENDAMENMARKT_COURT_CENTER = new Location(52.513639, 13.392645);

    public static final Location GENDAMENMARKT_TOP_LEFT = new Location(52.514873, 13.391102);
    public static final Location GENDAMENMARKT_TOP_RIGHT = new Location(52.514992, 13.393043);
    public static final Location GENDAMENMARKT_BOTTOM_LEFT = new Location(52.512251, 13.391545);
    public static final Location GENDAMENMARKT_BOTTOM_RIGHT = new Location(52.512374, 13.393467);

}
