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
    private Handler handler;

    public emptySpace(TextView empty){
        this.empty = empty;
        handler = new Handler();
    }

    public void run(){
        while(MainActivity.onEmptyThread) {
            conn = new HttpURLConnector("empty_places_count");
            conn.start();
            try{
                conn.join();
            } catch(InterruptedException e){}
            String emptyStr = conn.getResult();
            JSONParser parser = new JSONParser(emptyStr);
            parser.parser();
            emptySpace = parser.getEmpty_space();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    empty.setText("현재 : " + emptySpace);
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
