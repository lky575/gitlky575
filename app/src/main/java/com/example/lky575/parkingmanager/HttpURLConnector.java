package com.example.lky575.parkingmanager;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lky575 on 2017-07-14.
 */

public class HttpURLConnector extends Thread{
    private String url_str;
    private HttpURLConnection conn;
    private String result;


    public HttpURLConnector(String url_str) {
        this.url_str = url_str;
    }

    public void run(){
        try {
            URL url = new URL("http://13.124.74.249:3000/" + url_str);

            conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {
                conn.setDoInput(true);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
            }

            else{
                Log.d("conn","HttpURLConnector() : connect error");
                result = null;
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        try {
            int res_code = conn.getResponseCode();
            if (res_code == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while (true) {
                    line = reader.readLine();
                    if (line == null)
                        break;
                    sb.append(line + "\n");
                }
                reader.close();
            }

            // 'cars/차량번호' 에서 해당 차량번호에 대한 정보가 없는 경우.
            else if (res_code == 422) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line = null;
                while (true) {
                    line = reader.readLine();
                    if (line == null)
                        break;
                    sb.append(line + "\n");
                }
                reader.close();
            }

            else{
                Log.d("conn", "HttpURLConnector() : " + res_code);
                return;
            }

        } catch (IOException e) {
            Log.d("conn","HttpURLConnector() : " + e.getMessage());
            e.printStackTrace();
        }
        result = sb.toString();
        conn.disconnect();
    }

    public String getResult(){
        return result;
    }
}
