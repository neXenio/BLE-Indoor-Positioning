package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationPredictor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.nexenio.bleindoorpositioning.location.LocationPredictor.calculateSpeed;
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
        List<Location> strollOnGendarmenmarkt = setupLocations();
        Location predictedLocation = LocationPredictor.predict(strollOnGendarmenmarkt, TimeUnit.SECONDS.toMillis(1));
        double distance = predictedLocation.getDistanceTo(SQUARE_EXPECTED_GENDARMENMARKT);
        assertEquals(0, distance, 5);
    }

    @Test
    public void calculateSpeed_locations_correctSpeed() throws Exception {
        List<Location> strollOnGendarmenmarkt = setupLocations();
        double speed = calculateSpeed(strollOnGendarmenmarkt);
        assertEquals(speed, HUMAN_WALKING_SPEED, 0.1);
    }

    public List<Location> setupLocations() {
        List<Location> strollOnGendarmenmarkt = new ArrayList<>();
        strollOnGendarmenmarkt.add(SQUARE_ONE_GENDARMENMARKT);
        strollOnGendarmenmarkt.add(SQUARE_TWO_GENDARMENMARKT);
        strollOnGendarmenmarkt.add(SQUARE_THREE_GENDARMENMARKT);
        strollOnGendarmenmarkt.add(SQUARE_FOUR_GENDARMENMARKT);

        // set timestamps based on timestamp of previous square and human walking speed
        double timeNeeded = SQUARE_ONE_GENDARMENMARKT.getDistanceTo(SQUARE_TWO_GENDARMENMARKT) / HUMAN_WALKING_SPEED;
        // TimeUnit.SECONDS.toMillis is less accurate because value is casted to long before multiplication
        long walkingTimeEstimation = (long) (timeNeeded * 1000);
        SQUARE_TWO_GENDARMENMARKT.setTimestamp(SQUARE_TWO_GENDARMENMARKT.getTimestamp() + walkingTimeEstimation);
        timeNeeded = SQUARE_TWO_GENDARMENMARKT.getDistanceTo(SQUARE_THREE_GENDARMENMARKT) / HUMAN_WALKING_SPEED;
        walkingTimeEstimation = (long) (timeNeeded * 1000);
        SQUARE_THREE_GENDARMENMARKT.setTimestamp(SQUARE_TWO_GENDARMENMARKT.getTimestamp() + walkingTimeEstimation);
        timeNeeded = SQUARE_THREE_GENDARMENMARKT.getDistanceTo(SQUARE_FOUR_GENDARMENMARKT) / HUMAN_WALKING_SPEED;
        walkingTimeEstimation = (long) (timeNeeded * 1000);
        SQUARE_FOUR_GENDARMENMARKT.setTimestamp(SQUARE_THREE_GENDARMENMARKT.getTimestamp() + walkingTimeEstimation);
        return strollOnGendarmenmarkt;
    }
}