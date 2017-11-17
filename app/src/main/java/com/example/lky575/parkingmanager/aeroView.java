package com.example.lky575.parkingmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class aeroView extends AppCompatActivity {

    private WebView aeroView;
    private WebSettings mWebSettings;
    private int count = 1;
    private final int MAXFLOOR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aero_view);

        aeroView = (WebView) findViewById(R.id.aeroView);
        // 새로운 웹뷰 클라이언트를 지정해주어야 새창이 뜨지 않는다.
        aeroView.setWebViewClient(new WebViewClient());
        mWebSettings = aeroView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        // 서버에서 조감도를 띄워주는 url 을 웹뷰에 출력한다.
        aeroView.loadUrl("http://13.124.74.249:3000/places/dashboard?floor=" + count);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels;
        int margin = widthPixels / 50;

        ViewGroup.MarginLayoutParams aeroViewParams = (ViewGroup.MarginLayoutParams) aeroView.getLayoutParams();
        aeroViewParams.setMargins(margin, margin, margin, margin);
    }

    public void onUpButtonClicked(View v){
        count++;
        if(count > MAXFLOOR){
            count = 1;
        }
        aeroView.loadUrl("http://13.124.74.249:3000/places/dashboard?floor=" + count);
    }

    public void onDownButtonClicked(View v){
        count--;
        if(count < 1){
            count = MAXFLOOR;
        }
        aeroView.loadUrl("http://13.124.74.249:3000/places/dashboard?floor=" + count);
    }
}
