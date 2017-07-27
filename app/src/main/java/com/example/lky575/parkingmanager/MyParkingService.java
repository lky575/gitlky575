package com.example.lky575.parkingmanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import java.text.SimpleDateFormat;

public class MyParkingService extends Service {
    private SharedPreferences pref;
    private String carNumber;
    private HttpURLConnector conn;
    private boolean isParking;
    private int currentNO = 0;


    public MyParkingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pref = getSharedPreferences("sign", Context.MODE_PRIVATE);
        carNumber = pref.getString("CarNumber","");
        isParking = false;
        conn = new HttpURLConnector(carNumber);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new serviceThread().start();

        return super.onStartCommand(intent, flags, startId);
    }

    public class serviceThread extends Thread{
        public void run(){
            while(!isParking) {
                conn.start();
                try {
                    conn.join();
                } catch (InterruptedException e) {
                }
                String dbResult = conn.getResult();
                JSONParser parser = new JSONParser(dbResult);
                parser.parser();
                if (parser.isCarNumbering()) {
                    int start_at = parser.getStarted_at();
                    String pattern = "HHmmss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                    String date = formatter.format(start_at);
                    ParkingLog parkingLog = new ParkingLog(getApplicationContext(),"PARKING_LOG",null,1);
                    String recentLog = parkingLog.getStarted_at("select start_at from PARKING_LOG where no = " + currentNO + ";");
                    if(!recentLog.equals(date)) {
                        parkingLog.execQuery("insert into PARKING_LOG values (null," + date + ",null);");
                        currentNO = parkingLog.getNO("select no from PARKING_LOG where start_at = " + date + ";");

                        isParking = true;
                    }
                }
                try{
                    Thread.sleep(10000);
                } catch(InterruptedException e){}
            }
            while(isParking){
                conn.start();
                try {
                    conn.join();
                } catch (InterruptedException e) {
                }
                String dbResult = conn.getResult();
                JSONParser parser = new JSONParser(dbResult);
                parser.parser();
                if (!parser.isCarNumbering()) {
                    // 출차시간 로그 찍기
                    int end_at = parser.getStarted_at(); // getEnd_at() 메소드 구현후 수정 필요
                    String pattern = "HHmmss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                    String date = formatter.format(end_at);
                    ParkingLog parkingLog = new ParkingLog(getApplicationContext(),"PARKING_LOG",null,1);
                    parkingLog.execQuery("update PARKING_LOG set end_at = " + end_at + "where no = " + currentNO + ";");
                    isParking = false;
                }
                try{
                    Thread.sleep(10000);
                } catch(InterruptedException e){}
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
