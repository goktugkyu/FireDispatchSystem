package com.example.app_ee3;

import java.sql.Time;

public class DispatchModel {
    String date;
    String time;
    String location;
    String situation;
    String dispatch_id;


    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getSituation() { return situation; }
    public String getDispatch_id() { return dispatch_id; }

    public DispatchModel(String date, String time, String location, String situation, String dispatch_id) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.situation = situation;
        this.dispatch_id = dispatch_id;
    }
}
