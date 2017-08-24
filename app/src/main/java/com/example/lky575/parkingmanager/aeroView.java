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
        // 새로운 웹뷰 클라이언트를 지정해주어야 새창이 뜨지 않는다.
        aeroView.setWebViewClient(new WebViewClient());
        // 서버에서 조감도를 띄워주는 url 을 웹뷰에 출력한다.
        aeroView.loadUrl("http://13.124.74.249:3000/places/dashboard?floor=1");
    }

    public void onbackButtonClicked(View v){
        finish();
    }
}
