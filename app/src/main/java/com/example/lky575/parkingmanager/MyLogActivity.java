package com.example.lky575.parkingmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MyLogActivity extends AppCompatActivity {
    private ParkingLog mylog;
    private ProgressDialogTask task;
    private ListView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_log);
        // SQLite 에서 등록된 차량 번호에 대한 입출입 로그를 출력할 리스트뷰
        logView = (ListView) findViewById(R.id.listView);
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

    public void onbackButtonClicked(View v){
        finish();
    }
}
