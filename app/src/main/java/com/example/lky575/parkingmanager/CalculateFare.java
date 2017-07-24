package com.example.lky575.parkingmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public class CalculateFare extends AppCompatActivity {

    EditText edtCarNumber, edtStartHour, edtStartMinute, edtEndHour, edtEndMinute;
    TextView fareText;
    int startHour, startMinute, endHour, endMinute;
    ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_fare);

        edtCarNumber = (EditText) findViewById(R.id.EdtCarNumber);
        edtStartHour = (EditText) findViewById(R.id.EdtstartHour);
        edtStartMinute = (EditText) findViewById(R.id.EdtstartMinute);
        edtEndHour = (EditText) findViewById(R.id.EdtendHour);
        edtEndMinute = (EditText) findViewById(R.id.EdtendMinute);
        fareText = (TextView) findViewById(R.id.Fare);

        waitDialog = new ProgressDialog(CalculateFare.this);
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        SharedPreferences pref = getSharedPreferences("sign", Activity.MODE_PRIVATE);
        String carNumber = pref.getString("CarNumber","");

        edtCarNumber.setText(carNumber);
        if(!carNumber.equals("")){
            HttpURLConnector conn = new HttpURLConnector(carNumber);
            conn.start();
            try{
                waitDialog.setMessage("잠시만 기다려주세요.");
                waitDialog.show();
                conn.join();
                waitDialog.dismiss();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
            String dbResult = conn.getResult();
            JSONParser parser = new JSONParser(dbResult);
            parser.parser();
            int started_at = parser.getStarted_at();
            getTime(started_at);
        }

        Calendar now = Calendar.getInstance();
        int endHour = now.get(Calendar.HOUR_OF_DAY);
        int endMinute = now.get(Calendar.MINUTE);
        edtEndHour.setText(String.valueOf(endHour));
        edtEndMinute.setText(String.valueOf(endMinute));
    }

    public void onsearchButtonClicked(View v){
        HttpURLConnector conn = new HttpURLConnector(edtCarNumber.getText().toString());
        conn.start();
        try{
            waitDialog.setMessage("잠시만 기다려주세요.");
            waitDialog.show();
            conn.join();
            waitDialog.dismiss();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        String dbResult = conn.getResult();
        JSONParser parser = new JSONParser(dbResult);
        parser.parser();
        int started_at = parser.getStarted_at();
        getTime(started_at);
    }

    public void oncalculateButtonClicked(View v){
        int gap_H = endHour - startHour;
        int gap_M = endMinute - startMinute;
        if(gap_M >= 30)
            gap_H++;
        int fare = gap_H * 2000;
        fareText.setText("주차 요금 : " + fare + "원.");
    }

    public void onbackButtonClicked(View v){
        finish();
    }

    public void getTime(int millisec){
        String pattern = "HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date = formatter.format(millisec);
        StringTokenizer tokenizer = new StringTokenizer(date,":");
        edtStartHour.setText(tokenizer.nextToken());
        edtStartMinute.setText(tokenizer.nextToken());
        startHour = Integer.parseInt(edtStartHour.getText().toString());
        startMinute = Integer.parseInt(edtStartMinute.getText().toString());
    }
}
