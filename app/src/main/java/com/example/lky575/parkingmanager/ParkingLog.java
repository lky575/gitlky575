package com.example.lky575.parkingmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by lky575 on 2017-07-26.
 */

public class ParkingLog extends SQLiteOpenHelper {
    // ParkingLog 클래스 생성자로 DB 이름과 버전을 넘겨 받는다.
    public ParkingLog(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // onCreate() 메소드는 넘겨 받은 이름과 버전의 데이터베이스가 없는 경우 한번 호출 된다.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // void execSQL(String sql) : SELECT 명령을 제외한 모든 SQL 문장을 실행한다.
        db.execSQL("CREATE TABLE PARKING_LOG(_id INTEGER PRIMARY KEY AUTOINCREMENT, car_number TEXT, entered_at TEXT, exited_at TEXT);");    }

    // onUpgrade() 메소드는 데이터베이스는 존재하지만 버전이 다른경우 호출된다.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PARKING_LOG");
        onCreate(db);
    }

    public void setLog(ArrayList<Integer> entered_array,ArrayList<Integer> ended_array, String car_number){
        SQLiteDatabase db = getWritableDatabase();
        for(int i = 0 ; i < entered_array.size(); i++){
            String pattern = "yyyy-MM-dd HH:mm";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            String entered_date = formatter.format(entered_array.get(i));
            String exited_date = formatter.format(ended_array.get(i));

            db.execSQL("insert into PARKING_LOG values(null, " + car_number + ", "
                    + entered_date + ", " + exited_date + ");");

            // (null, '88허1234', '2017-06-11 11:33', '2017-06-11 12:33');
        }
    }

    public Cursor getLog(String car_number){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("select * from PARKING_LOG where car_number = " + car_number + ";", null);
    }

    public void execQuery(String query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }
}
