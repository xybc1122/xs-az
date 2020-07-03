package com.example.xs.activity;

import android.app.Activity;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.xs.R;
import com.example.xs.bean.DeviceInfo_30;
import com.example.xs.bean.LoginInfo;
import com.example.xs.utils.CrashUtil;
import com.example.xs.utils.MsgUtil;
import com.example.xs.views.PlaySurfaceView;
import com.example.xs.views.RockerView;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class StartActivity extends Activity implements View.OnClickListener, Callback {

    private final String TAG = StartActivity.class.getName();
    private ImageButton mLoginBt = null;
    private int m_iLogID = -1;
    private int btnSize;
    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
    //登陆信息
    private LoginInfo loginInfo = null;
    private EditText ipAdd = null;
    private EditText port = null;
    private EditText userName = null;
    private EditText password = null;

    private RockerView rockerView = null;


    private static PlaySurfaceView[] playView = new PlaySurfaceView[4];
    private int m_iStartChan = 0; // start channel number
    private int m_iChanNum = 0; // channel number
    private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V40
    private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime
    private Button m_oPreviewBtn = null;
    private boolean m_bMultiPlay = false;
    private SurfaceView m_osurfaceView = null;
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

//        rockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
//        // 监听摇动方向
//        rockerView.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, new RockerView.OnShakeListener() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void direction(RockerView.Direction direction) {
//                System.out.println("摇动方向 : " + direction);
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                if (m_iLogID < 0)
                {
                    // login on the device
                    m_iLogID = loginDevice();
                    if (m_iLogID < 0)
                    {
                        Log.e(TAG, "该设备登录失败!");
                        return;
                    }
                    else
                    {
                        Log.i(TAG, "m_iLogID=" + m_iLogID);
                    }
                    // get instance of exception callback and set
                    ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                    if (oexceptionCbf == null)
                    {
                        Log.e(TAG, "ExceptionCallBack object is failed!");
                        return;
                    }

                    if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf))
                    {
                        Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
                        return;
                    }

                    mLoginBt.setImageResource(R.mipmap.sorcket_red);
                    Log.i(TAG, "Login sucess");
                }
                else
                {
                    // whether we have logout
                    if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID))
                    {
                        Log.e(TAG, " NET_DVR_Logout is failed!");
                        //if (!HCNetSDKJNAInstance.getInstance().NET_DVR_DeleteOpenEzvizUser(m_iLogID)) {
                        //		Log.e(TAG, " NET_DVR_DeleteOpenEzvizUser is failed!");
                        return;
                    }
                    mLoginBt.setImageResource(R.mipmap.sorcket_red);
                    m_iLogID = -1;
                }
                break;
            case R.id.btn_Preview:
                try
                {
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(StartActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    if (m_iLogID < 0)
                    {
                        Log.e(TAG, "please login on device first");
                        return;
                    }

                    if (m_iPlaybackID >= 0)
                    {
                        Log.i(TAG, "Please stop palyback first");
                        return;
                    }

                    if (m_iChanNum > 1)// preview more than a channel
                    {
                        if (!m_bMultiPlay)
                        {
                            startMultiPreview();
                            m_bMultiPlay = true;
                            m_oPreviewBtn.setText("Stop");
                        }
                        else
                        {
                            stopMultiPreview();
                            m_bMultiPlay = false;
                            m_oPreviewBtn.setText("Preview");
                        }
                    }
                    else // preivew a channel
                    {
                        if (m_iPlayID < 0)
                        {

                        }
                        else
                        {

                            m_oPreviewBtn.setText("Preview");
                        }
                    }
                }
                catch(Exception ex)
                {
                    Log.e(TAG, ex.toString());
                }
                break;
        }
    }

//                mLoginBt.setEnabled(false);
//                mLoginBt.setImageResource(R.mipmap.sorcket_red);
//                if (checkLogin()) {
//                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                    intent.putExtra("loginInfo", loginInfo);
//                    mLoginBt.setEnabled(true);
//                    this.finish();
//                    startActivity(intent);
//                    break;
//                }
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mLoginBt.setImageResource(R.mipmap.socket);
//                        mLoginBt.setEnabled(true);
//                    }
//                }, 2 * 1000);


    private void startMultiPreview()
    {
        //one by one
        for (int i = 0; i < 4; i++)
        {
            playView[i].startPreview(m_iLogID, m_iStartChan + i);
        }
    }
    private void stopMultiPreview()
    {
        int i = 0;
        for (i = 0; i < 4; i++)
        {
            playView[i].stopPreview();
        }
        m_iPlayID = -1;
    }

    /**
     * @fn loginDevice
     * @author zhangqing
     * @brief login on device
     * @param NULL
     *            [in]
     * @param NULL
     *            [out]
     * @return login ID
     */
    private int loginDevice()
    {
        int iLogID = -1;
        iLogID = loginNormalDevice();
        // iLogID = JNATest.TEST_EzvizLogin();
        // iLogID = loginEzvizDevice();
        return iLogID;
    }


    /**
     * @fn loginNormalDevice
     * @author zhuzhenlei
     * @brief login on device
     * @param NULL
     *            [in]
     * @param NULL
     *            [out]
     * @return login ID
     */
    private int loginNormalDevice()
    {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30)
        {
            Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
            return -1;
        }
        final String strIp = ipAdd.getText().toString();
        final int nPort = Integer.parseInt(port.getText().toString());
        final String strUserName = userName.getText().toString();
        final String strPwd = password.getText().toString();

        // call NET_DVR_Login_v30 to login on, port 8000 as default
        m_iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIp, nPort, strUserName, strPwd, m_oNetDvrDeviceInfoV30);
        if (m_iLogID < 0)
        {
            Log.e(TAG, "NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }

        if (m_oNetDvrDeviceInfoV30.byChanNum > 0)
        {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        }
        else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0)
        {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }

        if (m_iChanNum > 1)
        {
            ChangeSingleSurFace(false);
        }
        else
        {
            ChangeSingleSurFace(true);
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");
        return m_iLogID;
    }


    private void ChangeSingleSurFace(boolean bSingle)
    {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        for (int i = 0; i < 4; i++)
        {
            if (playView[i] == null)
            {
                playView[i] = new PlaySurfaceView(this);
                playView[i].setParam(metric.widthPixels);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                addContentView(playView[i], params);
                playView[i].setVisibility(View.INVISIBLE);

            }
        }

        if (bSingle)
        {
            for (int i = 0; i < 4; ++i)
            {
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
        }
        else
        {
            for (int i = 0; i < 4; ++i)
            {
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

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        m_osurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created");

        //valid just when single channel preview
        if (-1 == m_iPlayID && -1 == m_iPlaybackID)
        {
            return;
        }
        playView[0].m_hHolder = holder;
        Surface surface = holder.getSurface();
        if (true == surface.isValid())
        {
            if(m_iPlayID != -1)
            {
                if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(m_iPlayID, 0, holder))
                {
                    Log.e(TAG, "Player setVideoWindow failed!");
                }
            }
            else
            {
                if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(m_iPlaybackID, 0, holder))
                {
                    Log.e(TAG, "Player setVideoWindow failed!");
                }
            }

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        Log.i(TAG, "surface changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.i(TAG, "Player setVideoWindow release!");
        if (-1 == m_iPlayID && -1 == m_iPlaybackID)
        {
            return;
        }
        if (true == holder.getSurface().isValid())
        {
            if(m_iPlayID != -1)
            {
                if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(m_iPlayID, 0, null))
                {
                    Log.e(TAG, "Player setVideoWindow failed!");
                }
            }
            else
            {
                if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(m_iPlaybackID, 0, null))
                {
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
            MsgUtil.stopHandlerMsg(tipDialog, 2000);
            return false;
        }
        loginInfo = new LoginInfo(m_iLogID, set(m_oNetDvrDeviceInfoV30));
        return true;
    }

    private DeviceInfo_30 set(NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30) {
        DeviceInfo_30 deviceInfo_30 = new DeviceInfo_30();
        deviceInfo_30.setsSerialNumber(m_oNetDvrDeviceInfoV30.sSerialNumber);
        deviceInfo_30.setByAlarmInPortNum(m_oNetDvrDeviceInfoV30.byAlarmInPortNum);
        deviceInfo_30.setByAlarmOutPortNum(m_oNetDvrDeviceInfoV30.byAlarmOutPortNum);
        deviceInfo_30.setByDiskNum(m_oNetDvrDeviceInfoV30.byDiskNum);
        deviceInfo_30.setByDVRType(m_oNetDvrDeviceInfoV30.byDVRType);
        deviceInfo_30.setByChanNum(m_oNetDvrDeviceInfoV30.byChanNum);
        deviceInfo_30.setByStartChan(m_oNetDvrDeviceInfoV30.byStartChan);
        deviceInfo_30.setByAudioChanNum(m_oNetDvrDeviceInfoV30.byAudioChanNum);
        deviceInfo_30.setByIPChanNum(m_oNetDvrDeviceInfoV30.byIPChanNum);
        deviceInfo_30.setByZeroChanNum(m_oNetDvrDeviceInfoV30.byZeroChanNum);
        deviceInfo_30.setwDevType(m_oNetDvrDeviceInfoV30.wDevType);
        deviceInfo_30.setByStartDChan(m_oNetDvrDeviceInfoV30.byStartDChan);
        deviceInfo_30.setByStartDTalkChan(m_oNetDvrDeviceInfoV30.byStartDTalkChan);
        deviceInfo_30.setByHighDChanNum(m_oNetDvrDeviceInfoV30.byHighDChanNum);
        return deviceInfo_30;
    }


    // GUI init
    private boolean initActivity() {
        findViews();
        setListeners();
        return true;
    }

    private void setListeners() {
        mLoginBt.setOnClickListener(this);

        m_oPreviewBtn.setOnClickListener(this);

    }

    private void findViews() {
//        rockerView = (RockerView) findViewById(R.id.rockerView);
        mLoginBt = findViewById(R.id.login);
        ipAdd = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        m_oPreviewBtn = (Button) findViewById(R.id.btn_Preview);
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
