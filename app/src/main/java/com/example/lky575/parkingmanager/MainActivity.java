package com.example.lky575.parkingmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView currentText, totalText;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentText = (TextView) findViewById(R.id.currentText);
        totalText = (TextView) findViewById(R.id.totalText);

        pref = getSharedPreferences("sign", Context.MODE_PRIVATE);
        boolean hasVisited = pref.getBoolean("hasVisited",false);
        if(!hasVisited){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("환영합니다.");
            builder.setMessage("차량을 등록하시겠습니까?");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), Config.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("hasVisited",true);
            editor.commit();
        }
    }

    public void onParkingButtonClicked(View v){ // 조감도 버튼
        Intent intent = new Intent(getApplicationContext(),aeroView.class);
        startActivity(intent);
    }

    public void onSearchButtonClicked(View v){ // 내 차 위치 찾기 버튼
        Intent intent = new Intent(getApplicationContext(),MyCarPosition.class);
        startActivity(intent);
    }

    public void onChargeButtonClicked(View v){ // 주차요금 버튼
        Intent intent = new Intent(getApplicationContext(),CalculateFare.class);
        startActivity(intent);
    }

    public void onConfigButtonClicked(View v){ // 내 차 정보 버튼
        Intent intent = new Intent(getApplicationContext(), Config.class);
        startActivity(intent);
    }
}
