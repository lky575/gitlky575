package com.example.lky575.parkingmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by lky575 on 2017-07-24.
 */

public class ProgressDialogTask extends AsyncTask<Void,Void,Void> {
    private ProgressDialog waitDialog;
    private Context context;
    private boolean finish;

    public ProgressDialogTask(Context context){
        this.context = context;
        finish = false;

        waitDialog = new ProgressDialog(context);
        waitDialog.setMessage("잠시만 기다려 주세요");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        waitDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        waitDialog.dismiss();
    }

    @Override
    protected Void doInBackground(Void... params) {
        while(!finish){
            try{
                Thread.sleep(100);
            }catch(InterruptedException e){}
        }
        return null;
    }

    public void setFinish(){
        finish = true;
    }
}
