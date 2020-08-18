package com.example.xs.utils;

public class PTZControlUtil {


    public static void PTZControl(int lRealHandle, int dwPTZCommand, int dwStop) {
        if (!HkSdkUtil.cloudOpera(lRealHandle, dwPTZCommand, dwStop)) {
            System.out.println("PTZControl  PAN_LEFT 0 faild!" + " err: " + MsgUtil.errMsg());
            return;
        }
        System.out.println("PTZControl  PAN_LEFT 0 succ");
    }


}
