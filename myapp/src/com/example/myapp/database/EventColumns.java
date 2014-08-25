package com.example.myapp.database;

import android.provider.BaseColumns;

/**
 * Created by shizhao.czc on 2014/8/25.
 */
public class EventColumns implements BaseColumns {

    @Column(notnull = true)
    public static final String EVENT_NAME = "event_name";

    @Column(type = Column.Type.INTEGER)
    public static final String TIME = "time";

    @Column(type = Column.Type.INTEGER)
    public static final String SOUND = "sound";

    @Column(type = Column.Type.INTEGER)
    public static final String PLAN_TIME ="plan_time";

    @Column(type = Column.Type.INTEGER)
    public static final String CREATE_TIME ="create_time";

}
