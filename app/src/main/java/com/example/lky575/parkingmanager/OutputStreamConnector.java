package com.example.lky575.parkingmanager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lky575 on 2017-08-01.
 */

public class OutputStreamConnector extends Thread {
    private String url_str;

    public OutputStreamConnector(String url_str){
        this.url_str = url_str;
    }

    public void run(){
        try{
            URL url = new URL(url_str);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null){
                conn.setConnectTimeout(10000);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
            }
            int res_code = conn.getResponseCode();

            if(res_code == conn.HTTP_OK){
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write("충전 데이터 입력");
                writer.close();
            }

            conn.disconnect();
        } catch(IOException e){}

    }
}
