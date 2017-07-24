package com.example.lky575.parkingmanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MyCarPosition extends AppCompatActivity {

    EditText numberText;
    SharedPreferences pref;
    HttpURLConnector conn;

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
        String number = numberText.getText().toString();
        String url = "http://13.124.74.249:3000/cars/";
        conn = new HttpURLConnector(number); //88%ED%97%881234
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){};
        String dbResult = conn.getResult();
        Log.d("conn","json 파싱전 : " + dbResult);
        JSONParser parser = new JSONParser(dbResult);
        parser.parser();
        int floor = parser.getFloor();
        int zone_index = parser.getZone_index();
        String zone_name = parser.getZone_name();

        Toast.makeText(getApplicationContext(),floor +"층" + zone_name + zone_index,Toast.LENGTH_LONG).show();

    }

    public void onbackButtonClicked(View v){
        finish();
    }
}
