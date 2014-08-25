package com.example.myapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapp.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        String TABLE_NAME = EventColumns.class.getSimpleName();
        String[] eventColumns = new String[] {
                EventColumns._ID, EventColumns.EVENT_NAME, EventColumns.TIME, EventColumns.SOUND, EventColumns.PLAN_TIME, EventColumns.CREATE_TIME
        };
        long today = System.currentTimeMillis()/86400000*86400000-(23-Calendar.ZONE_OFFSET)*3600000;
        long tomorrow = System.currentTimeMillis()/86400000*86400000+(1+Calendar.ZONE_OFFSET)*3600000;
        //String selection = EventColumns.PLAN_TIME + " >= " + today;
        String selection = EventColumns.PLAN_TIME + " >= " + today + " AND " + EventColumns.PLAN_TIME + "<" + tomorrow;
        String orderBy = EventColumns.CREATE_TIME;
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

    public long insertEvent(Event event) {
        String TABLE_NAME = EventColumns.class.getSimpleName();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventColumns.EVENT_NAME, event.getEventName());
        contentValues.put(EventColumns.TIME, event.getTime());
        contentValues.put(EventColumns.SOUND, event.getSound());
        contentValues.put(EventColumns.PLAN_TIME, event.getPlanTime());
        contentValues.put(EventColumns.CREATE_TIME, event.getCreateTime());
        SQLiteDatabase db = getWritable();
        return db.insert(TABLE_NAME, null, contentValues);
    }

    public void deleteEvent(Event event) {
        String whereClause = EventColumns._ID + "=?";
        String[] whereArgs = {String.valueOf(event.getEventID())};
        SQLiteDatabase db = getWritable();
        db.delete(EventColumns.class.getSimpleName(), whereClause, whereArgs);
    }

    public void updateEvent(Event event) {
        String where = EventColumns._ID + "= ? ";
        String[] args = {String.valueOf(event.getEventID())};
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventColumns.EVENT_NAME, event.getEventName());
        contentValues.put(EventColumns.TIME, event.getTime());
        contentValues.put(EventColumns.SOUND, event.getSound());
        contentValues.put(EventColumns.PLAN_TIME, event.getPlanTime());
        contentValues.put(EventColumns.CREATE_TIME, event.getCreateTime());
        SQLiteDatabase db = getWritable();
        db.update(EventColumns.class.getSimpleName(), contentValues, where, args);
    }

}
