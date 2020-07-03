package com.example.xs.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.xs.R;
import com.example.xs.bean.DeviceInfo_30;
import com.example.xs.bean.LoginInfo;
import com.example.xs.jna.HCNetSDKJNAInstance;
import com.example.xs.utils.MsgUtil;
import com.example.xs.views.PlaySurfaceView;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.StdDataCallBack;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class MainActivity extends Activity implements  View.OnClickListener,Callback {
    private final String TAG = MainActivity.class.getName();
    private LoginInfo loginInfo = null;
    private int m_iLogID;
    private DeviceInfo_30 deviceInfo_30 = null;
    private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V40
    private int m_iPlaybackID = -1;
    private ImageButton mLogOutBt = null;
    private ImageButton mPlayAndStop = null;
    private ImageButton mUp = null;
    private StdDataCallBack cbf = null;
    private static PlaySurfaceView[] playView = new PlaySurfaceView[4];
    private boolean m_bMultiPlay = false;
    private int m_iStartChan = 0; // 开始的频道号
    private int m_iChanNum = 0; // channel number
    private SurfaceView m_osurfaceView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        loginInfo = (LoginInfo) intent.getSerializableExtra("loginInfo");
        m_iLogID = loginInfo.getLoginId();
        deviceInfo_30 = loginInfo.getDeviceInfo_30();
        findViews();
        init();
        setListeners();
    }

    @Override
    public void onClick(View v) {
        final QMUITipDialog tipDialog;
        switch (v.getId()) {
            case R.id.login_out:
                showMessagePositiveDialog();
                break;
            case R.id.play_and_stop:
                try {
                    if (m_iLogID < 0) {
                        Log.e(TAG, "please login on device first");
                        return;
                    }

                    if (m_iPlaybackID >= 0) {
                        Log.i(TAG, "Please stop palyback first");
                        return;
                    }

                    if (m_iChanNum > 1)// preview more than a channel
                    {
                        if (!m_bMultiPlay) {
                            startMultiPreview();
                            m_bMultiPlay = true;
                            mPlayAndStop.setImageResource(R.mipmap.stop);
                        } else {
                            stopMultiPreview();
                            m_bMultiPlay = false;
                            mPlayAndStop.setImageResource(R.mipmap.play);
                        }
                    } else // preivew a channel
                    {
                        if (m_iPlayID < 0) {
                            String msg = startSinglePreview();
                            if (msg != null) {
                                tipDialog = MsgUtil.tipDialog(this, msg, QMUITipDialog.Builder.ICON_TYPE_FAIL);
                                tipDialog.show();
                                MsgUtil.stopHandlerMsg(tipDialog, 2000);
                            }
                        } else {
                            stopSinglePreview();
                            mPlayAndStop.setImageResource(R.mipmap.play);
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.toString());
                }
                break;

            case R.id.up:
                mUp.setImageResource(R.mipmap.up_y);
                break;
        }
    }

    private void init() {
        m_osurfaceView.getHolder().addCallback(this);
        if (deviceInfo_30.byChanNum > 0) {
            m_iStartChan = deviceInfo_30.byStartChan;
            m_iChanNum = deviceInfo_30.byChanNum;
        } else if (deviceInfo_30.byIPChanNum > 0) {
            m_iStartChan = deviceInfo_30.byStartDChan;
            m_iChanNum = deviceInfo_30.byIPChanNum + deviceInfo_30.byHighDChanNum * 256;
        }

        if (m_iChanNum > 1) {
            ChangeSingleSurFace(false);
        } else {
            ChangeSingleSurFace(true);
        }
        // get instance of exception callback and set
        ExceptionCallBack oexceptionCbf = getExceptiongCbf();
        if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf))
        {
            Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        m_osurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created");

        //valid just when single channel preview
        if (-1 == m_iPlayID && -1 == m_iPlaybackID) {
            return;
        }
        playView[0].m_hHolder = holder;
        Surface surface = holder.getSurface();
        if (surface.isValid()) {
            if (m_iPlayID != -1) {
                if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(m_iPlayID, 0, holder)) {
                    Log.e(TAG, "Player setVideoWindow failed!");
                }
            } else {
                if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(m_iPlaybackID, 0, holder)) {
                    Log.e(TAG, "Player setVideoWindow failed!");
                }
            }

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surface changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Player setVideoWindow release!");
        if (-1 == m_iPlayID && -1 == m_iPlaybackID) {
            return;
        }
        if (holder.getSurface().isValid()) {
            if (m_iPlayID != -1) {
                if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(m_iPlayID, 0, null)) {
                    Log.e(TAG, "Player setVideoWindow failed!");
                }
            } else {
                if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(m_iPlaybackID, 0, null)) {
                    Log.e(TAG, "Player setVideoWindow failed!");
                }
            }
        }
    }


    /**
     * @fn getExceptiongCbf
     * @author zhuzhenlei
     * @brief process exception
     * @param NULL
     *            [in]
     * @param NULL
     *            [out]
     * @return exception instance
     */
    private ExceptionCallBack getExceptiongCbf()
    {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack()
        {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle)
            {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }


    private void ChangeSingleSurFace(boolean bSingle) {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);


        for (int i = 0; i < 4; i++) {
            if (playView[i] == null) {
                playView[i] = new PlaySurfaceView(this);
                playView[i].setParam(metric.widthPixels);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                addContentView(playView[i], params);
                playView[i].setVisibility(View.INVISIBLE);

            }
        }

        if (bSingle) {
            for (int i = 0; i < 4; ++i) {
                playView[i].setVisibility(View.INVISIBLE);
            }
            playView[0].setParam(metric.widthPixels * 2);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = playView[3].getM_iHeight() - (3 / 2)
                    * playView[3].getM_iHeight();
//            params.bottomMargin = 0;
            params.leftMargin = 0;
            // params.
            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            playView[0].setLayoutParams(params);
            playView[0].setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < 4; ++i) {
                playView[i].setVisibility(View.VISIBLE);
            }

            playView[0].setParam(metric.widthPixels);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = playView[0].getM_iHeight() - (0 / 2)
                    * playView[0].getM_iHeight();
            params.leftMargin = (0 % 2) * playView[0].getM_iWidth();
            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            playView[0].setLayoutParams(params);
        }
    }


    private void startMultiPreview() {
        //one by one
        for (int i = 0; i < 4; i++) {
            playView[i].startPreview(m_iLogID, m_iStartChan + i);
        }
    }


    private void stopMultiPreview() {
        int i = 0;
        for (i = 0; i < 4; i++) {
            playView[i].stopPreview();
        }
        m_iPlayID = -1;
    }


    //播放
    private String startSinglePreview() {
        INT_PTR prt = new INT_PTR();
        if (m_iPlaybackID >= 0) {
            return "请先停止播放";
        }
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan;
        previewInfo.dwStreamType = 0; // main stream
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = playView[0].m_hHolder;

        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID, previewInfo, null);
        if (m_iPlayID < 0) {
            prt.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
            Log.i(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(prt));
            return "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(prt);
        }

//         open sound
        boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_OpenSound(m_iPlayID);
        if (bRet) {
            Log.e(TAG, "NET_DVR_OpenSound Succ!");
        }

//       capture picture
        if (HCNetSDKJNAInstance.getInstance().NET_DVR_CapturePicture(m_iPlayID, "/mnt/sdcard/capture_01.dmp")) {
            Log.e(TAG, "NET_DVR_CapturePicture Succ!");
        } else {
            prt.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
            return "NET_DVR_CapturePicture fail! Err:" + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(prt);
        }


//       set volume
        short volume = 55;
        if (HCNetSDKJNAInstance.getInstance().NET_DVR_Volume(m_iPlayID, volume)) {
            Log.e(TAG, "NET_DVR_Volume Succ!");
        }


//         std data call back  标准数据回调 这里处理数据流
        if (cbf == null) {
            cbf = new StdDataCallBack() {
                public void fStdDataCallback(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {

                }

            };
        }
        //这里判断数据流是否获取成功
        if (!HCNetSDK.getInstance().NET_DVR_SetStandardDataCallBack(m_iPlayID, cbf)) {
            prt.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
            return "NET_DVR_SetStandardDataCallBack is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(prt);
        } else {
            Log.i(TAG, "NET_DVR_SetStandardDataCallBack sucess");
        }
        return null;
    }

    /**
     * @param NULL [in]
     * @param NULL [out]
     * @return NULL
     * @fn stopSinglePreview
     * @author zhuzhenlei
     * @brief stop preview
     */
    private void stopSinglePreview() {
        if (m_iPlayID < 0) {
            Log.e(TAG, "m_iPlayID < 0");
            return;
        }

        if (HCNetSDKJNAInstance.getInstance().NET_DVR_CloseSound()) {
            Log.e(TAG, "NET_DVR_CloseSound Succ!");
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
            Log.e(TAG, "StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        Log.i(TAG, "NET_DVR_StopRealPlay succ");
        m_iPlayID = -1;
    }

    //    注销
    private void LogOut() {
        final QMUITipDialog tip;
        boolean isLogOut = HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID);
        if (!isLogOut) {
            tip = MsgUtil.tipDialog(this, "注销失败", QMUITipDialog.Builder.ICON_TYPE_FAIL);
        } else {
            tip = MsgUtil.tipDialog(this, "注销成功", QMUITipDialog.Builder.ICON_TYPE_SUCCESS);
        }
        tip.show();
        MsgUtil.stopHandlerMsg(tip, 2000);
        if (isLogOut) {
            this.finish();
        }
    }

    private void showMessagePositiveDialog() {
        new QMUIDialog.MessageDialogBuilder(this)
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
                    }
                })
                .create().show();

    }


    private void setListeners() {
        mLogOutBt.setOnClickListener(this);
        mPlayAndStop.setOnClickListener(this);
        mUp.setOnClickListener(this);
    }

    private void findViews() {
        mLogOutBt = findViewById(R.id.login_out);
        mPlayAndStop = findViewById(R.id.play_and_stop);
        mUp = findViewById(R.id.up);
        m_osurfaceView = (SurfaceView) findViewById(R.id.sur_player);
    }

}
