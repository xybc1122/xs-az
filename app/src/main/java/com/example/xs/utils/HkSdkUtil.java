package com.example.xs.utils;

import android.util.Log;

import com.example.xs.jna.HCNetSDKJNAInstance;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
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


}
