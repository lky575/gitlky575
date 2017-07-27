package com.example.lky575.parkingmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        db.execSQL("CREATE TABLE PARKING_LOG(no INTEGER PRIMARY KEY AUTOINCREMENT, start_at TEXT, end_at TEXT);");
    }

    // onUpgrade() 메소드는 데이터베이스는 존재하지만 버전이 다른경우 호출된다.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void execQuery(String query){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }

    public int getNO(String query){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    public String getStarted_at(String query){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToNext();
        return cursor.getString(0);
    }

    //사용 안할 예정
/*    public String getAvgTime(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> start_at = new ArrayList<>();
        ArrayList<String> end_at = new ArrayList<>();

        int sumTime = 0;
        int count;

        Cursor cursor = db.rawQuery("select * from PARKING_LOG", null);
        while(cursor.moveToNext()){
            start_at.add(cursor.getString(1));
            end_at.add(cursor.getString(2));
        }

        count = start_at.size();

        for(int i = 0 ; i < count; i++){
            StringTokenizer start_token = new StringTokenizer(start_at.get(i));
            StringTokenizer end_token = new StringTokenizer(end_at.get(i));

            int start_H = Integer.parseInt(start_token.nextToken());
            int end_H = Integer.parseInt(end_token.nextToken());
            int start_M = Integer.parseInt(start_token.nextToken());
            int end_M = Integer.parseInt(end_token.nextToken());

            int gap_H = end_H - start_H;
            int gap_M = end_M - start_M;
            if(gap_M < 0){
                gap_H--;
                gap_M += 60;
            }
            sumTime += (gap_H * 60) + gap_M;
        }

        int avgTime = sumTime / count;
        return (avgTime / 60) + "시간 " + (avgTime % 60) + "분";
    }*/

    public String PrintData() {
        SQLiteDatabase db = getReadableDatabase();
        String dbData = "";
        // Cursor rawQuery(String sql, String[] selectionArgs) : SELECT 쿼리를 실행할때 사용한다.
        // Cursor 객체는 쿼리에 의하여 생성된 행들을 가리키고, 결과를 순회하여 읽는데 유용하다.
        Cursor cursor = db.rawQuery("select * from PARKING_LOG", null);
        while(cursor.moveToNext()){
            dbData += "입차 : "
                    + cursor.getString(1) // start_at 행
                    + "  출차 : "
                    + cursor.getString(2) // end_at 행
                    + "\n";
        }

        return dbData;
    }


}
