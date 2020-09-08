package com.example.xs.utils;

import java.util.ArrayList;
import java.util.List;

public class StrUtil {


    //2020/8/27 10:48:15
    public static List<Integer> strSpl(String strTime) {
        strTime = strTime.trim();
        String[] str = strTime.split(" ");
        //时分秒
        String[] HMS = str[1].split(":");

        List<Integer> timeList = new ArrayList<>(HMS.length);
        for (String hm : HMS) {
            timeList.add(Integer.parseInt(hm));
        }
        return timeList;
    }

    /**
     * 对时间进行格式化
     *
     * @param hour 小时
     * @param min  分钟
     * @param sec  秒
     * @return 字符串数字
     */
    public  static String formatString(int hour, int min, int sec) {
//        System.out.println("hour--->" + hour);
        StringBuilder builder = new StringBuilder();
        if (hour == 24) {
            builder.append(hour - 1).append(":").append(59).append(":").append(59);
            return builder.toString();
        }
        if (hour < 10) {
            builder.append("0").append(hour).append(":");
        } else {
            builder.append(hour).append(":");
        }
        if (min < 10 && min >= 0) {
            builder.append("0").append(min).append(":");
        } else {
            builder.append(min).append(":");
        }
        if (sec < 10 && sec >= 0) {
            builder.append("0").append(sec);
        } else {
            builder.append(sec);
        }
        return builder.toString();
    }

}
