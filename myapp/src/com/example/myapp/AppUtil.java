package com.example.myapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shizhao.czc on 2014/8/26.
 */
public class AppUtil {
    public static final long DAY_TIME = 86400000;

    public static final int[] TIME = {0, 1, 15, 20, 25, 30, 35, 40, 45};

    public static final String[] TIME_TITLE = {"未设置", "10分钟", "15分钟", "20分钟", "25分钟", "30分钟", "35分钟", "40分钟", "45分钟"};

    public static final String[] SOUND_TITLE = {"dingding", "dangdang"};

    public static final int[] SOUND = {R.raw.sound1, R.raw.sound2};

    public static String timeToString1(long time, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(time));
    }

    public static long stringToTime(String dateString, SimpleDateFormat dateFormat) {
        try {
            return dateFormat.parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isSameDay(long time1, long time2) {
       return (time1/DAY_TIME == time2/DAY_TIME);
    }
}
