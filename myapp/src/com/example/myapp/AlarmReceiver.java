package com.example.myapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.widget.Toast;

/**
 * Created by shizhao.czc on 2014/8/27.
 */
public class AlarmReceiver extends BroadcastReceiver {
    SoundPool sp;
    int soundId;

    public static final String ALARM_ALERT_ACTION = "com.example.myapp.Alarm_Alert";
    @Override
    public void onReceive(Context context, Intent intent) {
        int soundRes = intent.getIntExtra("sound_res", R.raw.sound1);
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1, 1, 0, 5, 1);
            }
        });
        soundId = sp.load(context, soundRes, 1);
        Toast.makeText(context, R.string.mission_complete, Toast.LENGTH_SHORT).show();
    }
}
