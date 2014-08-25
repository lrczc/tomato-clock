package com.example.myapp.model;


import java.util.Date;

/**
 * Created by czc on 2014/8/21.
 */
public class Event {
    private long eventID;
    private String eventName;
    private int time;
    private int sound;
    private long planTime;
    private long createTime;

    public Event() {
        eventID = 0;
        eventName = null;
        time = 0;
        sound = 0;
        planTime = System.currentTimeMillis();
        createTime = System.currentTimeMillis();
    }

    public Event(String eventName, long planTime) {
        this.eventName = eventName;
        this.planTime = planTime;
    }

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getPlanTime() {
        return planTime;
    }

    public void setPlanTime(long planTime) {
        this.planTime = planTime;
    }
}
