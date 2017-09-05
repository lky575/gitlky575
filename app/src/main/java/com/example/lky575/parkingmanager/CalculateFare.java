package com.example.lky575.parkingmanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class CalculateFare extends AppCompatActivity {

    private EditText edtCarNumber, edtStartHour, edtStartMinute, edtStartSecond, edtEndHour, edtEndMinute , edtEndSecond;
    private TextView fareText;
    private int startHour, startMinute, startSecond, endHour, endMinute, endSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_fare);

        edtCarNumber = (EditText) findViewById(R.id.Edt_carNumber);
        edtStartHour = (EditText) findViewById(R.id.Edt_startHour);
        edtStartMinute = (EditText) findViewById(R.id.Edt_startMinute);
        edtStartSecond = (EditText) findViewById(R.id.Edt_startSecond);
        edtEndHour = (EditText) findViewById(R.id.Edt_endHour);
        edtEndMinute = (EditText) findViewById(R.id.Edt_endMinute);
        edtEndSecond = (EditText) findViewById(R.id.Edt_endSecond);
        fareText = (TextView) findViewById(R.id.Fare);

        setLayoutAttrs();
        SharedPreferences pref = getSharedPreferences("sign", Activity.MODE_PRIVATE);
        String carNumber = pref.getString("CarNumber","");

        edtCarNumber.setText(carNumber);
        // 차량 번호가 등록되어 있다면 조건문 수행
        if(!carNumber.equals("")){
            int entered_at = new getDBdata().getEntered_at(edtCarNumber.getText().toString(), CalculateFare.this);
            if(entered_at != -1){
                // 여기서는 int 형인 entered_at 데이터를 받아서 getTime() 메소드로 보내주어 시간:분 형식으로 포멧한다.
                getTime(entered_at);
            }
            else{
                Toast.makeText(getApplicationContext(), "해당 차량을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                setCurrentTime();
            }
        }
    }

    public void onsearchButtonClicked(View v){
        // 위와 동일한 작업
        int entered_at = new getDBdata().getEntered_at(edtCarNumber.getText().toString(), CalculateFare.this);
        if(entered_at != -1){
            getTime(entered_at);
        }
        else{
            Toast.makeText(getApplicationContext(), "해당 차량을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            setCurrentTime();
        }
    }

    public void setCurrentTime(){
        Calendar now = Calendar.getInstance();
        edtEndHour.setText(String.valueOf(now.get(Calendar.HOUR_OF_DAY)));
        edtEndMinute.setText(String.valueOf(now.get(Calendar.MINUTE)));
        edtEndSecond.setText(String.valueOf(now.get(Calendar.SECOND)));
    }

    public void oncalculateButtonClicked(View v) {
        try {
            startHour = Integer.parseInt(edtStartHour.getText().toString());
            startMinute = Integer.parseInt(edtStartMinute.getText().toString());
            startSecond = Integer.parseInt(edtStartSecond.getText().toString());
            endHour = Integer.parseInt(edtEndHour.getText().toString());
            endMinute = Integer.parseInt(edtEndMinute.getText().toString());
            endSecond = Integer.parseInt(edtEndSecond.getText().toString());

            // 시간은 24시 이상이 될 수 없다. (24시 = 0시)
            if (startHour >= 24 || endHour >= 24) {
                Toast.makeText(getApplicationContext(), "시간을 잘못 입력 하셨습니다.", Toast.LENGTH_SHORT).show();
            }
            // 분은 60분 이상이 될 수 없다. (60분 = 0분)
            else if (startMinute >= 60 || endMinute >= 60) {
                Toast.makeText(getApplicationContext(), "분을 잘못 입력 하셨습니다.", Toast.LENGTH_SHORT).show();
            }
            // 초는 60초 이상이 될 수 없다. (60초 = 0초)
            else if (startSecond >= 60 || endSecond >= 60){
                Toast.makeText(getApplicationContext(), "초를 잘못 입력 하셨습니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                int gap_H = endHour - startHour;
                int gap_M = endMinute - startMinute;
                int gap_S = endSecond - startSecond;
                // 입차 시간이 출차 시간보다 늦을 순 없다.
                if (gap_H < 0) {
                    Toast.makeText(getApplicationContext(), "시간을 잘못 입력 하셨습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 입차 '분' 이 출차 '분' 보다 클 경우
                    if (gap_M < 0) {
                        gap_H--;
                        if(gap_H < 0){
                            Toast.makeText(getApplicationContext(), "시간을 잘못 입력 하셨습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        gap_M += 60;
                    }

                    // 입차 '초' 가 출차 '초' 보다 클 경우
                    if (gap_S < 0){
                        gap_M--;
                        gap_S += 60;
                    }

                    // 10초당 천원 계산
                    int totalSecond = (gap_H * 3600 + gap_M * 60 + gap_S) / 10;
                    int fare = totalSecond * 1000;
                    fareText.setText(fare + "원");
                }
            }
        } catch(Exception e){
            Toast.makeText(getApplicationContext(),"시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getTime(int millisec){
        // 서버로 부터 시간을 밀리세컨드로 받아서 '시간' 과 '분' 만 각각 출력
        long millisec_l = (long) millisec * 1000L;

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(millisec_l);
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);

        edtStartHour.setText(Integer.toString(hour));
        edtStartMinute.setText(Integer.toString(minute));
        edtStartSecond.setText(Integer.toString(second));

        // 출차 시간 텍스트는 현재시간으로 자동 완성한다.
        Calendar now = Calendar.getInstance();
        edtEndHour.setText(String.valueOf(now.get(Calendar.HOUR_OF_DAY)));
        edtEndMinute.setText(String.valueOf(now.get(Calendar.MINUTE)));
        edtEndSecond.setText(String.valueOf(now.get(Calendar.SECOND)));
    }

    public void setLayoutAttrs(){
        LinearLayout searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
        LinearLayout enterLayout = (LinearLayout) findViewById(R.id.enterLayout);
        LinearLayout exitLayout = (LinearLayout) findViewById(R.id.exitLayout);
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        LinearLayout.MarginLayoutParams searchLayoutParams = (LinearLayout.MarginLayoutParams) searchLayout.getLayoutParams();
        LinearLayout.MarginLayoutParams enterLayoutParams = (LinearLayout.MarginLayoutParams) enterLayout.getLayoutParams();
        LinearLayout.MarginLayoutParams exitLayoutParams = (LinearLayout.MarginLayoutParams) exitLayout.getLayoutParams();
        LinearLayout.MarginLayoutParams buttonLayoutParams = (LinearLayout.MarginLayoutParams) buttonLayout.getLayoutParams();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels;
        int margin = widthPixels / 50;

        searchLayoutParams.setMargins(margin, margin, margin, margin);
        enterLayoutParams.setMargins(margin, margin, margin, margin);
        exitLayoutParams.setMargins(margin, margin, margin, margin);
        buttonLayoutParams.setMargins(margin, margin, margin, margin);

        edtCarNumber.setPadding(margin * 2, 0, 0, 0);
        edtStartHour.setPadding(0, 0, margin, 0);
        edtStartMinute.setPadding(0, 0, margin, 0);
        edtStartSecond.setPadding(0, 0, margin, 0);
        edtEndHour.setPadding(0, 0, margin, 0);
        edtEndMinute.setPadding(0, 0, margin, 0);
        edtEndSecond.setPadding(0, 0, margin, 0);
        fareText.setPadding(0, 0, margin, 0);

        edtCarNumber.setTextSize(margin);
        edtStartHour.setTextSize(margin);
        edtStartMinute.setTextSize(margin);
        edtStartSecond.setTextSize(margin);
        edtEndHour.setTextSize(margin);
        edtEndMinute.setTextSize(margin);
        edtEndSecond.setTextSize(margin);
        fareText.setTextSize(margin * 2);
    }
}
