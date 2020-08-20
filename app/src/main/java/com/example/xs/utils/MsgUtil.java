package com.example.xs.utils;

import android.content.Context;
import android.os.Handler;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class MsgUtil {

    public static void showDialogFail(Context context, String text) {
        QMUITipDialog tipDialog = tipDialog(context, text, QMUITipDialog.Builder.ICON_TYPE_FAIL);
        tipDialog.show();
        stopHandlerMsg(tipDialog, 2000);
    }

    public static void showDialogSuccess(Context context, String text) {
        QMUITipDialog tipDialog = tipDialog(context, text, QMUITipDialog.Builder.ICON_TYPE_SUCCESS);
        tipDialog.show();
        stopHandlerMsg(tipDialog, 2000);
    }


    public static void showDialog(Context context, String text, int type) {
        QMUITipDialog tipDialog = tipDialog(context, text, type);
        tipDialog.show();
        stopHandlerMsg(tipDialog, 2000);
    }

    public static void showDialog(Context context, String text, int type, long delayMillis) {
        QMUITipDialog tipDialog = tipDialog(context, text, type);
        tipDialog.show();
        stopHandlerMsg(tipDialog, delayMillis);
    }

    public static QMUITipDialog tipDialog(Context context, String text, int type) {
        return new QMUITipDialog.Builder(context)
                .setIconType(type)
                .setTipWord(text)
                .create();
    }

    public static void stopHandlerMsg(final QMUITipDialog tipDialog, long delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, delayMillis);
    }

    //获得错误码返回字符串信息
    public static String errMsg() {
        INT_PTR ptr = new INT_PTR();
        ptr.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
        return HCNetSDK.getInstance().NET_DVR_GetErrorMsg(ptr);

    }

    //获得错误码
    public static int errMsgLast() {
        return HCNetSDK.getInstance().NET_DVR_GetLastError();

    }
}
