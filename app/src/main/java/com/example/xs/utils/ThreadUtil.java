package com.example.xs.utils;

import android.content.Context;
import android.os.Handler;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class ThreadUtil {


    public static QMUITipDialog loadThread(Context context, String msg, final Runnable runnable) {
        QMUITipDialog tipDialog = MsgUtil.tipDialog(context, msg, QMUITipDialog.Builder.ICON_TYPE_LOADING);
        tipDialog.show();
        startThread(runnable);
        return tipDialog;
    }

    public static void startThread(final Runnable runnable) {
        final Handler handler = new Handler();
        Thread thread = new Thread() {
            public void run() {
                handler.post(runnable);
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
