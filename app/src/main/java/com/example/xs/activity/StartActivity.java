package com.example.xs.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.xs.R;
import com.example.xs.bean.LoginInfo;
import com.example.xs.utils.MsgUtil;
import com.example.xs.utils.PTZControlUtil;
import com.example.xs.views.PlaySurfaceView;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.PTZCommand;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class StartActivity extends Activity implements View.OnClickListener, Callback {

    private PlaySurfaceView[] playView = new PlaySurfaceView[4];

    private final String TAG = StartActivity.class.getName();

    private int m_iLogID = -1;
    private int m_iStartChan = 0; // start channel number
    private int m_iChanNum = 0; // channel number


    private LoginInfo loginInfo = null;

    private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V40
    private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime

    private ImageButton mLogOutBt = null;
    private ImageButton mPlayAndStop = null;

    private boolean m_bMultiPlay = false;


    private ImageButton left = null;

    private ImageButton leftUp = null;

    private ImageButton leftDown = null;

    private ImageButton right = null;

    private ImageButton rightUp = null;

    private ImageButton rightDown = null;


    private ImageButton up = null;


    private ImageButton down = null;

    private ImageButton reset = null;

    private ImageButton zoomIn = null;


    private ImageButton zoomOut = null;

    private int lRealHandle = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findViews();
        setListeners();
        Intent intent = getIntent();
        loginInfo = (LoginInfo) intent.getSerializableExtra("loginInfo");
        if (loginInfo == null) {
            return;
        }
        m_iLogID = loginInfo.getLoginId();
        m_iChanNum = loginInfo.getM_iChanNum();
        m_iStartChan = loginInfo.getM_iStartChan();
        if (m_iChanNum > 1) {
            ChangeSingleSurFace(false);
        } else {
            ChangeSingleSurFace(true);
        }
        ExceptionCallBack oexceptionCbf = getExceptiongCbf();
        if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf)) {
            Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
        }
    }


    /**
     * @return exception instance
     * @fn getExceptiongCbf
     * @author zhuzhenlei
     * @brief process exception
     */
    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_and_stop:
                if (m_iLogID < 0) {
                    Toast.makeText(this, "设备已注销", Toast.LENGTH_SHORT).show();
                    break;
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
                }
                break;
            case R.id.login_out:
                showMessagePositiveDialog();
                break;
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

    //    注销
    private void LogOut() {
        final QMUITipDialog tip;
        boolean isLogOut = HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID);
        if (!isLogOut) {
            tip = MsgUtil.tipDialog(this, "注销失败", QMUITipDialog.Builder.ICON_TYPE_FAIL);
            tip.show();
            MsgUtil.stopHandlerMsg(tip, 2000);
        }
        if (isLogOut) {
            m_iLogID = -1;
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            this.finish();
            startActivity(intent);
            //注销成功
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


    private void ChangeSingleSurFace(boolean bSingle) {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        for (int i = 0; i < 4; i++) {
            if (playView[i] == null) {
                playView[i] = new PlaySurfaceView(this);
                playView[i].setParam(metric.widthPixels);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = playView[i].getM_iHeight() - (i / 2)
                        * playView[i].getM_iHeight();
                params.leftMargin = (i % 2) * playView[i].getM_iWidth();
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        m_osurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
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


    private void setListeners() {
        mPlayAndStop.setOnClickListener(this);
        mLogOutBt.setOnClickListener(this);

        zoomIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, lRealHandle, PTZCommand.ZOOM_IN);
                return true;
            }
        });

        zoomOut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, lRealHandle, PTZCommand.ZOOM_OUT);
                return true;
            }
        });

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, lRealHandle, PTZCommand.PAN_LEFT);
                return true;
            }
        });

        leftUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, lRealHandle, PTZCommand.UP_LEFT, R.mipmap.left_up_red, R.mipmap.left_up, leftUp);
                return true;
            }
        });


        leftDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, lRealHandle, PTZCommand.UP_LEFT);
                return true;
            }
        });


        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, lRealHandle, PTZCommand.PAN_RIGHT);
                return true;
            }
        });


        rightUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, lRealHandle, PTZCommand.UP_RIGHT);
                return true;
            }
        });


        rightDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, lRealHandle, PTZCommand.DOWN_RIGHT);
                return true;
            }
        });


        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, lRealHandle, PTZCommand.TILT_UP);
                return true;
            }
        });

        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, lRealHandle, PTZCommand.TILT_DOWN);
                return true;
            }
        });

        reset.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, lRealHandle, PTZCommand.PAN_AUTO);
                return true;
            }
        });
    }

    private void setCommand(MotionEvent event, int handel, int command) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                PTZControlUtil.PTZControl(handel, command, 0);
                break;
            case MotionEvent.ACTION_UP:
                PTZControlUtil.PTZControl(handel, command, 1);
                break;
        }
    }

    /**
     * '
     *
     * @param event
     * @param handel
     * @param command     sdk执行的命令
     * @param downColor   按下去的图标
     * @param upColor     抬起的图标
     * @param imageButton
     */
    private void setCommand(MotionEvent event, int handel, int command, int downColor, int upColor, ImageButton imageButton) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                imageButton.setImageResource(downColor);
                PTZControlUtil.PTZControl(handel, command, 0);
                break;
            case MotionEvent.ACTION_UP:
                PTZControlUtil.PTZControl(handel, command, 1);
                imageButton.setImageResource(upColor);
                break;
        }
    }

    private void findViews() {
        reset = findViewById(R.id.reset);
        left = findViewById(R.id.left);
        leftUp = findViewById(R.id.left_up);
        leftDown = findViewById(R.id.left_down);
        right = findViewById(R.id.right);
        rightUp = findViewById(R.id.right_up);
        rightDown = findViewById(R.id.right_down);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);

        mPlayAndStop = findViewById(R.id.play_and_stop);
        mLogOutBt = findViewById(R.id.login_out);

        zoomIn = findViewById(R.id.zoom_in);

        zoomOut = findViewById(R.id.zoom_out);

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
