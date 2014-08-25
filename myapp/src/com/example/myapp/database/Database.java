package com.example.myapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

import com.example.myapp.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by shizhao.czc on 2014/8/25.
 */
public class Database {
    private SQLiteOpenHelper mOpenHelper;

    public Database(SQLiteOpenHelper mOpenHelper) {
        this.mOpenHelper = mOpenHelper;
    }

    public SQLiteDatabase getWritable() {
        return mOpenHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadable() {
        return mOpenHelper.getReadableDatabase();
    }


    public List<Event> getTodayEvent(String count) {
        String TABLE_NAME = EventColumn.class.getSimpleName();
        String[] eventColumns = new String[] {
                EventColumn._ID, EventColumn.EVENT_NAME, EventColumn.TIME, EventColumn.SOUND, EventColumn.CREATE_TIME
        };
        Date today =new Date(System.currentTimeMillis()/86400000*86400000-(23-Calendar.ZONE_OFFSET)*3600000);
        String selection = EventColumn.CREATE_TIME + ">=" + today.getTime();
        String orderBy = EventColumn.CREATE_TIME;
        String limit = " " + count;
        SQLiteDatabase db = getReadable();
        Cursor cursor = db.query(TABLE_NAME, eventColumns, selection, null, null, null, orderBy, limit);
        List<Event> events = new ArrayList<Event>();
        while (cursor.moveToNext()) {
            Event event = new Event();
            event.setEventID(cursor.getInt(0));
            event.setEventName(cursor.getString(1));
            event.setTime(cursor.getInt(2));
            event.setSound(cursor.getInt(3));
            event.setCreateTime(cursor.getInt(4));
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public void insertEvent(Event event) {
        String TABLE_NAME = EventColumn.class.getSimpleName();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventColumn.EVENT_NAME, event.getEventName());
        contentValues.put(EventColumn.TIME, event.getTime());
        contentValues.put(EventColumn.SOUND, event.getSound());
        contentValues.put(EventColumn.CREATE_TIME, event.getCreateTime());
        SQLiteDatabase db = getWritable();
        db.insert(TABLE_NAME, null, contentValues);
    }

}
