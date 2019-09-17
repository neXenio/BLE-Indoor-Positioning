package com.nexenio.bleindoorpositioning.ble.beacon.distance.benchmark;

import java.util.List;

public interface ScoreMerger {

    String getName();

    double mergeScores(List<Double> scores);
}
