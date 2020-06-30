package com.example.xs.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.example.xs.R;
import com.example.xs.utils.MsgUtil;
import com.hikvision.netsdk.HCNetSDK;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class MainActivity extends Activity implements View.OnClickListener {
    private final String TAG = MainActivity.class.getName();
    private int m_iLogID;
    private Button mLogOutBt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String miLogID = intent.getStringExtra("m_iLogID");
        m_iLogID = Integer.parseInt(miLogID);
        findViews();
        setListeners();
    }

    @Override
    public void onClick(View v) {
        final QMUITipDialog tip;
        switch (v.getId()) {
            case R.id.login_out:
                mLogOutBt.setEnabled(false);
                if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
                    tip = MsgUtil.tipDialog(this, "注销失败", QMUITipDialog.Builder.ICON_TYPE_FAIL);

                } else {
                    tip = MsgUtil.tipDialog(this, "注销成功", QMUITipDialog.Builder.ICON_TYPE_FAIL);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tip.dismiss();
                    }
                }, 2 * 1000);
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
                break;

        }
    }

    private void setListeners() {
        mLogOutBt.setOnClickListener(this);
    }

    private void findViews() {
        mLogOutBt = findViewById(R.id.login_out);
    }

}
