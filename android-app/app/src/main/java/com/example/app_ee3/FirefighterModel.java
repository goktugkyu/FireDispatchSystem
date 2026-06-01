package com.example.app_ee3;

public class FirefighterModel {

    private String name;
    private int batch_number;
    private boolean present;

    public FirefighterModel(String name, int batch_number, boolean present) {
        this.name = name;
        this.batch_number = batch_number;
        this.present = present;
    }

    public String getName() {
        return name;
    }

    public int getBatch_number() {
        return batch_number;
    }

    public boolean isPresent() {
        return present;
    }
}
