package com.example.lky575.parkingmanager;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by lky575 on 2017-07-17.
 */

public class emptySpace extends Thread {
    private TextView empty;
    private int emptySpace;
    private Handler handler;
    private getDBdata getDBdata;

    public emptySpace(TextView empty){
        this.empty = empty;
        handler = new Handler();
        getDBdata = new getDBdata();
    }

    public void run(){
        while(MainActivity.onEmptyThread) {
            emptySpace = getDBdata.getEmpty_Space();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (emptySpace >= MainActivity.TOTAL_SPACE / 2)
                        empty.setTextColor(Color.rgb(0, 255, 0));
                    else if(emptySpace == 0)
                        empty.setTextColor(Color.rgb(255, 0, 0));
                    else
                        empty.setTextColor(Color.rgb(255, 187, 0));

                    empty.setText("남은 공간\n" + emptySpace);
                }
            });
            try{
                Thread.sleep(5000);
            } catch(InterruptedException e){
                Log.d("Exception","emptySpace : " + e.getMessage());
            }
        }
    }
}
