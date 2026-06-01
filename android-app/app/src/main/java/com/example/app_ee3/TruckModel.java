package com.example.app_ee3;

public class TruckModel {
    String truckId;
    String waterLevel;
    boolean present;

    public TruckModel(String truckId, String waterLevel, boolean present) {
        this.truckId = truckId;
        this.waterLevel = waterLevel;
        this.present = present;
    }

    public String getTruckId() {
        return truckId;
    }

    public String getWaterLevel() {
        return waterLevel;
    }

    public boolean isPresent() {
        return present;
    }
}

