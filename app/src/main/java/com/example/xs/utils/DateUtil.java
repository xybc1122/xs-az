package com.example.xs.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static String getNow() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return df.format(new Date());
    }

    //获取当天0点时间
    public static String getCalendarNowZeroY() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    //设置一个 Calendar 时间
    public static Calendar setCalendarTime(String time) {
        if (time == null || time.equals("")) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        // 对 calendar 设置时间的方法
        // 设置传入的时间格式
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        // 指定一个日期
        Date date;
        try {
            date = dateFormat.parse(time);
            assert date != null;
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    //格式化时间输出字符串
    public static String formatDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return df.format(date);
    }

    //获取今天0点时分
    public static String getCalendarZeroH() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);//控制时
        cal.set(Calendar.MINUTE, 0);//控制分
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(cal.getTime());
    }


    //获取当前事件时分
    public static String getCalendarNowZeroHM() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(cal.getTime());
    }


    //获取当前事件时
    public static String getCalendarNowZeroH() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    //获得时分秒
    public static String[] getHMS(String time) {
        String[] strTime = time.split(" ");
        return strTime[1].split(":");
    }
}
