package com.example.xs.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.example.xs.R;
import com.example.xs.utils.CrashUtil;
import com.example.xs.utils.MsgUtil;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class StartActivity extends Activity implements View.OnClickListener {

    private final String TAG = StartActivity.class.getName();
    private ImageButton mLoginBt = null;
    private int m_iLogID = -1;
    private int btnSize;
    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
    private EditText ipAdd = null;
    private EditText port = null;
    private EditText userName = null;
    private EditText password = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashUtil crashUtil = CrashUtil.getInstance();
        crashUtil.init(this);
        setContentView(R.layout.activity_start);

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
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    intent.putExtra("m_iLogID", m_iLogID + "");
                    mLoginBt.setEnabled(true);
                    startActivity(intent);
                    finish();
                    break;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoginBt.setImageResource(R.mipmap.socket);
                        mLoginBt.setEnabled(true);
                    }
                }, 2 * 1000);
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
                System.out.println("执行线程");
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("线程执行完毕");
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 2 * 1000);
            return false;
        }
        return true;
    }

    // GUI init
    private boolean initActivity() {
        findViews();
        setListeners();
        return true;
    }

    private void setListeners() {
        mLoginBt.setOnClickListener(this);

    }

    private void findViews() {
        mLoginBt = findViewById(R.id.login);
        ipAdd = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
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
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
        return true;
    }


    //    Activity生命周期
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.i(StartActivity.class.getName(), "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(StartActivity.class.getName(), "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(StartActivity.class.getName(), "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(StartActivity.class.getName(), "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(StartActivity.class.getName(), "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(StartActivity.class.getName(), "onStop");
    }

}
