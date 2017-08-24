package com.example.lky575.parkingmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by lky575 on 2017-08-23.
 */

public class getDBdata {
    public int getEntered_at(String car_number, Context context){
        // 작업 전에 progressDialog 를 실행하고 작업 후 종료 해주어
        // 사용자로 하여금 응답에 시간이 걸리는 작업에 대해 작업중임을 알려준다.
        ProgressDialogTask task = new ProgressDialogTask(context);
        task.execute();

        // HttpURLConnector 생성자로 보내줄 url 중 한글 깨짐을 막기위해 인코딩 해준다.
        String encode_url = null;
        try{
            encode_url = URLEncoder.encode(car_number,"utf-8");
        } catch(UnsupportedEncodingException e){}

        // 인코딩된 url 주소와 함께 알맞는 String 을 조합하여 HttpURLConnector 를 실행한다.
        HttpURLConnector conn = new HttpURLConnector("cars/" + encode_url);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        String dbResult = conn.getResult();
        if(dbResult != null){
            // 서버로 부터 받은 JSON 형식의 String 값을 전달받아 상황에 맞는 데이터 형식으로 파싱한다.
            JSONParser parser = new JSONParser(dbResult);
            parser.parser();

            int entered_at = parser.getEntered_at();
            int error_code = parser.getErrorCode();
            // 차량을 찾지 못한경우
            if(error_code != 0){
                task.setFinish();
                return -1;
            }
            if(entered_at != -1){
                task.setFinish();
                return entered_at;
            }
            // 차량은 있으나 입차시간이 없는 경우
            else{
                task.setFinish();
                return -1;
            }
        }
        // 서버로부터 응답이 없는 경우
        else{
            task.setFinish();
            return -1;
        }
    }

    public int getMoney(String car_number) {
        String encode_url = null;
        try {
            encode_url = URLEncoder.encode(car_number, "utf-8");
        } catch(UnsupportedEncodingException e){}
        HttpURLConnector conn = new HttpURLConnector("cars/" + encode_url);
        conn.start();
        try {
            conn.join();
        } catch (InterruptedException e) {
        }
        String result = conn.getResult();
        Log.d("conn", result);
        if (result != null) {
            JSONParser parser = new JSONParser(result);
            parser.parser();
            return parser.getVirtual_money();
        }

        else {
            Log.d("conn", "서버로부터 응답이 없습니다.");
            return -1;
        }
    }

    public DBposition getPosition(String car_number, Context context){
        ProgressDialogTask task = new ProgressDialogTask(context);
        task.execute();
        String encode_url = null;
        try{
            encode_url = URLEncoder.encode(car_number, "utf-8");
        } catch(UnsupportedEncodingException e){}

        HttpURLConnector conn = new HttpURLConnector("cars/" + encode_url);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){}
        String dbResult = conn.getResult();
        if(dbResult != null){
            JSONParser parser = new JSONParser(dbResult);
            parser.parser();

            if(parser.isCarNumbering()){
                int floor = parser.getFloor();
                int zone_index = parser.getZone_index();
                String zone_name = parser.getZone_name();

                task.setFinish();
                return new DBposition(floor, zone_index, zone_name);
            }
            // 입차는 했으나 주차를 하지 않은 경우
            else{
                task.setFinish();
                return null;
            }
        }
        // 서버로부터 응답이 없는 경우
        else{
            task.setFinish();
            return null;
        }
    }

    public int getEmpty_Space(){
        HttpURLConnector conn = new HttpURLConnector("empty_places_count");
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){}
        String emptyStr = conn.getResult();
        if(emptyStr != null){
            JSONParser parser = new JSONParser(emptyStr);
            parser.parser();

            return parser.getEmpty_space();
        }
        else
            return -1;
    }

    public DBlog getLog(String car_number, Context context, SharedPreferences pref){
        ProgressDialogTask task = new ProgressDialogTask(context);
        task.execute();

        // 서버로부터 해당 차량의 입출입 로그를 받아온다.
        String encode_url = null;
        try {
            encode_url = URLEncoder.encode(car_number, "utf-8");
        } catch(UnsupportedEncodingException e){}
        HttpURLConnector conn = new HttpURLConnector("entering_logs/" + encode_url);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){}
        String result = conn.getResult();
        if(result != null) {
            JSONParser parser = new JSONParser(result);

            // parser_array() 메소드는 SharedPreferences 를 인자로 받아서
            // 가장 최근 갱신한 _id 값을 불러와 가장 최신 _id 값 까지 갱신한다.
            parser.parser_array(pref);

            // 갱신 되지 않는 최신 데이터들을 ArrayList 에 저장해둔다.
            ArrayList<Integer> entered_array = parser.getEntered_array();
            ArrayList<Integer> exited_array = parser.getExited_array();
            task.setFinish();
            return new DBlog(entered_array, exited_array);
        }
        else{
            task.setFinish();
            return null;
        }
    }
}
