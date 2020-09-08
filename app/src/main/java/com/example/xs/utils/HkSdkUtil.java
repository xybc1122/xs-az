package com.example.xs.utils;

import android.content.Context;
import android.util.Log;
import android.view.Surface;

import com.example.xs.jna.HCNetSDKJNAInstance;
import com.example.xs.views.TimeScaleView;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_FILECOND;
import com.hikvision.netsdk.NET_DVR_FINDDATA_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.NET_DVR_TIME;
import com.hikvision.netsdk.NET_DVR_VOD_PARA;
import com.hikvision.netsdk.RealPlayCallBack;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.List;

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

    //查找回放文件 获得文件Handle
    public static int findFile(int loginId, NET_DVR_FILECOND lpSearchInfo) {
        return HCNetSDK.getInstance().NET_DVR_FindFile_V30(loginId, lpSearchInfo);
    }

    //设置  NET_DVR_FILECOND对象  String[] timeStr 年月日时间
    public static NET_DVR_FILECOND setNetDvrFileCond(String[] timeStr, int lChannel, String[] sDay, String[] eDay) {
        if (timeStr.length <= 0) {
            return null;
        }
        int y = Integer.parseInt(timeStr[0]);
        int m = Integer.parseInt(timeStr[1]);
        int d = Integer.parseInt(timeStr[2]);
        NET_DVR_FILECOND lpSearchInfo = new NET_DVR_FILECOND();
        lpSearchInfo.lChannel = lChannel;
        lpSearchInfo.dwFileType = 0xff;
        lpSearchInfo.dwIsLocked = 0xff;
        lpSearchInfo.dwUseCardNo = 0;
        lpSearchInfo.struStartTime.dwYear = y;
        lpSearchInfo.struStartTime.dwMonth = m;
        lpSearchInfo.struStartTime.dwDay = d;
        if (sDay == null || sDay.length <= 0) {
            lpSearchInfo.struStartTime.dwHour = 0;
            lpSearchInfo.struStartTime.dwMinute = 0;
            lpSearchInfo.struStartTime.dwSecond = 0;
        } else {
            lpSearchInfo.struStartTime.dwHour = Integer.parseInt(sDay[0]);
            lpSearchInfo.struStartTime.dwMinute = Integer.parseInt(sDay[1]);
            lpSearchInfo.struStartTime.dwSecond = Integer.parseInt(sDay[2]);
        }
        lpSearchInfo.struStopTime.dwYear = y;
        lpSearchInfo.struStopTime.dwMonth = m;
        lpSearchInfo.struStopTime.dwDay = d;
        if (eDay == null || eDay.length <= 0) {
            lpSearchInfo.struStopTime.dwHour = 23;
            lpSearchInfo.struStopTime.dwMinute = 59;
            lpSearchInfo.struStopTime.dwSecond = 59;
        } else {
            lpSearchInfo.struStopTime.dwHour = Integer.parseInt(eDay[0]);
            lpSearchInfo.struStopTime.dwMinute = Integer.parseInt(eDay[1]);
            lpSearchInfo.struStopTime.dwSecond = Integer.parseInt(eDay[2]);
        }
        return lpSearchInfo;
    }


    //获得播放时间片段
    public static List<TimeScaleView.TimePart> getTimePart(int iFindHandle, Context context) {
        QMUITipDialog tipDialog = MsgUtil.tipDialog(context, "视频查询中...", QMUITipDialog.Builder.ICON_TYPE_LOADING);
        tipDialog.show();
        List<TimeScaleView.TimePart> time = new ArrayList<>();
        int findNext = 0;
        NET_DVR_FINDDATA_V30 struFindData = new NET_DVR_FINDDATA_V30();
        String msg;
        while (findNext != -1) {
            findNext = HCNetSDK.getInstance().NET_DVR_FindNextFile_V30(iFindHandle, struFindData);
            if (findNext == HCNetSDK.NET_DVR_FILE_SUCCESS) {
//                System.out.println("~~~~~Find File===>" + CommonMethod.toValidString(new String(struFindData.sFileName)));
//                System.out.println("~~~~~File Size===>" + struFindData.dwFileSize);
                System.out.println("~~~~~File Time,from===>" + struFindData.struStartTime.ToString());
                System.out.println("~~~~~File Time,to===>" + struFindData.struStopTime.ToString());
                List<Integer> startTime = StrUtil.strSpl(struFindData.struStartTime.ToString());
                List<Integer> endTime = StrUtil.strSpl(struFindData.struStopTime.ToString());
                time.add(new TimeScaleView.TimePart(startTime.get(0), startTime.get(1), startTime.get(2),
                        endTime.get(0), endTime.get(1), endTime.get(2)));
            } else if (HCNetSDK.NET_DVR_FILE_NOFIND == findNext) {
                System.out.println("No file found");
                msg = "没有回放视频...";
                break;
            } else if (HCNetSDK.NET_DVR_NOMOREFILE == findNext) {
                System.out.println("All files are listed");
                msg = "All files are listed";
                break;
            } else if (HCNetSDK.NET_DVR_FILE_EXCEPTION == findNext) {
                System.out.println("Exception in searching");
                msg = "Exception in searching";
                break;
            } else if (HCNetSDK.NET_DVR_ISFINDING == findNext) {
                System.out.println("NET_DVR_ISFINDING");
            }
        }
        HCNetSDK.getInstance().NET_DVR_FindClose_V30(iFindHandle);
        MsgUtil.stopHandlerMsg(tipDialog, 1500);
        return time;
    }

    //是否有播发文件
    public static NET_DVR_FINDDATA_V30 findOneFilePlayInfo(int iFindHandle) {
        int findNext = 0;
        NET_DVR_FINDDATA_V30 struFindData = new NET_DVR_FINDDATA_V30();
        while (findNext != -1) {
            findNext = HCNetSDK.getInstance().NET_DVR_FindNextFile_V30(iFindHandle, struFindData);
            if (findNext == HCNetSDK.NET_DVR_FILE_SUCCESS) {
                return struFindData;
            } else if (HCNetSDK.NET_DVR_ISFINDING == findNext) {
                System.out.println("等待查找文件");
            }
        }
        return null;
    }


}
