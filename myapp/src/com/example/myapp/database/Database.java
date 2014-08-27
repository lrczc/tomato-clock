package com.example.myapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapp.AppUtil;
import com.example.myapp.model.Event;
import com.example.myapp.model.RecordEvent;

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

    public Event getEvent(long id) {
        String[] eventColumns = new String[] {
                EventColumns._ID, EventColumns.EVENT_NAME, EventColumns.TIME, EventColumns.SOUND, EventColumns.PLAN_TIME, EventColumns.CREATE_TIME
        };
        String selection = EventColumns._ID + "=" + id;
        SQLiteDatabase db = getReadable();
        Cursor cursor = db.query(EventColumns.class.getSimpleName(), eventColumns, selection, null, null, null, null, null);
        Event event = new Event();
        if (cursor.moveToNext()) {
            event.setEventID(cursor.getInt(0));
            event.setEventName(cursor.getString(1));
            event.setTime(cursor.getInt(2));
            event.setSound(cursor.getInt(3));
            event.setPlanTime(cursor.getLong(4));
            event.setCreateTime(cursor.getLong(5));
            cursor.close();
            return event;
        }
        cursor.close();
        return null;
    }


    public List<Event> getTodayEvents(String count) {
        String TABLE_NAME = EventColumns.class.getSimpleName();
        String[] eventColumns = new String[] {
                EventColumns._ID, EventColumns.EVENT_NAME, EventColumns.TIME, EventColumns.SOUND, EventColumns.PLAN_TIME, EventColumns.CREATE_TIME
        };
        long today = System.currentTimeMillis()/86400000*86400000-(23-Calendar.ZONE_OFFSET)*3600000;
        long tomorrow = System.currentTimeMillis()/86400000*86400000+(1+Calendar.ZONE_OFFSET)*3600000;

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
            event.setPlanTime(cursor.getLong(4));
            event.setCreateTime(cursor.getLong(5));
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

    public List<RecordEvent> getRecordEvents() {
        String TABLE_NAME = RecordEventColumns.class.getSimpleName();
        String[] eventColumns = new String[] {
                RecordEventColumns._ID, RecordEventColumns.EVENT_NAME,
                RecordEventColumns.TIME, RecordEventColumns.SOUND,
                RecordEventColumns.PLAN_TIME, RecordEventColumns.CREATE_TIME,
                RecordEventColumns.COMPLETE_TIME
        };
        String orderBy = RecordEventColumns.COMPLETE_TIME;
        SQLiteDatabase db = getReadable();
        Cursor cursor = db.query(TABLE_NAME, eventColumns, null, null, null, null, orderBy, null);
        List<RecordEvent> events = new ArrayList<RecordEvent>();
        while (cursor.moveToNext()) {
            RecordEvent event = new RecordEvent();
            event.setEventID(cursor.getInt(0));
            event.setEventName(cursor.getString(1));
            event.setTime(cursor.getInt(2));
            event.setSound(cursor.getInt(3));
            event.setPlanTime(cursor.getLong(4));
            event.setCreateTime(cursor.getLong(5));
            event.setCompleteTime(cursor.getLong(6));
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public RecordEvent getRecordEvent(int recordEventId) {
        String[] eventColumns = new String[] {
                RecordEventColumns._ID, RecordEventColumns.EVENT_NAME,
                RecordEventColumns.TIME, RecordEventColumns.SOUND,
                RecordEventColumns.PLAN_TIME, RecordEventColumns.CREATE_TIME,
                RecordEventColumns.COMPLETE_TIME
        };
        String selection = RecordEventColumns._ID + "=" + recordEventId;
        SQLiteDatabase db = getReadable();
        Cursor cursor = db.query(RecordEventColumns.class.getSimpleName(), eventColumns, selection, null, null, null, null, null);
        RecordEvent event = new RecordEvent();
        if (cursor.moveToNext()) {
            event.setEventID(cursor.getInt(0));
            event.setEventName(cursor.getString(1));
            event.setTime(cursor.getInt(2));
            event.setSound(cursor.getInt(3));
            event.setPlanTime(cursor.getLong(4));
            event.setCreateTime(cursor.getLong(5));
            event.setCompleteTime(cursor.getLong(6));
            cursor.close();
            return event;
        }
        cursor.close();
        return null;
    }

    public long insertRecordEvent(RecordEvent event) {
        String TABLE_NAME = RecordEventColumns.class.getSimpleName();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecordEventColumns.EVENT_NAME, event.getEventName());
        contentValues.put(RecordEventColumns.TIME, event.getTime());
        contentValues.put(RecordEventColumns.SOUND, event.getSound());
        contentValues.put(RecordEventColumns.PLAN_TIME, event.getPlanTime());
        contentValues.put(RecordEventColumns.CREATE_TIME, event.getCreateTime());
        contentValues.put(RecordEventColumns.COMPLETE_TIME, event.getCompleteTime());
        SQLiteDatabase db = getWritable();
        return db.insert(TABLE_NAME, null, contentValues);
    }

}
