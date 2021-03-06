package com.example.lky575.parkingmanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by lky575 on 2017-07-31.
 */

public class LogAdapter extends CursorAdapter {

    private int textSize;

    public LogAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView entered_txt = (TextView) view.findViewById(R.id.entered_txt);
        TextView exited_txt = (TextView) view.findViewById(R.id.exited_txt);

        entered_txt.setTextSize(textSize);
        exited_txt.setTextSize(textSize);

        String entered_at = cursor.getString(cursor.getColumnIndex("entered_at"));
        String exited_at = cursor.getString(cursor.getColumnIndex("exited_at"));

        entered_txt.setText(entered_at);
        exited_txt.setText(exited_at);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.logview_adapter, parent, false);
        return v;
    }

    public void setTextSize(int size){
        textSize = size;
    }
}
