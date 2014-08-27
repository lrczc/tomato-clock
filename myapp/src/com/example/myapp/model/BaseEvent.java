package com.example.myapp.model;

/**
 * Created by shizhao.czc on 2014/8/27.
 */
public class BaseEvent {
    protected long eventID;
    protected String eventName;
    protected int time;
    protected int sound;
    protected long planTime;
    protected long createTime;

    public BaseEvent() {
        eventID = 0;
        eventName = null;
        time = 0;
        sound = 0;
        planTime = System.currentTimeMillis();
        createTime = System.currentTimeMillis();
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
