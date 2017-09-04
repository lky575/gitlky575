package com.example.lky575.parkingmanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MyCarPosition extends AppCompatActivity {

    private EditText numberText;
    private SharedPreferences pref;
    private WebView positionView;
    private ImageView defaultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car_position);

        positionView = (WebView) findViewById(R.id.positionView);
        defaultView = (ImageView) findViewById(R.id.defaultView);
        numberText = (EditText) findViewById(R.id.Edt_carNumber);

        defaultView.setVisibility(View.VISIBLE);
        positionView.setVisibility(View.GONE);

        pref = getSharedPreferences("sign", Activity.MODE_PRIVATE);
        String number = pref.getString("CarNumber","");
        numberText.setText(number);

        setLayoutAttrs();
    }

    public void onsearchButtonClicked(View v){
        String number = numberText.getText().toString();

        DBposition position = new getDBdata().getPosition(number, MyCarPosition.this);

        if(position != null){
            defaultView.setVisibility(View.GONE);
            positionView.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), position.floor + "층" + position.zone_name + position.zone_index, Toast.LENGTH_LONG).show();
        }

        else{
            defaultView.setVisibility(View.VISIBLE);
            positionView.setVisibility(View.GONE);
            Toast.makeText(getApplication(),"해당 차량을 찾을 수 없습니다.",Toast.LENGTH_LONG).show();
        }
    }

    public void setLayoutAttrs(){
        EditText Edt_carNumber = (EditText) findViewById(R.id.Edt_carNumber);
        LinearLayout viewLayout = (LinearLayout) findViewById(R.id.viewLayout);
        LinearLayout searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
        ImageView defaultView = (ImageView) findViewById(R.id.defaultView);
        WebView positionView = (WebView) findViewById(R.id.positionView);

        LinearLayout.LayoutParams viewLayoutParams = (LinearLayout.LayoutParams) viewLayout.getLayoutParams();
        LinearLayout.LayoutParams searchLayoutParams = (LinearLayout.LayoutParams) searchLayout.getLayoutParams();
        ViewGroup.MarginLayoutParams defaultViewParams = (ViewGroup.MarginLayoutParams) defaultView.getLayoutParams();
        ViewGroup.MarginLayoutParams positionViewParams = (ViewGroup.MarginLayoutParams) positionView.getLayoutParams();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels;
        int margin = widthPixels / 50;

        Edt_carNumber.setTextSize(widthPixels / 50);
        Edt_carNumber.setPadding(margin, 0, 0, 0);
        viewLayoutParams.setMargins(margin, margin, margin, margin);
        searchLayoutParams.setMargins(margin, margin, margin, margin);
        defaultViewParams.setMargins(margin * 5, margin * 10, margin * 5, margin * 10);
        positionViewParams.setMargins(margin, margin, margin, margin);
    }
}
