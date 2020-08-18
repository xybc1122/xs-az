package com.example.xs.utils;

import android.content.Context;
import android.os.Handler;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class MsgUtil {

    public static void showDialog(Context context, String text, int type) {
        QMUITipDialog tipDialog = tipDialog(context, text, type);
        tipDialog.show();
        stopHandlerMsg(tipDialog, 2000);
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


    public static String errMsg() {
        INT_PTR ptr = new INT_PTR();
        ptr.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
        return HCNetSDK.getInstance().NET_DVR_GetErrorMsg(ptr);

    }
}
