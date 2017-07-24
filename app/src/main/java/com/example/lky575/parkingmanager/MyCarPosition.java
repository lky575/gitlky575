package com.example.lky575.parkingmanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MyCarPosition extends AppCompatActivity {

    private EditText numberText;
    private SharedPreferences pref;
    private HttpURLConnector conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_my_car_position);

            numberText = (EditText) findViewById(R.id.numberText_po);

            pref = getSharedPreferences("sign", Activity.MODE_PRIVATE);
            String number = pref.getString("CarNumber","");
            numberText.setText(number);
    }

    public void onsearchButtonClicked(View v){
        ProgressDialogTask task = new ProgressDialogTask(MyCarPosition.this);
        task.execute();
        String number = numberText.getText().toString();
        String url = "http://13.124.74.249:3000/cars/";
        conn = new HttpURLConnector(number); //88%ED%97%881234
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){};
        String dbResult = conn.getResult();
        JSONParser parser = new JSONParser(dbResult);
        parser.parser();

        if(parser.isCarNumbering()) {
            int floor = parser.getFloor();
            int zone_index = parser.getZone_index();
            String zone_name = parser.getZone_name();

            Toast.makeText(getApplicationContext(), floor + "층" + zone_name + zone_index, Toast.LENGTH_LONG).show();
        }

        else{
            int errorCode = parser.getErrorCode();
            String message = parser.getMessage();

            Toast.makeText(getApplication(),"에러코드 : " + errorCode + "\n" + message,Toast.LENGTH_LONG).show();
        }
        task.setFinish();
    }

    public void onbackButtonClicked(View v){
        finish();
    }
}
