package com.example.lky575.parkingmanager;

import android.util.Log;

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
    private int code;
    private String message;
    private boolean error;
    private boolean car;

    public JSONParser(String dbStr){
        this.dbStr = dbStr;
        numbering = null;
        zone_name = null;
        started_at = 0;
        zone_index = 0;
        floor = 0;
        empty_space = 0;

        code = 0;
        message = null;
        error = false;
        car = false;
    }

    public void parser(){
        try {
            JSONObject json = new JSONObject(dbStr);

            if(json.has("error")) {
                JSONObject notFind = json.getJSONObject("error");
                code = notFind.getInt("code");
                message = notFind.getString("message");
                error = true;
            }

            if(json.has("car")) {
                JSONObject result = json.getJSONObject("car");
                numbering = result.getString("numbering");
                started_at = result.getInt("started_at");
                result = json.getJSONObject("place");
                zone_name = result.getString("zone_name");
                zone_index = result.getInt("zone_index");
                floor = result.getInt("floor");
                car = true;
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public void logprn() {
        Log.d("conn","차 번호 : " + numbering +
                "\n입차 시간 : " + started_at +
                "\n주차 구역 : " + floor + zone_name + zone_index +
        "\n차 : " + car);

        Log.d("conn","코드 : " + code +
        "\n메세지 : " + message +
        "\n에러 : " + error);
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
}
