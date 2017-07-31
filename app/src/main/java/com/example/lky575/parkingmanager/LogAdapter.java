package com.example.lky575.parkingmanager;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by lky575 on 2017-07-31.
 */

public class LogAdapter extends CursorAdapter {
    public LogAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }
}
