package me.jingbin.sbannerview;

import java.util.ArrayList;

public class TimeUtil {

    public static ArrayList<String> getCountTimeByLong(long finishTime) {
        long day = 0, hour = 0, minute = 0, second = 0;

        day = finishTime / 60 / 60 / 24;
        hour = (finishTime - day * 60 * 60 * 24) / 60 / 60;
        minute = (finishTime - day * 60 * 60 * 24 - hour * 60 * 60) / 60;
        second = (finishTime - day * 60 * 60 * 24 - hour * 60 * 60 - minute * 60);
        if (day < 0) {
            day = 0;
        }
        if (hour < 0) {
            hour = 0;
        }
        if (minute < 0) {
            minute = 0;
        }
        if (second < 0) {
            second = 0;
        }

        ArrayList<String> list = new ArrayList<>();
        list.add(String.valueOf(day));
        list.add(String.valueOf(hour));
        list.add(String.valueOf(minute));
        list.add(String.valueOf(second));
        return list;
    }
}
