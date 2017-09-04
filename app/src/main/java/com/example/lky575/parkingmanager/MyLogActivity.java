package com.example.lky575.parkingmanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Calendar;

import static com.example.lky575.parkingmanager.R.id.listView;

public class MyLogActivity extends AppCompatActivity {
    private ParkingLog mylog;
    private ListView logView;
    private String carNumber;
    private int textSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_log);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels;
        int margin = widthPixels / 25;
        textSize = widthPixels / 50;

        // SQLite 에서 등록된 차량 번호에 대한 입출입 로그를 출력할 리스트뷰
        logView = (ListView) findViewById(listView);
        ViewGroup.MarginLayoutParams logViewParams = (ViewGroup.MarginLayoutParams) logView.getLayoutParams();
        logViewParams.setMargins(margin, margin * 2, margin, margin * 2);
        SharedPreferences pref = getSharedPreferences("sign", Context.MODE_PRIVATE);
        carNumber = pref.getString("CarNumber", "");

        mylog = new ParkingLog(getApplicationContext(), "PARKING_LOG", null, 2);

        DBlog logData = new getDBdata().getLog(carNumber, MyLogActivity.this, pref);

        if (logData != null) {
            // 최신 데이터들을 SQLite 에 갱신한다.
            mylog.setLog(logData.entered_array, logData.exited_array, carNumber);

            // SQLite 에 저장된 로그중 해당 차량에 대한 로그들만 listView 에 출력한다.
            Cursor cursor = mylog.getLog(carNumber);
            LogAdapter logList = new LogAdapter(MyLogActivity.this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            logList.setTextSize(widthPixels / 50);
            logView.setAdapter(logList);
        }
        else{
            Toast.makeText(getApplicationContext(),"서버로 부터 결과를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
        }
    }

    public void oncalendarButtonClicked(View v){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        Context context = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog);

/*        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            // API 24 이상일 경우 시스템 기본 테마 사용.
            context = this;
        }*/


        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Cursor cursor = mylog.search(year, month + 1, carNumber);
                if(cursor.getCount() == 0){
                    Toast.makeText(getApplicationContext(),"로그가 없습니다.",Toast.LENGTH_SHORT).show();
                }
                LogAdapter logList = new LogAdapter(MyLogActivity.this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                logList.setTextSize(textSize);
                logView.setAdapter(logList);
            }
        }, year, month, day);

        Calendar min = Calendar.getInstance();
        min.set(Calendar.YEAR, 2000);
        min.set(Calendar.MONTH, 0);
        min.set(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMinDate(min.getTimeInMillis());

        datePickerDialog.getDatePicker().setMaxDate(now.getTimeInMillis());

        // datePickerDialog 년,월 만 선택 가능
        try {
            Field[] fields = datePickerDialog.getClass().getDeclaredFields();
            for (Field dateField : fields) {
                if (dateField.getName().equals("mDatePicker")) {
                    dateField.setAccessible(true);

                    DatePicker datePicker = (DatePicker) dateField.get(datePickerDialog);
                    Field[] datePickerFields = dateField.getType().getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if (android.os.Build.VERSION.SDK_INT >= 21) {
                            int daySpinnerId = Resources.getSystem()
                                    .getIdentifier("day", "id", "android");
                            if (daySpinnerId != 0) {
                                View daySpinner = datePicker
                                        .findViewById(daySpinnerId);
                                if (daySpinner != null) {
                                    daySpinner.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            if ("mDaySpinner".equals(datePickerField.getName())) {
                                datePickerField.setAccessible(true);
                                Object dayPicker = new Object();
                                dayPicker = datePickerField.get(datePicker);
                                ((View) dayPicker).setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        }catch(IllegalAccessException e){}

        datePickerDialog.show();
    }

    public void onrefreshButtonClicked(View v){
        Cursor cursor = mylog.getLog(carNumber);
        LogAdapter logList = new LogAdapter(MyLogActivity.this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        logList.setTextSize(textSize);
        logView.setAdapter(logList);
    }
}
