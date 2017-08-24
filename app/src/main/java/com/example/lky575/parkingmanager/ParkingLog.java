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
            String entered_date = formatter.format(entered_array.get(i) * 1000L);
            String exited_date = formatter.format(ended_array.get(i) * 1000L);

            db.execSQL("insert into PARKING_LOG values(null, '" + car_number + "', '"
                    + entered_date + "', '" + exited_date + "');");
        }
    }

    public Cursor getLog(String car_number){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from PARKING_LOG where car_number = '" + car_number + "' ORDER BY entered_at desc;", null);
        return cursor;
    }

    public Cursor search(int year, int month, String carNumber){
        SQLiteDatabase db = getReadableDatabase();
        String month_str;
        String year_str = Integer.toString(year);
        if(month < 10){
            month_str = "0";
            month_str += Integer.toString(month);
        }
        else{
            month_str = Integer.toString(month);
        }
        String[] args = {carNumber, year_str, month_str};
        Cursor cursor = db.rawQuery("select * from PARKING_LOG where car_number = ? AND substr(entered_at, 1, 4) = ? and substr(entered_at, 6, 2) = ? ORDER BY entered_at desc;", args);

        return cursor;
    }
}
