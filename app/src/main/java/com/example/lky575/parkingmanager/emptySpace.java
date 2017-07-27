package com.example.lky575.parkingmanager;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by lky575 on 2017-07-17.
 */

public class emptySpace extends Thread {
    private TextView empty;
    private HttpURLConnector conn;
    private int emptySpace;

    public emptySpace(TextView empty){
        this.empty = empty;
    }

    public void run(){
        while(!MainActivity.onEmptyThread) {
            //conn = new HttpURLConnector("여석 url");
            //String emptyStr = conn.connect();
            //JSONParser parser = new JSONParser(emptyStr);
            //emptySpace = parser.getEmpty_space();

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    empty.setText(emptySpace + "");
                }
            });
            try{
                Thread.sleep(5000);
            } catch(InterruptedException e){
                Log.d("Exception","InterruptedException");
            }
        }
    }
}
