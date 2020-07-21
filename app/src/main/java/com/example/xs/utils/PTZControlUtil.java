package com.example.xs.utils;

import com.hikvision.netsdk.HCNetSDK;

public class PTZControlUtil {

    public static void PTZControlOther(int m_iLogID, int m_iStartChan, int dwPTZCommand, int dwStop) {
        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, dwPTZCommand, dwStop)) {
            System.out.println("PTZControl  PAN_LEFT 0 faild!" + " err: " + MsgUtil.errMsg());
            return;
        }
        System.out.println("PTZControl  PAN_LEFT 0 succ");
    }


    public static void PTZControl(int lRealHandle, int dwPTZCommand, int dwStop) {
        if (!HCNetSDK.getInstance().NET_DVR_PTZControl(lRealHandle, dwPTZCommand, dwStop)) {
            System.out.println("PTZControl  PAN_LEFT 0 faild!" + " err: " + MsgUtil.errMsg());
            return;
        }
        System.out.println("PTZControl  PAN_LEFT 0 succ");
    }


}
