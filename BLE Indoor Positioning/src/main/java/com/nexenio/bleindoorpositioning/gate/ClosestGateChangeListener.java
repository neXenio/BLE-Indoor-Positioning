package com.nexenio.bleindoorpositioning.gate;

/**
 * Created by steppschuh on 21.12.17.
 */

public interface ClosestGateChangeListener {

    void onClosestGateDistanceChanged(GateGroup gateGroup, Gate gate, float distance);

    void onClosestGateChanged(GateGroup gateGroup, Gate gate, float distance);

    void onClosestGateGroupChanged(GateGroup gateGroup, Gate gate, float distance);

}
