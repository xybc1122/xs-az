package com.example.xs.utils;

import android.util.Log;
import android.view.Surface;

import com.example.xs.jna.HCNetSDKJNAInstance;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_FILECOND;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.NET_DVR_TIME;
import com.hikvision.netsdk.NET_DVR_VOD_PARA;
import com.hikvision.netsdk.RealPlayCallBack;

public class HkSdkUtil {
    private final static String TAG = HkSdkUtil.class.getName();


    /**
     * 初始化SDK
     */
    public static boolean initSdk() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/storage/emulated/0/sdklog/", true);
        return true;
    }

    //登陆api
    public static int HkLogin(String strIp, int nPort, String strUserName, String strPwd, NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30) {
        return HCNetSDK.getInstance().NET_DVR_Login_V30(strIp, nPort, strUserName, strPwd, m_oNetDvrDeviceInfoV30);
    }

    //注销api
    public static boolean HkLogOut(int iLoginId) {
        return HCNetSDK.getInstance().NET_DVR_Logout_V30(iLoginId);
    }

    //api 报错返回int 数字
    public static int HkErrorNumber() {
        return HCNetSDK.getInstance().NET_DVR_GetLastError();
    }

    //播放api
    public static int playVideo(int iLoginId, NET_DVR_PREVIEWINFO previewInfo, RealPlayCallBack callBack) {
        return HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iLoginId, previewInfo, callBack);
    }

    //暂停api
    public static boolean stopPlayVideo(int playId) {
        return HCNetSDK.getInstance().NET_DVR_StopRealPlay(playId);
    }

    //云台操作api
    public static boolean cloudOpera(final int playId, final int dwPTZCommand, final int dwStop) {
        return HCNetSDK.getInstance().NET_DVR_PTZControl(playId, dwPTZCommand, dwStop);
    }

    //抓图api
    public static boolean grabImgBlock(int playId, String path, int dwTimeOut) {
        return HCNetSDKJNAInstance.getInstance().NET_DVR_CapturePictureBlock(playId, path, dwTimeOut);
    }


    //开始录屏api
    public static boolean startRealData(int playId, int dwTransType, String path) {
        return HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(playId, dwTransType, path);
    }


    //停止录屏api
    public static boolean stopRealData(int playId) {
        return HCNetSDK.getInstance().NET_DVR_StopSaveRealData(playId);
    }

    //设置录像回放时间 年月日 时分秒
    public static NET_DVR_TIME setRePlayTime(String year, String month, String day, String hour, String minute) {
        NET_DVR_TIME derTime = new NET_DVR_TIME();
        derTime.dwYear = Integer.parseInt(year);
        derTime.dwMonth = Integer.parseInt(month);
        derTime.dwDay = Integer.parseInt(day);
        derTime.dwHour = Integer.parseInt(hour);
        derTime.dwMinute = Integer.parseInt(minute);
        return derTime;
    }

    //设置录像回放 参数
    public static NET_DVR_VOD_PARA setRePlayParam(NET_DVR_TIME timeStart,
                                                  NET_DVR_TIME timeStop, int mIStartChan, Surface surface) {
        NET_DVR_VOD_PARA para = new NET_DVR_VOD_PARA();
        para.struBeginTime = timeStart;
        para.struEndTime = timeStop;
        //主码流
        para.byStreamType = 0;
        //通道
        para.struIDInfo.dwChannel = mIStartChan;
        //播放句柄
        para.hWnd = surface;
        return para;
    }

    //录像回放api
    public static int getRePlayByTime(int loginId, NET_DVR_VOD_PARA para) {
        return HCNetSDK.getInstance().NET_DVR_PlayBackByTime_V40(loginId, para);
    }

    //查找回放文件
    public static int findFile(int loginId, NET_DVR_FILECOND lpSearchInfo) {
        return HCNetSDK.getInstance().NET_DVR_FindFile_V30(loginId, lpSearchInfo);
    }
}
