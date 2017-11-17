package com.example.lky575.parkingmanager;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by lky575 on 2017-08-01.
 * 가상머니 충전용 클래스
 */

public class OutputStreamConnector extends Thread {
    private String url_str;
    private int money;
    private int res_code;

    public OutputStreamConnector(String url_str, int money){
        try {
            this.url_str = URLEncoder.encode(url_str, "utf-8");
        } catch(UnsupportedEncodingException e){}
        this.money = money;
    }

    public void run(){
        try{
            URL url = new URL("http://13.124.74.249:3000/cars/" + url_str + "/charge_money");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null){
                conn.setConnectTimeout(10000);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("amount",money);
                } catch(JSONException e){}
                writer.write(jsonObject.toString());
                writer.close();
            }

            res_code = conn.getResponseCode();

            if(res_code == conn.HTTP_OK){
                Log.d("conn","OutputStream 요청 완료");
            }

            else{
                Log.d("conn","OutputStreamConnector : " + res_code + "\nOutputStream 요청 실패");
            }

            conn.disconnect();
        } catch(IOException e){}

    }

    public int getRes_code(){
        return res_code;
    }
}
