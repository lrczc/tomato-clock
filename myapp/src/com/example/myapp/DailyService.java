package com.example.myapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by shizhao.czc on 2014/9/1.
 */
public class DailyService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
