package com.example.xs.utils;

import android.content.Context;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class MsgUtil {


    public static QMUITipDialog tipDialog(Context context, String text, int type) {
        return new QMUITipDialog.Builder(context)
                .setIconType(type)
                .setTipWord(text)
                .create();
    }
}
