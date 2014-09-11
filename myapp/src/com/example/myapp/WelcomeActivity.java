package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.io.Serializable;

/**
 * Created by czc on 2014/9/10.
 */
public class WelcomeActivity extends Activity {

    Handler handler = new Handler();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        Intent intent = new Intent(getApplication(),MainActivity.class);
        handler.postDelayed(new Loading(intent), 2000);
    }

    class Loading implements Runnable{
        Intent intent;

        Loading(Intent intent) {
            this.intent = intent;
        }

        @Override
        public void run() {
            startActivity(intent);
            WelcomeActivity.this.finish();
        }
    }
}