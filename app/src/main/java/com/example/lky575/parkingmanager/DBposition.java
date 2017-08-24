package com.example.lky575.parkingmanager;

/**
 * Created by lky575 on 2017-08-24.
 */

public class DBposition {
    public int floor;
    public int zone_index;
    public String zone_name;

    public DBposition(int floor, int zone_index, String zone_name){
        this.floor = floor;
        this.zone_index = zone_index;
        this.zone_name = zone_name;
    }
}
