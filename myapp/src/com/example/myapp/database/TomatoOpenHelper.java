package com.example.myapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by shizhao.czc on 2014/8/25.
 */
public class TomatoOpenHelper extends OpenHelper {
    public static final String DATABASE_NAME = "myDatabase.db";
    public static final int DATABASE_VERSION = 2;

    public TomatoOpenHelper(Context context) {
        super(context, DATABASE_NAME, DATABASE_VERSION, EventColumns.class);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
