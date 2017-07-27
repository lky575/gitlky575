package com.example.lky575.parkingmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MyLogActivity extends AppCompatActivity {
    private ParkingLog mylog;
    private ProgressDialogTask task;
    private TextView logText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_log);

        logText = (TextView) findViewById(R.id.logText);

        task = new ProgressDialogTask(MyLogActivity.this);
        task.execute();

        mylog = new ParkingLog(getApplicationContext(), "PARKING_LOG", null, 1);
        String logData = mylog.PrintData();

        if(logData.equals(""))
            logText.setText("주차 기록이 없습니다.");

        else logText.setText(logData);
        task.setFinish();

    }

    public void onbackButtonClicked(View v){
        finish();
    }
}
