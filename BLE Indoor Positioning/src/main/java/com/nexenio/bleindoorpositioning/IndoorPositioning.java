package com.nexenio.bleindoorpositioning;

public class IndoorPositioning {

    private static IndoorPositioning instance;

    private IndoorPositioning() {

    }

    public static IndoorPositioning getInstance() {
        if (instance == null) {
            instance = new IndoorPositioning();
        }
        return instance;
    }

}
