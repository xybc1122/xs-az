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
}
