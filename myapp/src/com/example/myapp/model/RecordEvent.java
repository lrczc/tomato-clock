package com.example.myapp.model;

/**
 * Created by shizhao.czc on 2014/8/27.
 */
public class RecordEvent extends BaseEvent {
    private long completeTime;

    public RecordEvent(Event event) {
        eventID = event.getEventID();
        eventName = event.getEventName();
        time = event.getTime();
        sound = event.getSound();
        planTime = event.getPlanTime();
        createTime = event.getCreateTime();
    }

    public RecordEvent() {
        super();
    }

    public long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(long completeTime) {
        this.completeTime = completeTime;
    }
}
