package com.example.lky575.parkingmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class aeroView extends AppCompatActivity {

    private WebView aeroView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aero_view);

        aeroView = (WebView) findViewById(R.id.aeroView);
        aeroView.setWebViewClient(new WebViewClient());
        aeroView.loadUrl("https://www.naver.com/");
    }

    public void onfloor1ButtonClicked(View v){
        aeroView.loadUrl("https://www.naver.com/");
    }
    public void onfloor2ButtonClicked(View v){
        aeroView.loadUrl("https://www.google.co.kr/");
    }
    public void onbackButtonClicked(View v){
        finish();
    }
}