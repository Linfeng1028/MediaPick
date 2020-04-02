package com.wlf.mediapick.utils;

import android.content.Context;

import com.wlf.mediapick.R;

import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

public class TimeUtils {
    private static StringBuilder sFormatBuilder = new StringBuilder();
    private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
    private static final Object[] sTimeArgs = new Object[5];

    public static String makeTimeString(Context context, long milliSecs) {
        int secs = milli2Secs(milliSecs);
        String durationformat = context.getString(
                secs < 3600 ? R.string.media_duration_format_short : R.string.media_duration_format_long);
        sFormatBuilder.setLength(0);

        final Object[] timeArgs = sTimeArgs;
        timeArgs[0] = secs / 3600;      // 小时数
        timeArgs[1] = secs / 60;        // 分钟数
        timeArgs[2] = (secs / 60) % 60; // 基于小时的基础上, 剩余分钟数
        timeArgs[3] = secs;             // 总共有多少秒
        timeArgs[4] = secs % 60;        // 基于分钟的基础上, 剩余秒数

        return sFormatter.format(durationformat, timeArgs).toString();
    }

    /**
     * 毫秒转成秒, 向上取整
     */
    public static int milli2Secs(long milliSecs) {
        // 向上取整
        return (int) Math.ceil(milliSecs / 1000f);
    }

    /**
     *
     * @return 当前年份月份 格式为YYYYMM
     */
    public static int getCurrentYearMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) * 100 + calendar.get(Calendar.MONTH) + 1;
    }

}
