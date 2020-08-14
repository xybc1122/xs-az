package com.example.xs.service;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

public class StartActivityQMUIImpl implements Execution {

    private QMUIRoundButton roundButton;
    private int color;

    public StartActivityQMUIImpl(QMUIRoundButton roundButton, int color) {
        this.roundButton = roundButton;
        this.color = color;
    }

    public StartActivityQMUIImpl() {
    }

    @Override
    public void execution() {
        commit();
    }

    public void commit() {
        roundButton.setBackgroundColor(color);
    }
}
