package com.example.lky575.parkingmanager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lky575 on 2017-07-06.
 */

public class JSONParser {
    private String dbStr;
    private String numbering;
    private String zone_name;
    private int started_at;
    private int zone_index;
    private int floor;
    private int empty_space;
    private int errorCode;
    private String message;
    private boolean isCarNumbering;

    public JSONParser(String dbStr){
        this.dbStr = dbStr;
        numbering = null;
        zone_name = null;
        started_at = 0;
        zone_index = 0;
        floor = 0;
        empty_space = 0;

        errorCode = 0;
        message = null;
        isCarNumbering = false;
    }

    public void parser(){
        try {
            JSONObject json = new JSONObject(dbStr);

            if(json.has("error")) {
                JSONObject notFind = json.getJSONObject("error");
                errorCode = notFind.getInt("code");
                message = notFind.getString("message");
            }

            if(json.has("car")) {
                JSONObject result = json.getJSONObject("car");
                numbering = result.getString("numbering");
                started_at = result.getInt("started_at");
                result = json.getJSONObject("place");
                zone_name = result.getString("zone_name");
                zone_index = result.getInt("zone_index");
                floor = result.getInt("floor");
                isCarNumbering = true;
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public int getEmpty_space() { return empty_space; }

    public String getNumbering() {
        return numbering;
    }

    public String getZone_name() {
        return zone_name;
    }

    public int getStarted_at() {
        return started_at;
    }

    public int getZone_index() {
        return zone_index;
    }

    public int getFloor() {
        return floor;
    }

    public String getMessage() { return message; }

    public int getErrorCode() { return errorCode; }

    public boolean isCarNumbering() { return isCarNumbering; }
}
