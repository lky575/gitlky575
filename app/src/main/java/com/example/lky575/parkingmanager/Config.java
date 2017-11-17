package com.example.lky575.parkingmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;

public class Config extends AppCompatActivity {

    private SharedPreferences pref;
    private TextView numberText, setNumberText;
    private String carNumber;
    private Intent serviceIntent;
    private ImageView alarmOn, alarmOff;
    private int textSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        serviceIntent = new Intent(Config.this, FareService.class);
        pref = getSharedPreferences("sign", Context.MODE_PRIVATE);

        numberText = (TextView) findViewById(R.id.numberText);
        setNumberText = (TextView) findViewById(R.id.setNumberText);
        alarmOn = (ImageView) findViewById(R.id.alarmOn);
        alarmOff = (ImageView) findViewById(R.id.alarmOff);

        setLayoutAttrs();

        if(pref.getBoolean("service_on", false)){
            alarmOn.setVisibility(View.VISIBLE);
            alarmOff.setVisibility(View.GONE);
        }

        else{
            alarmOn.setVisibility(View.GONE);
            alarmOff.setVisibility(View.VISIBLE);
        }

        setNumberText.setVisibility(View.INVISIBLE);
        carNumber = pref.getString("CarNumber","");
        // 차량번호가 등록되어 있는 경우
        if(!carNumber.equals("")){
            setNumberText.setVisibility(View.VISIBLE);
            setNumberText.setText("등록 번호\n\n" + carNumber);
        }

        if(getIntent().getBooleanExtra("NFmessage", false)){
            oncashButtonClicked(null);
        }

    }

    public void setLayoutAttrs(){
        LinearLayout searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
        FrameLayout alarmLayout = (FrameLayout) findViewById(R.id.alarmLayout);
        LinearLayout.MarginLayoutParams searchLayoutParams = (LinearLayout.MarginLayoutParams) searchLayout.getLayoutParams();
        LinearLayout.MarginLayoutParams alarmLayoutParams = (LinearLayout.MarginLayoutParams) alarmLayout.getLayoutParams();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels;
        int margin = widthPixels / 50;
        textSize = widthPixels / 50;
        numberText.setTextSize(textSize);
        numberText.setPadding(textSize ,0, 0, 0);
        setNumberText.setTextSize(textSize);
        setNumberText.setPadding(0, 0, 0, textSize * 4);
        searchLayoutParams.setMargins(margin, margin, margin, margin);
        alarmLayoutParams.setMargins(margin, margin, margin, margin);
    }

    public void onAlarmOnButtonClicked(View v){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("service_on", true);
        editor.commit();
        alarmOn.setVisibility(View.VISIBLE);
        alarmOff.setVisibility(View.GONE);
        startService(serviceIntent);
    }

    public void onAlarmOffButtonClicked(View v){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("service_on", false);
        editor.commit();
        alarmOn.setVisibility(View.GONE);
        alarmOff.setVisibility(View.VISIBLE);
        stopService(serviceIntent);
    }

    public void onSettingButtonClicked(View v){
        // 차량 번호 등록
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CarNumber",numberText.getText().toString());
        editor.commit();
        Toast.makeText(getApplicationContext(),"차량 번호가 등록 되었습니다.",Toast.LENGTH_SHORT).show();

        setNumberText.setVisibility(View.VISIBLE);
        setNumberText.setText("등록 번호\n\n" + pref.getString("CarNumber",""));
    }

    public void onlogButtonClicked(View v){
        // 로그 확인 버튼
        carNumber = pref.getString("CarNumber", "");
        if(carNumber.equals("")){
            Toast.makeText(getApplicationContext(),"등록된 차량이 없습니다.",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), MyLogActivity.class);
            startActivity(intent);
        }
    }

    public void oninquiryButtonClicked(View v){
        Intent inquiry_call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-0000-0000"));
        startActivity(inquiry_call);
    }


    public void oncashButtonClicked(View v){
        // 충전용 다이얼로그
        carNumber = pref.getString("CarNumber", "");
        if(carNumber == "") {
            Toast.makeText(getApplicationContext(), "차량 등록부터 해주세요.", Toast.LENGTH_SHORT).show();
        }
        else{
            int virtual_money = new getDBdata().getMoney(carNumber);
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View textEntryView = layoutInflater.inflate(R.layout.cash_input_dialog,null);

            final TextView currentMoney = (TextView) textEntryView.findViewById(R.id.currentCash);
            final EditText chargeMoney = (EditText) textEntryView.findViewById(R.id.cashInputText);

            chargeMoney.setTextSize(textSize);
            currentMoney.setTextSize(textSize);

            currentMoney.setText(" 현재 충전된 금액 : " + virtual_money + "원");
            AlertDialog.Builder cashInputDialogBuilder = new AlertDialog.Builder(Config.this);
            cashInputDialogBuilder.setTitle("충전할 금액을 입력하세요.");
            cashInputDialogBuilder.setView(textEntryView);
            cashInputDialogBuilder.setIcon(R.drawable.coin);
            cashInputDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int inputCash = 0;
                    try{
                        inputCash = Integer.parseInt(chargeMoney.getText().toString());
                    } catch(NumberFormatException e){}

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
