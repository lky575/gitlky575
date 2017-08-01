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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Config extends AppCompatActivity {

    private SharedPreferences pref;
    private TextView numberText, setNumberText;
    private String carNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        numberText = (TextView) findViewById(R.id.numberText);
        setNumberText = (TextView) findViewById(R.id.setNumberText);

        pref = getSharedPreferences("sign", Context.MODE_PRIVATE);
        setNumberText.setVisibility(View.INVISIBLE);
        carNumber = pref.getString("CarNumber","");
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
        // 충전용 다이얼로그 (서버 연동해서 변경 예정)
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View textEntryView = layoutInflater.inflate(R.layout.cash_input_dialog,null);
        TextView currentCash = (TextView) textEntryView.findViewById(R.id.currentCash);
        currentCash.setText("현재 충전된 금액 : " + pref.getInt("cash",0) + "원");
        AlertDialog.Builder cashInputDialogBuilder = new AlertDialog.Builder(Config.this);
        cashInputDialogBuilder.setMessage("충전할 금액을 입력하세요.");
        cashInputDialogBuilder.setView(textEntryView);
        cashInputDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText testText = (EditText) textEntryView.findViewById(R.id.cashInputText);
                int inputCash = Integer.parseInt(testText.getText().toString());
                int cash = pref.getInt("cash",0) + inputCash;
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("cash",cash);
                editor.commit();
                Toast.makeText(getApplicationContext(),inputCash + "원 충전이 완료 되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        cashInputDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog cashInputDialog = cashInputDialogBuilder.create();

        // 캐쉬 시스템 가입 다이얼로그
        if(!pref.getBoolean("cashSigned",false)) {
            AlertDialog.Builder signDialogBuilder = new AlertDialog.Builder(Config.this);
            signDialogBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("cashSigned",true);
                        editor.commit();
                        cashInputDialog.show();
                }
            });
            signDialogBuilder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            signDialogBuilder.setMessage("캐쉬 시스템에 가입 하시겠습니까?");
            AlertDialog signDialog = signDialogBuilder.create();
            signDialog.show();
        }

        else{
            cashInputDialog.show();
        }
    }

}
