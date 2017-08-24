package com.example.lky575.parkingmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;

public class Config extends AppCompatActivity {

    private SharedPreferences pref;
    private TextView numberText, setNumberText;
    private String carNumber;
    private CheckBox service_on;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        serviceIntent = new Intent(Config.this, FareService.class);
        pref = getSharedPreferences("sign", Context.MODE_PRIVATE);

        numberText = (TextView) findViewById(R.id.numberText);
        setNumberText = (TextView) findViewById(R.id.setNumberText);
        service_on = (CheckBox) findViewById(R.id.checkBox);

        service_on.setChecked(pref.getBoolean("service_on",false));
        service_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("service_on", true);
                    editor.commit();
                    startService(serviceIntent);
                }
                else{
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("service_on", false);
                    editor.commit();
                    stopService(serviceIntent);
                }
            }
        });

        setNumberText.setVisibility(View.INVISIBLE);
        carNumber = pref.getString("CarNumber","");
        // 차량번호가 등록되어 있는 경우
        if(!carNumber.equals("")){
            setNumberText.setVisibility(View.VISIBLE);
            setNumberText.setText("등록된 차량 번호 : " + carNumber);
        }

        if(getIntent().getBooleanExtra("NFmessage", false)){
            oncashButtonClicked(null);
        }

    }

    public void onSettingButtonClicked(View v){
        // 차량 번호 등록
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CarNumber",numberText.getText().toString());
        editor.commit();
        Toast.makeText(getApplicationContext(),"차량 번호가 등록 되었습니다.",Toast.LENGTH_SHORT).show();

        setNumberText.setVisibility(View.VISIBLE);
        setNumberText.setText("등록된 차량 번호 : " + carNumber);
    }

    public void onbackButtonClicked(View v){
        finish();
    }

    public void onlogButtonClicked(View v){
        // 로그 확인 버튼
        if(carNumber.equals("")){
            Toast.makeText(getApplicationContext(),"등록된 차량이 없습니다.",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), MyLogActivity.class);
            startActivity(intent);
        }
    }

    public void oninquiryButtonClicked(View v){
        Intent inquiry_call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-5049-8654"));
        startActivity(inquiry_call);
    }


    public void oncashButtonClicked(View v){
        // 충전용 다이얼로그
        if(carNumber == "") {
            Toast.makeText(getApplicationContext(), "차량 등록부터 해주세요.", Toast.LENGTH_SHORT).show();
        }
        else{
            int virtual_money = new getDBdata().getMoney(carNumber);
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View textEntryView = layoutInflater.inflate(R.layout.cash_input_dialog,null);
            TextView currentCash = (TextView) textEntryView.findViewById(R.id.currentCash);
            currentCash.setText("현재 충전된 금액 : " + virtual_money + "원");
            AlertDialog.Builder cashInputDialogBuilder = new AlertDialog.Builder(Config.this);
            cashInputDialogBuilder.setMessage("충전할 금액을 입력하세요.");
            cashInputDialogBuilder.setView(textEntryView);
            cashInputDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText testText = (EditText) textEntryView.findViewById(R.id.cashInputText);
                    int inputCash = Integer.parseInt(testText.getText().toString());
                    OutputStreamConnector conn = new OutputStreamConnector(carNumber, inputCash);
                    conn.start();
                    try{
                        conn.join();
                    } catch(InterruptedException e){}
                    if(conn.getRes_code() != HttpURLConnection.HTTP_OK){
                        Toast.makeText(getApplicationContext(), "충전에 실패 하였습니다.\n고객센터에 문의 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("NFmessage",false);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), inputCash + "원 충전이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            cashInputDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 취소 버튼
                }
            });

            final AlertDialog cashInputDialog = cashInputDialogBuilder.create();
            cashInputDialog.show();
        }
    }

}
