package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationPredictor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 14.02.18.
 */
public class LocationPredictorTest {

    public static final double HUMAN_WALKING_SPEED = 1.388889; // meters per second

    public static final Location SQUARE_ONE_GENDARMENMARKT = new Location(52.513588, 13.392995);
    public static final Location SQUARE_TWO_GENDARMENMARKT = new Location(52.513632, 13.392986);
    public static final Location SQUARE_THREE_GENDARMENMARKT = new Location(52.513675, 13.392979);
    public static final Location SQUARE_FOUR_GENDARMENMARKT = new Location(52.513720, 13.392971);
    public static final Location SQUARE_EXPECTED_GENDARMENMARKT = new Location(52.513764, 13.392962);

    @Test
    public void predictLocationFromLocations_locations_accuratePrediction() throws Exception {
        List<Location> strollOnGendarmenmarkt = new ArrayList<>();
        strollOnGendarmenmarkt.add(SQUARE_ONE_GENDARMENMARKT);
        strollOnGendarmenmarkt.add(SQUARE_TWO_GENDARMENMARKT);
        strollOnGendarmenmarkt.add(SQUARE_THREE_GENDARMENMARKT);
        strollOnGendarmenmarkt.add(SQUARE_FOUR_GENDARMENMARKT);

        // set timestamps based on timestamp of previous square and human walking speed
        long walkingTimeEstimation = TimeUnit.SECONDS.toMillis((long) (SQUARE_ONE_GENDARMENMARKT.getDistanceTo(SQUARE_TWO_GENDARMENMARKT) / HUMAN_WALKING_SPEED));
        SQUARE_TWO_GENDARMENMARKT.setTimestamp(SQUARE_TWO_GENDARMENMARKT.getTimestamp() + walkingTimeEstimation);
        walkingTimeEstimation = TimeUnit.SECONDS.toMillis((long) (SQUARE_TWO_GENDARMENMARKT.getDistanceTo(SQUARE_THREE_GENDARMENMARKT) / HUMAN_WALKING_SPEED));
        SQUARE_THREE_GENDARMENMARKT.setTimestamp(SQUARE_TWO_GENDARMENMARKT.getTimestamp() + walkingTimeEstimation);
        walkingTimeEstimation = TimeUnit.SECONDS.toMillis((long) (SQUARE_THREE_GENDARMENMARKT.getDistanceTo(SQUARE_FOUR_GENDARMENMARKT) / HUMAN_WALKING_SPEED));
        SQUARE_FOUR_GENDARMENMARKT.setTimestamp(SQUARE_THREE_GENDARMENMARKT.getTimestamp() + walkingTimeEstimation);

        Location predictedLocation = LocationPredictor.predict(strollOnGendarmenmarkt, TimeUnit.SECONDS.toMillis(1));
        double distance = predictedLocation.getDistanceTo(SQUARE_EXPECTED_GENDARMENMARKT);
        assertEquals(0, distance, 5);
    }
}