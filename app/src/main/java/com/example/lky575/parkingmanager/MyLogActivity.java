package com.example.lky575.parkingmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MyLogActivity extends AppCompatActivity {
    private ParkingLog mylog;
    private ProgressDialogTask task;
    private TextView logText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_log);
        logText = (TextView) findViewById(R.id.logText);
        SharedPreferences pref = getSharedPreferences("sign", Context.MODE_PRIVATE);
        String carNumber = pref.getString("CarNumber",null);

        task = new ProgressDialogTask(MyLogActivity.this);
        task.execute();

        HttpURLConnector conn = new HttpURLConnector("entering_logs?car_numbering=" + carNumber);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){}
        JSONParser parser = new JSONParser(conn.getResult());
        parser.parser_array(pref);

        ArrayList<Integer> entered_array = parser.getEntered_array();
        ArrayList<Integer> exited_array = parser.getExited_array();

        mylog = new ParkingLog(getApplicationContext(), "PARKING_LOG", null, 1);
        mylog.setLog(entered_array, exited_array, carNumber);

        ArrayList<String> entered_date = new ArrayList<>();
        ArrayList<String> exited_date = new ArrayList<>();
        Cursor cursor = mylog.getLog(carNumber);
        while(cursor.moveToNext()){
            entered_date.add(cursor.getString(2));
            exited_date.add(cursor.getString(3));
        }
        // 리스트뷰 형식으로 전환
/*        String logData = mylog.PrintData();

        if(logData.equals(""))
            logText.setText("주차 기록이 없습니다.");

        else logText.setText(logData);*/
        task.setFinish();

    }

    public void onbackButtonClicked(View v){
        finish();
    }
}
