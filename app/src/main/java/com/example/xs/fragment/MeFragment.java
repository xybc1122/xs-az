package com.example.xs.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.xs.R;
import com.example.xs.mvp.model.LoginInfo;
import com.example.xs.utils.GlobalUtil;
import com.example.xs.utils.HkSdkUtil;
import com.example.xs.utils.MsgUtil;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class MeFragment extends Fragment implements View.OnClickListener {

    //登陆信息
    private LoginInfo loginInfo = null;
    private ImageButton mLoginOut = null;
    private RelativeLayout mLoginLayout;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment, container, false);
        initActivity(view);
        if (GlobalUtil.loginInfo != null && GlobalUtil.loginInfo.getLoginId() >= 0) {
            changLogin(true);
        }
        ipAdd.setText("192.168.2.64");
        port.setText("8000");
        userName.setText("admin");
        password.setText("xsznaf168");
        return view;
    }

    private void changLogin(boolean isFlg) {
        if (isFlg) {
            mLoginLayout.setVisibility(View.INVISIBLE);
            mLoginOut.setVisibility(View.VISIBLE);
        } else {
            mLoginOut.setVisibility(View.INVISIBLE);
            mLoginLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                mLoginBt.setEnabled(false);
                mLoginBt.setImageResource(R.mipmap.sorcket_red);
                if (checkLogin()) {
                    changLogin(true);
                    mLoginBt.setImageResource(R.mipmap.socket);
                    mLoginBt.setEnabled(true);
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
            case R.id.login_out:
                showMessagePositiveDialog();
                break;
        }
    }

    private void showMessagePositiveDialog() {
        new QMUIDialog.MessageDialogBuilder(getContext())
                .setTitle("注销设备")
                .setMessage("确定注销吗")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        LogOut();
                        changLogin(false);
                    }
                })
                .create().show();

    }


    //    注销
    private void LogOut() {
        final QMUITipDialog tip;
        boolean isLogOut = HkSdkUtil.HkLogOut(GlobalUtil.loginInfo.getLoginId());
        if (!isLogOut) {
            tip = MsgUtil.tipDialog(getContext(), "注销失败", QMUITipDialog.Builder.ICON_TYPE_FAIL);
            tip.show();
            MsgUtil.stopHandlerMsg(tip, 2000);
        }
        if (isLogOut) {
            GlobalUtil.loginInfo = null;
            //注销成功
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
                m_iLogID = HkSdkUtil.HkLogin(strIp, nPort, strUserName, strPwd, m_oNetDvrDeviceInfoV30);
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
            switch (HkSdkUtil.HkErrorNumber()) {
                case 1:
                    tipDialog = MsgUtil.tipDialog(getContext(), getString(R.string.login_error), QMUITipDialog.Builder.ICON_TYPE_FAIL);
                    break;
                default:
                    tipDialog = MsgUtil.tipDialog(getContext(), getString(R.string.device_error), QMUITipDialog.Builder.ICON_TYPE_FAIL);
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

    private void findViews(View view) {
        mLoginOut = view.findViewById(R.id.login_out);
        mLoginLayout = view.findViewById(R.id.login_layout);
        mLoginBt = view.findViewById(R.id.login);
        ipAdd = view.findViewById(R.id.ip);
        port = view.findViewById(R.id.port);
        userName = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
    }

    private void setListeners() {
        mLoginOut.setOnClickListener(this);
        mLoginBt.setOnClickListener(this);
    }

    // GUI init
    private void initActivity(View view) {
        findViews(view);
        setListeners();
    }


}
