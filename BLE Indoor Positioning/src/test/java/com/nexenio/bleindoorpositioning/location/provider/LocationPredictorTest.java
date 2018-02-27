package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationPredictor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 14.02.18.
 */
public class LocationPredictorTest {

    public static final double HUMAN_WALKING_SPEED = 1.388889; // meters per second

    public static final Location FIRST_POINT_OF_LINE = new Location(52.513588, 13.392995);
    public static final Location SECOND_POINT_OF_LINE = new Location(52.513632, 13.392986);
    public static final Location THIRD_POINT_OF_LINE = new Location(52.513675, 13.392979);
    public static final Location FOURTH_POINT_OF_LINE = new Location(52.513720, 13.392971);
    public static final Location FIFTH_POINT_OF_LINE = new Location(52.513764, 13.392962);

    @Test
    public void predictLocationFromLocations_locationsInLine_accuratePrediction() throws Exception {
        List<Location> lineOnGendarmenmarkt = new ArrayList<>(Arrays.asList(
                new Location(FIRST_POINT_OF_LINE), new Location(SECOND_POINT_OF_LINE),
                new Location(THIRD_POINT_OF_LINE), new Location(FOURTH_POINT_OF_LINE)
        ));
        lineOnGendarmenmarkt = setupTimestampsForLocations(lineOnGendarmenmarkt);
        Location predictedLocation = LocationPredictor.predict(lineOnGendarmenmarkt, TimeUnit.SECONDS.toMillis(1));
        double delta = predictedLocation.getDistanceTo(FIFTH_POINT_OF_LINE);
        assertEquals(0, delta, 5);
    }

    @Test
    public void predictLocationFromLocations_locationsInCircle_accuratePrediction() throws Exception {
        long timestampDelta = 500;
        double distanceToNextLocation = HUMAN_WALKING_SPEED / TimeUnit.MILLISECONDS.toSeconds(timestampDelta);
        List<Location> walkInACircle = new ArrayList<>();
        Location nextLocation;
        Location lastLocation = null;
        for (int angle = 0; angle < 360; angle += 10) {
            if (angle == 0) {
                nextLocation = new Location(FIRST_POINT_OF_LINE).getShiftedLocation(distanceToNextLocation, angle);
            } else {
                nextLocation = lastLocation.getShiftedLocation(distanceToNextLocation, angle);
                nextLocation.setTimestamp(lastLocation.getTimestamp() + timestampDelta);
            }
            walkInACircle.add(nextLocation);
            lastLocation = nextLocation;
        }

        for (int i = 0; i < walkInACircle.size() - 3; i++) {
            List<Location> subList = new ArrayList<>();
            subList.add(walkInACircle.get(i));
            subList.add(walkInACircle.get(i + 1));

            Location lastKnownLocation = walkInACircle.get(i + 2);
            subList.add(lastKnownLocation);

            Location expectedLocation = walkInACircle.get(i + 3);
            Location predictedLocation = LocationPredictor.predict(subList, TimeUnit.SECONDS.toMillis(1));

            // angle
            double expectedAngle = lastKnownLocation.getAngleTo(expectedLocation);
            double actualAngle = lastKnownLocation.getAngleTo(predictedLocation);
            assertEquals(expectedAngle, actualAngle, 50);

            // distance
            double expectedDistance = lastKnownLocation.getDistanceTo(expectedLocation);
            double actualDistance = lastKnownLocation.getDistanceTo(predictedLocation);
            assertEquals(expectedDistance, actualDistance, 1);
        }

    }

    @Test
    public void calculateSpeed_locations_correctSpeed() throws Exception {
        List<Location> lineOnGendarmenmarkt = new ArrayList<>(Arrays.asList(
                new Location(FIRST_POINT_OF_LINE), new Location(SECOND_POINT_OF_LINE),
                new Location(THIRD_POINT_OF_LINE), new Location(FOURTH_POINT_OF_LINE)
        ));
        lineOnGendarmenmarkt = setupTimestampsForLocations(lineOnGendarmenmarkt);
        double speed = LocationPredictor.calculateSpeed(lineOnGendarmenmarkt);
        assertEquals(speed, HUMAN_WALKING_SPEED, 0.1);
    }

    private static List<Location> setupTimestampsForLocations(List<Location> locations) {
        for (int i = 1; i < locations.size(); i++) {
            // set timestamps based on timestamp of previous square and human walking speed
            double timeNeeded = locations.get(i - 1).getDistanceTo(locations.get(i)) / HUMAN_WALKING_SPEED;
            long walkingTimeEstimation = (long) (timeNeeded * TimeUnit.SECONDS.toMillis(1));
            locations.get(i).setTimestamp(locations.get(i - 1).getTimestamp() + walkingTimeEstimation);
        }
        return locations;
    }
}