package com.example.app_ee3;

public class InterventionModel {
    String intervention_id;
    String dispatch;
    String truck;
    int duration;

    public InterventionModel(String intervention_id, String dispatch, String truck, int duration) {
        this.intervention_id = intervention_id;
        this.dispatch = dispatch;
        this.truck = truck;
        this.duration = duration;
    }

    public String getIntervention_id() {
        return intervention_id;
    }

    public String getDispatch() {
        return dispatch;
    }

    public String getTruck() {
        return truck;
    }

    public int getDuration() {
        return duration;
    }

}
