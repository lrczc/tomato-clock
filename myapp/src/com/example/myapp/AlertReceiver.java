package com.example.myapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.myapp.database.Database;
import com.example.myapp.database.TomatoOpenHelper;

/**
 * Created by shizhao.czc on 2014/9/1.
 */
public class AlertReceiver extends BroadcastReceiver {

    public static final String DAILY_ALERT_ACTION = "com.example.myapp.daily_alert";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DAILY_ALERT_ACTION)) {
            TomatoOpenHelper tomatoOpenHelper = new TomatoOpenHelper(context);
            Database db = new Database(tomatoOpenHelper);
            if (!db.existTodayEvent())
                return;
            NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent1 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);

            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker(context.getString(R.string.alert_title))
                    .setContentInfo(context.getString(R.string.alert_message))
                    .setContentTitle(context.getString(R.string.alert_title))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .build();
            mManager.notify(0, notification);
        }
    }
}
