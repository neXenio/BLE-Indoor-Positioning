package com.nexenio.bleindoorpositioning.gate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steppschuh on 21.12.17.
 */

public class GateGroup {

    private int index;
    private Map<Integer, Gate> gates = new HashMap<>();

    public GateGroup(int index) {
        this.index = index;
    }

    public Gate getClosestGate() {
        Gate closestGate = null;
        float closestDistance = Float.MAX_VALUE;
        float currentDistance;
        for (Gate gate : gates.values()) {
            currentDistance = gate.getClosestDistance();
            if (currentDistance < closestDistance) {
                closestGate = gate;
            }
        }
        return closestGate;
    }

    @Override
    public String toString() {
        return "Gate Group with index " + index + ", containing " + gates.size() + " gate(s)";
    }

    /*
        Getter & Setter
     */

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Map<Integer, Gate> getGates() {
        return gates;
    }

    public void setGates(Map<Integer, Gate> gates) {
        this.gates = gates;
    }

}
