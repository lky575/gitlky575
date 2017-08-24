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

        DBposition position = new getDBdata().getPosition(number, MyCarPosition.this);

        if(position != null){
            Toast.makeText(getApplicationContext(), position.floor + "층" + position.zone_name + position.zone_index, Toast.LENGTH_LONG).show();
        }

        else{
            Toast.makeText(getApplication(),"해당 차량을 찾을 수 없습니다.",Toast.LENGTH_LONG).show();
        }
    }

    public void onbackButtonClicked(View v){
        finish();
    }
}
