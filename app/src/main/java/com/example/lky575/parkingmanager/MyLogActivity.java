package com.example.lky575.parkingmanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.lky575.parkingmanager.R.id.listView;

public class MyLogActivity extends AppCompatActivity {
    private ParkingLog mylog;
    private ProgressDialogTask task;
    private ListView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_log);
        // SQLite 에서 등록된 차량 번호에 대한 입출입 로그를 출력할 리스트뷰
        logView = (ListView) findViewById(listView);
        SharedPreferences pref = getSharedPreferences("sign", Context.MODE_PRIVATE);
        String carNumber = pref.getString("CarNumber",null);

        task = new ProgressDialogTask(MyLogActivity.this);
        task.execute();

        // 서버로부터 해당 차량의 입출입 로그를 받아온다.
        HttpURLConnector conn = new HttpURLConnector("entering_logs?car_numbering=" + carNumber);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){}
        JSONParser parser = new JSONParser(conn.getResult());

        // parser_array() 메소드는 SharedPreferences 를 인자로 받아서
        // 가장 최근 갱신한 _id 값을 불러와 가장 최신 _id 값 까지 갱신한다.
        parser.parser_array(pref);

        // 갱신 되지 않는 최신 데이터들을 ArrayList 에 저장해둔다.
        ArrayList<Integer> entered_array = parser.getEntered_array();
        ArrayList<Integer> exited_array = parser.getExited_array();

        mylog = new ParkingLog(getApplicationContext(), "PARKING_LOG", null, 2);
        // 최신 데이터들을 SQLite 에 갱신한다.
        mylog.setLog(entered_array, exited_array, carNumber);

        // SQLite 에 저장된 로그중 해당 차량에 대한 로그들만 listView 에 출력한다.
        Cursor cursor = mylog.getLog(carNumber);
        LogAdapter logList = new LogAdapter(MyLogActivity.this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        logView.setAdapter(logList);

        task.setFinish();

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
                Cursor cursor = mylog.search(year, month + 1);
                if(cursor.getCount() == 0){
                    Toast.makeText(getApplicationContext(),"로그가 없습니다.",Toast.LENGTH_SHORT).show();
                }
                LogAdapter listViewAdapter = new LogAdapter(MyLogActivity.this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                logView.setAdapter(listViewAdapter);
            }
        }, year, month, day);


        datePickerDialog.getDatePicker().setMaxDate(now.getTimeInMillis());

        //최소치 설정 오류 해결필요
/*        Calendar min = Calendar.getInstance();
        min.set(Calendar.YEAR, 2000);
        datePickerDialog.getDatePicker().setMinDate(min.getTimeInMillis());*/


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

    public void onbackButtonClicked(View v){
        finish();
    }
}
