package com.example.lky575.parkingmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Config extends AppCompatActivity {

    private SharedPreferences pref;
    private TextView numberText, setNumberText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        numberText = (TextView) findViewById(R.id.numberText);
        setNumberText = (TextView) findViewById(R.id.setNumberText);

        pref = getSharedPreferences("sign", Context.MODE_PRIVATE);
        setNumberText.setVisibility(View.INVISIBLE);
        String carNumber = pref.getString("CarNumber","");
        if(!carNumber.equals("")){
            setNumberText.setVisibility(View.VISIBLE);
            setNumberText.setText("등록된 차량 번호 : " + carNumber);
        }
    }

    public void onSettingButtonClicked(View v){


        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CarNumber",numberText.getText().toString());
        editor.commit();
        Toast.makeText(getApplicationContext(),"차량 번호가 등록 되었습니다.",Toast.LENGTH_SHORT).show();

    }

    public void onbackButtonClicked(View v){
        finish();
    }
}
