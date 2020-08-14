package com.example.xs.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.xs.R;
import com.example.xs.mvp.model.LoginInfo;
import com.example.xs.utils.GlobalUtil;
import com.example.xs.utils.MsgUtil;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class LoginActivity extends Activity implements View.OnClickListener {
    private final String TAG = StartActivity.class.getName();
    //登陆信息
    private LoginInfo loginInfo = null;
    private EditText ipAdd = null;
    private EditText port = null;
    private EditText userName = null;
    private EditText password = null;
    private int m_iStartChan = 0; // start channel number
    private int m_iChanNum = 0; // channel number
    private ImageButton mLoginBt = null;

    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

    private int m_iLogID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!initSdk()) {
            this.finish();
            return;
        }

        if (!initActivity()) {
            this.finish();
            return;
        }
        ipAdd.setText("192.168.2.64");
        port.setText("8000");
        userName.setText("admin");
        password.setText("xsznaf168");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                mLoginBt.setEnabled(false);
                mLoginBt.setImageResource(R.mipmap.sorcket_red);
                if (checkLogin()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    this.finish();
                    startActivity(intent);
                    break;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoginBt.setImageResource(R.mipmap.socket);
                        mLoginBt.setEnabled(true);
                    }
                }, 2 * 1000);
                mLoginBt.setEnabled(true);
                break;
        }
    }

    private boolean checkLogin() {
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        final String strIp = ipAdd.getText().toString();
        final int nPort = Integer.parseInt(port.getText().toString());
        final String strUserName = userName.getText().toString();
        final String strPwd = password.getText().toString();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                m_iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIp, nPort, strUserName, strPwd, m_oNetDvrDeviceInfoV30);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (m_iLogID < 0) {
            final QMUITipDialog tipDialog;
            switch (HCNetSDK.getInstance().NET_DVR_GetLastError()) {
                case 1:
                    tipDialog = MsgUtil.tipDialog(this, "账号或密码错误", QMUITipDialog.Builder.ICON_TYPE_FAIL);
                    break;
                default:
                    tipDialog = MsgUtil.tipDialog(this, "连接设备异常，请检查wifi是否跟设备连接", QMUITipDialog.Builder.ICON_TYPE_FAIL);
            }
            tipDialog.show();
            MsgUtil.stopHandlerMsg(tipDialog, 2000);
            return false;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }
        loginInfo = new LoginInfo(m_iLogID, m_iStartChan, m_iChanNum);
        //保存全局用户信息
        GlobalUtil.loginInfo = loginInfo;
        return true;
    }

    private void findViews() {
        mLoginBt = findViewById(R.id.login);
        ipAdd = findViewById(R.id.ip);
        port = findViewById(R.id.port);
        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }

    private void setListeners() {
        mLoginBt.setOnClickListener(this);
    }

    // GUI init
    private boolean initActivity() {
        findViews();
        setListeners();
        return true;
    }

    /**
     * 初始化SDK
     */
    private boolean initSdk() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/storage/emulated/0/sdklog/", true);
        return true;
    }

}
