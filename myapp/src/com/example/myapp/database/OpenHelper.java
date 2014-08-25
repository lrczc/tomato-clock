package com.example.myapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by shizhao.czc on 2014/8/25.
 */
public class OpenHelper extends SQLiteOpenHelper {

    protected Class<?>[] mClazz;

    public OpenHelper(Context context, String name, int version,Class<?>... clazz) {
        super(context, name, null, version);
        mClazz = clazz;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Class<?> cls : mClazz) {
            Log.d("helper create", DBUtil.createTableSql(cls));
            db.execSQL(DBUtil.createTableSql(cls));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(Class<?> cls:mClazz){
            db.execSQL(DBUtil.dropTableSql(cls));
        }
        onCreate(db);
    }
}
