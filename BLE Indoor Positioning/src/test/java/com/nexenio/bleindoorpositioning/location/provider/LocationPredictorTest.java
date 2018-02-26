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

    public static final Location FIRST_POINT_OF_LINE = new Location(52.513588, 13.392995);
    public static final Location SECOND_POINT_OF_LINE = new Location(52.513632, 13.392986);
    public static final Location THIRD_POINT_OF_LINE = new Location(52.513675, 13.392979);
    public static final Location FOURTH_POINT_OF_LINE = new Location(52.513720, 13.392971);
    public static final Location FIFTH_POINT_OF_LINE = new Location(52.513764, 13.392962);

    public static final Location CIRCLE_ONE_GENDARMENMARKT = new Location(52.513709, 13.392752);
    public static final Location CIRCLE_TWO_GENDARMENMARKT = new Location(52.513748, 13.392672);
    public static final Location CIRCLE_THREE_GENDARMENMARKT = new Location(52.513799, 13.392738);
    public static final Location CIRCLE_FOUR_GENDARMENMARKT = new Location(52.513759, 13.392818);

    @Test
    public void predictLocationFromLocations_locationsInLine_accuratePrediction() throws Exception {
        List<Location> lineOnGendarmenmarkt = setupStraightLineLocations();
        Location predictedLocation = LocationPredictor.predict(lineOnGendarmenmarkt, TimeUnit.SECONDS.toMillis(1));
        double delta = predictedLocation.getDistanceTo(FIFTH_POINT_OF_LINE);
        assertEquals(0, delta, 5);
    }

    @Test
    public void predictLocationFromLocations_locationsInCircle_accuratePrediction() throws Exception {
        List<Location> circleOnGendarmenmarkt = setupCircleLocations();
        Location predictedLocation = LocationPredictor.predict(circleOnGendarmenmarkt, TimeUnit.SECONDS.toMillis(1));
        double delta = predictedLocation.getDistanceTo(CIRCLE_ONE_GENDARMENMARKT);
        System.out.println(predictedLocation);
        // TODO decrease delta
        assertEquals(0, delta, 10);
    }

    // TODO fix test on ci
    @Test
    public void calculateSpeed_locations_correctSpeed() throws Exception {
        List<Location> strollOnGendarmenmarkt = setupStraightLineLocations();
        double speed = calculateSpeed(strollOnGendarmenmarkt);
        Thread.sleep(10000);
        assertEquals(speed, HUMAN_WALKING_SPEED, 0.1);
    }

    private static List<Location> setupStraightLineLocations() {
        List<Location> walkTheLine = new ArrayList<>();
        walkTheLine.add(FIRST_POINT_OF_LINE);
        walkTheLine.add(SECOND_POINT_OF_LINE);
        walkTheLine.add(THIRD_POINT_OF_LINE);
        walkTheLine.add(FOURTH_POINT_OF_LINE);

        // set timestamps based on timestamp of previous square and human walking speed
        double timeNeeded = FIRST_POINT_OF_LINE.getDistanceTo(SECOND_POINT_OF_LINE) / HUMAN_WALKING_SPEED;
        // TimeUnit.SECONDS.toMillis is less accurate because value is casted to long before multiplication
        long walkingTimeEstimation = (long) (timeNeeded * 1000);
        SECOND_POINT_OF_LINE.setTimestamp(SECOND_POINT_OF_LINE.getTimestamp() + walkingTimeEstimation);
        timeNeeded = SECOND_POINT_OF_LINE.getDistanceTo(THIRD_POINT_OF_LINE) / HUMAN_WALKING_SPEED;
        walkingTimeEstimation = (long) (timeNeeded * 1000);
        THIRD_POINT_OF_LINE.setTimestamp(SECOND_POINT_OF_LINE.getTimestamp() + walkingTimeEstimation);
        timeNeeded = THIRD_POINT_OF_LINE.getDistanceTo(FOURTH_POINT_OF_LINE) / HUMAN_WALKING_SPEED;
        walkingTimeEstimation = (long) (timeNeeded * 1000);
        FOURTH_POINT_OF_LINE.setTimestamp(THIRD_POINT_OF_LINE.getTimestamp() + walkingTimeEstimation);
        return walkTheLine;
    }

    private static List<Location> setupCircleLocations() {
        List<Location> walkInACircle = new ArrayList<>();
        walkInACircle.add(CIRCLE_ONE_GENDARMENMARKT);
        walkInACircle.add(CIRCLE_TWO_GENDARMENMARKT);
        walkInACircle.add(CIRCLE_THREE_GENDARMENMARKT);
        walkInACircle.add(CIRCLE_FOUR_GENDARMENMARKT);

        // set timestamps based on timestamp of previous square and human walking speed
        double timeNeeded = CIRCLE_ONE_GENDARMENMARKT.getDistanceTo(CIRCLE_TWO_GENDARMENMARKT) / HUMAN_WALKING_SPEED;
        // TimeUnit.SECONDS.toMillis is less accurate because value is casted to long before multiplication
        long walkingTimeEstimation = (long) (timeNeeded * 1000);
        CIRCLE_TWO_GENDARMENMARKT.setTimestamp(CIRCLE_TWO_GENDARMENMARKT.getTimestamp() + walkingTimeEstimation);
        timeNeeded = CIRCLE_TWO_GENDARMENMARKT.getDistanceTo(CIRCLE_THREE_GENDARMENMARKT) / HUMAN_WALKING_SPEED;
        walkingTimeEstimation = (long) (timeNeeded * 1000);
        CIRCLE_THREE_GENDARMENMARKT.setTimestamp(CIRCLE_TWO_GENDARMENMARKT.getTimestamp() + walkingTimeEstimation);
        timeNeeded = CIRCLE_THREE_GENDARMENMARKT.getDistanceTo(CIRCLE_FOUR_GENDARMENMARKT) / HUMAN_WALKING_SPEED;
        walkingTimeEstimation = (long) (timeNeeded * 1000);
        CIRCLE_FOUR_GENDARMENMARKT.setTimestamp(CIRCLE_THREE_GENDARMENMARKT.getTimestamp() + walkingTimeEstimation);
        return walkInACircle;
    }
}