package com.nexenio.bleindoorpositioning.ble.beacon.distance.benchmark;

import java.util.List;

public class MeanScoreMerger implements ScoreMerger {

    @Override
    public String getName() {
        return "Mean-Score";
    }

    @Override
    public double mergeScores(List<Double> scores) {
        if (scores.isEmpty()) {
            return 0;
        }
        double sum = scores.stream().mapToDouble(d -> d).sum();
        return sum / scores.size();
    }
}
