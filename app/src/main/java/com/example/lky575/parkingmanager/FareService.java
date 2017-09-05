package com.example.lky575.parkingmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class FareService extends Service {
    private boolean isRun;
    private SharedPreferences pref;
    public FareService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pref = getSharedPreferences("sign", Context.MODE_PRIVATE);
        isRun = true;
        new fareThread().start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class fareThread extends Thread {
        public void run() {
            while (isRun) {
                int count = 0;
                Log.d("conn", "fareThread() 실행중");
                String carNumber = pref.getString("CarNumber", "");
                if (carNumber != "") {
                    int virtual_money = new getDBdata().getMoney(carNumber);
                    // 가상머니가 -1 인 경우는 해당 차량이 없거나 서버로부터 데이터를 받지 못하는 상황이다
                    // 또는 해당 차량
                    if (virtual_money != -1) {
                        if (virtual_money < 2000) {
                            MakeNotification();
                            while(pref.getBoolean("NFmessage", false)){
                                try{
                                    Log.d("conn", "가상머니 충전중...");
                                    Thread.sleep(2000);
                                } catch(InterruptedException e){}
                                if(count++ >= 100)
                                    break;
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRun = false;
    }

    public void MakeNotification(){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("NFmessage", true);
        editor.commit();
        Resources res = getResources();
        Intent intent = new Intent(this, Config.class);
        intent.putExtra("NFmessage", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle("모두의 주차")
                .setContentText("요금이 얼마 남지 않았습니다.")
                .setTicker("요금이 얼마 남지 않았습니다.")
                .setSmallIcon(R.drawable.park_64)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.park_64))
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mbuilder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1000, mbuilder.build());
    }
}