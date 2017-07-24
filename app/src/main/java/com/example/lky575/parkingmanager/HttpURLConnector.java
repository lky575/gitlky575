package com.example.lky575.parkingmanager;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
            String encode_url = URLEncoder.encode(url_str,"utf-8");
            URL url = new URL("http://13.124.74.249:3000/cars/" + encode_url);

            conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {
                conn.setDoInput(true);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
            }

            else{
                Log.d("conn","connect error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        try {
            int res_code = conn.getResponseCode();
            if (res_code == HttpURLConnection.HTTP_OK || res_code == 422) {
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

        } catch (IOException e) {
            Log.d("conn","IOException : " + e.getMessage());
            e.printStackTrace();
        }
        result = sb.toString();
        conn.disconnect();
    }

    public String getResult(){
        return result;
    }
}
