package com.example.lky575.parkingmanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public class CalculateFare extends AppCompatActivity {

    private EditText edtCarNumber, edtStartHour, edtStartMinute, edtEndHour, edtEndMinute;
    private TextView fareText;
    private int startHour, startMinute, endHour, endMinute;
    private ProgressDialogTask task;

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

        task = new ProgressDialogTask(CalculateFare.this);

        SharedPreferences pref = getSharedPreferences("sign", Activity.MODE_PRIVATE);
        String carNumber = pref.getString("CarNumber","");

        edtCarNumber.setText(carNumber);
        if(!carNumber.equals("")){
            task.execute();
            HttpURLConnector conn = new HttpURLConnector(carNumber);
            conn.start();
            try{
                conn.join();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
            String dbResult = conn.getResult();
            JSONParser parser = new JSONParser(dbResult);
            parser.parser();
            int started_at = parser.getStarted_at();
            getTime(started_at);
            task.setFinish();
        }

        Calendar now = Calendar.getInstance();
        endHour = now.get(Calendar.HOUR_OF_DAY);
        endMinute = now.get(Calendar.MINUTE);
        edtEndHour.setText(String.valueOf(endHour));
        edtEndMinute.setText(String.valueOf(endMinute));
    }

    public void onsearchButtonClicked(View v){
        HttpURLConnector conn = new HttpURLConnector(edtCarNumber.getText().toString());
        task.execute();
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){}
        String dbResult = conn.getResult();
        JSONParser parser = new JSONParser(dbResult);
        parser.parser();
        int started_at = parser.getStarted_at();
        getTime(started_at);
        task.setFinish();
    }

    public void oncalculateButtonClicked(View v){
        int gap_H = endHour - startHour;
        int gap_M = endMinute - startMinute;
        if(gap_H < 0){
            Toast.makeText(getApplicationContext(),"시간을 잘못 입력 하셨습니다.",Toast.LENGTH_SHORT).show();
        }
        else {
            if (gap_M < 0) {
                gap_H--;
                gap_M += 60;
            }
            if (gap_M >= 30)
                gap_H++;
            int fare = gap_H * 2000;
            fareText.setText("요금 : " + fare + "원.");
        }
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
