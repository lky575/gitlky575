package com.example.lky575.parkingmanager;

import java.util.ArrayList;

/**
 * Created by lky575 on 2017-08-24.
 */

public class DBlog {
    public ArrayList<Integer> entered_array;
    public ArrayList<Integer> exited_array;

    public DBlog(ArrayList<Integer> entered_array, ArrayList<Integer> exited_array){
        this.entered_array = new ArrayList<>();
        this.exited_array = new ArrayList<>();

        this.entered_array = entered_array;
        this.exited_array = exited_array;
    }
}
