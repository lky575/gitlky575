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
    private HttpURLConnector conn;

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

        SharedPreferences pref = getSharedPreferences("sign", Activity.MODE_PRIVATE);
        String carNumber = pref.getString("CarNumber","");

        edtCarNumber.setText(carNumber);
        if(!carNumber.equals("")){
            task = new ProgressDialogTask(CalculateFare.this);
            task.execute();
            conn = new HttpURLConnector(carNumber);
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
        edtEndHour.setText(String.valueOf(now.get(Calendar.HOUR_OF_DAY)));
        edtEndMinute.setText(String.valueOf(now.get(Calendar.MINUTE)));
    }

    public void onsearchButtonClicked(View v){
        task = new ProgressDialogTask(CalculateFare.this);
        task.execute();
        conn = new HttpURLConnector(edtCarNumber.getText().toString());
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){}
        String dbResult = conn.getResult();
        JSONParser parser = new JSONParser(dbResult);
        parser.parser();
        if(parser.isCarNumbering()){
            int started_at = parser.getStarted_at();
            getTime(started_at);
        }
        else{
            int errorCode = parser.getErrorCode();
            String message = parser.getMessage();

            Toast.makeText(getApplication(),"에러코드 : " + errorCode + "\n" + message,Toast.LENGTH_LONG).show();
        }

        Calendar now = Calendar.getInstance();
        edtEndHour.setText(String.valueOf(now.get(Calendar.HOUR_OF_DAY)));
        edtEndMinute.setText(String.valueOf(now.get(Calendar.MINUTE)));

        task.setFinish();
    }

    public void oncalculateButtonClicked(View v){
        startHour = Integer.parseInt(edtStartHour.getText().toString());
        startMinute = Integer.parseInt(edtStartMinute.getText().toString());
        endHour = Integer.parseInt(edtEndHour.getText().toString());
        endMinute = Integer.parseInt(edtEndMinute.getText().toString());
        if(startHour >= 24 || endHour >= 24){
            Toast.makeText(getApplicationContext(),"시간을 잘못 입력 하셨습니다.",Toast.LENGTH_SHORT).show();
        }
        else if(startMinute >= 60 || endMinute >= 60){
            Toast.makeText(getApplicationContext(),"분을 잘못 입력 하셨습니다.",Toast.LENGTH_SHORT).show();
        }
        else {
            int gap_H = endHour - startHour;
            int gap_M = endMinute - startMinute;
            if (gap_H < 0) {
                Toast.makeText(getApplicationContext(), "시간을 잘못 입력 하셨습니다.", Toast.LENGTH_SHORT).show();
            } else {
                if (gap_M < 0) {
                    gap_H--;
                    gap_M += 60;
                }
                if (gap_M >= 30)
                    gap_H++;
                int fare = gap_H * 2000;
                fareText.setText("요금 : " + fare + "원");
            }
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
    }
}
