package com.example.xs.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.xs.R;
import com.example.xs.mvp.model.PlaySurfaceViewInfo;
import com.example.xs.utils.DateUtil;
import com.example.xs.utils.GenerateId;
import com.example.xs.utils.GlobalUtil;
import com.example.xs.utils.HkSdkUtil;
import com.example.xs.utils.MsgUtil;
import com.example.xs.utils.ThreadUtil;
import com.example.xs.views.PlaySurfaceView;
import com.hikvision.netsdk.PTZCommand;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;

public class StartActivity extends Activity implements View.OnClickListener {

    private final String TAG = StartActivity.class.getName();

    private ImageButton mLogOutBt = null;
    private ImageButton mPlayAndStop = null;
    //播放按钮切换控制
    private boolean isOnPlay = false;
    //录制切换按钮控制
    private boolean isRecord = false;
    //回放视频按钮控制
    private boolean isRePlay = false;
    //是否支持云台操作
    private boolean isCommand = false;
    //播放句柄id
    private int playId = -1;
    //绝对路径
    private String basePath = "/sdcard/";
    //图片后缀
    private final static String JPG = ".jpg";
    //视频后缀
    private final static String MP4 = ".mp4";
    //时间录制计时器
    private Chronometer mTimer;
    //抓图
    private ImageButton mScreenshot;
    //录制
    private ImageButton mRecord;
    //视频播放控件
    private PlaySurfaceView playSurfaceView;
    //播放信息
    private PlaySurfaceViewInfo palyInfo;
    //云台操控布局
    private RelativeLayout mRelativeLayout;
    //开始选择回放时间
    private LinearLayout startLinearLayout;
    //结束 选择回放时间
    private LinearLayout endLinearLayout;

    //年/月/日
    private EditText mStartDatePickerTimeEditY;
    //时:分
    private EditText mStarTimePickerEditH;

    //回放按钮
    private ImageButton mReplay;

    private ImageButton left = null;

    private ImageButton leftUp = null;

    private ImageButton leftDown = null;

    private ImageButton right = null;

    private ImageButton rightUp = null;

    private ImageButton rightDown = null;

    private ImageButton up = null;


    private ImageButton down = null;

    private ImageButton reset = null;

    private ImageButton ytZoomIn = null;

    private ImageButton ytZoomOut = null;

    private QMUITopBar mTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onDetach");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findViews();
        linearChanged(true);
        setListeners();
        initTopBar();
        createView();
        Intent intent = getIntent();
        palyInfo = (PlaySurfaceViewInfo) intent.getSerializableExtra("playInfo");
    }

    private void findViews() {
        mStartDatePickerTimeEditY = findViewById(R.id.start_dp_time_edit_y);
        mStarTimePickerEditH = findViewById(R.id.start_tp_time_edit_h);
        mReplay = findViewById(R.id.replay);
        mRelativeLayout = findViewById(R.id.control_layout);
        mRecord = findViewById(R.id.record);
        mScreenshot = findViewById(R.id.sub_img);
        mTimer = findViewById(R.id.timer);
        mTimer.setVisibility(View.GONE);
        startLinearLayout = findViewById(R.id.start_layout);
        startLinearLayout.setVisibility(View.GONE);
        mTopBar = findViewById(R.id.topbar);
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

        ytZoomIn = findViewById(R.id.zoom_in);

        ytZoomOut = findViewById(R.id.zoom_out);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.replay:
                if (isOnPlay) {
                    MsgUtil.showDialogFail(this, "请先停止实施预览...");
                    return;
                }
                //在回放中
                if (!isRePlay) {
                    mReplay.setImageResource(R.mipmap.replay_down);
                    mStartDatePickerTimeEditY.setText(DateUtil.getCalendarNowZeroY());
                    mStarTimePickerEditH.setText(DateUtil.getCalendarZeroH());
                    startLinearLayout.setVisibility(View.VISIBLE);
                    isRePlay = true;
                } else {
                    mReplay.setImageResource(R.mipmap.replay);
                    startLinearLayout.setVisibility(View.GONE);
                    isRePlay = false;
                }
                break;
            case R.id.record:
                if (!isPlay()) {
                    return;
                }
                String path = basePath + DateUtil.getNow() + GenerateId.getUUid() + MP4;
                //开始录制
                if (!isRecord) {
                    if (HkSdkUtil.startRealData(playId, 0x2, path)) {
                        MsgUtil.showDialogSuccess(this, "开始录制...");
                        mRecord.setImageResource(R.mipmap.lz_down);
                        mTimer.setVisibility(View.VISIBLE);
                        mTimer.setBase(SystemClock.elapsedRealtime());//计时器清零
                        int hour = (int) ((SystemClock.elapsedRealtime() - mTimer.getBase()) / 1000 / 60);
                        mTimer.setFormat("0" + hour + ":%s");
                        mTimer.start();
                        isRecord = true;
                        return;
                    }
                    String msg = "录制失败..." + MsgUtil.errMsg();
                    int errLast = MsgUtil.errMsgLast();
                    if (errLast == 581) {
                        msg = getString(R.string.not_support);
                    }
                    MsgUtil.showDialogFail(this, msg);
                    //删除文件
                    File file = new File(path);
                    //删除手机中视频
                    if (!file.delete()) {
                        Log.i(TAG, "删除文件失败");
                    }
                } else {
                    if (HkSdkUtil.stopPlayVideo(playId)) {
                        MsgUtil.showDialogSuccess(this, "停止录制...");
                        mTimer.setVisibility(View.GONE);
                        mRecord.setImageResource(R.mipmap.record);
                        //停止录制
                        isRecord = false;
                        return;
                    }
                    MsgUtil.showDialogFail(this, "停止录制失败..." + MsgUtil.errMsg());
                }

                break;
            case R.id.sub_img:
                subImg(basePath, GenerateId.getUUid() + JPG);
                break;
            case R.id.play_and_stop:
                //如果在回放
                if (isRePlay) {
                    MsgUtil.showDialogFail(this, "请先关闭回放...");
                    return;
                }
                final Handler handler = new Handler();
                QMUITipDialog tipDialog = MsgUtil.tipDialog(this, !isOnPlay ? "视频加载中..." : "视频关闭中...", QMUITipDialog.Builder.ICON_TYPE_LOADING);
                tipDialog.show();
                if (!isOnPlay) {
                    ThreadUtil.startThread(new Runnable() {
                        @Override
                        public void run() {
                            playId = playSurfaceView.startPreview(GlobalUtil.loginInfo.getLoginId(), palyInfo.getPlayTartChan());
                        }
                    });
                    mPlayAndStop.setImageResource(R.mipmap.stop);
                    isOnPlay = true;
                    linearChanged(false);
                } else {
                    ThreadUtil.startThread(new Runnable() {
                        @Override
                        public void run() {
                            playSurfaceView.stopPreview(playId);
                        }
                    });
                    //暂停
                    mPlayAndStop.setImageResource(R.mipmap.play);
                    isOnPlay = false;
                    linearChanged(true);
                }
                MsgUtil.stopHandlerMsg(tipDialog, 2000);
                break;
            case R.id.login_out:
                showMessagePositiveDialog();
                break;
            case R.id.qmui_topbar_item_left_back:
                //如果播放中被返回 必须要关闭不然会一直重连
                if (isOnPlay) {
                    ThreadUtil.loadThread(this, "视频正在播放 正在关闭...", new Runnable() {
                        @Override
                        public void run() {
                            playSurfaceView.stopPreview(playId);
                        }
                    });
                }
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                this.finish();
                startActivity(intent);
                break;
        }
    }

    //隐藏元素
    public void linearChanged(boolean isChang) {
        if (isChang) {
            mScreenshot.setVisibility(View.INVISIBLE);
            ytZoomIn.setVisibility(View.INVISIBLE);
            ytZoomOut.setVisibility(View.INVISIBLE);
            mRecord.setVisibility(View.INVISIBLE);
            mRelativeLayout.setVisibility(View.GONE);
        } else {
            mScreenshot.setVisibility(View.VISIBLE);
            ytZoomIn.setVisibility(View.VISIBLE);
            ytZoomOut.setVisibility(View.VISIBLE);
            mRecord.setVisibility(View.VISIBLE);
            mRelativeLayout.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 抓图
     */
    public void subImg(String filePath, String fileName) {
        mScreenshot.setImageResource(R.mipmap.jt_down);
        if (!isPlay()) {
            return;
        }
        final QMUITipDialog tipDialog;
        String path = filePath + fileName;
        boolean isSub = HkSdkUtil.grabImgBlock(playId, path, 0);
        if (isSub) {
            tipDialog = MsgUtil.tipDialog(this, "抓图成功", QMUITipDialog.Builder.ICON_TYPE_SUCCESS);
        } else {
            tipDialog = MsgUtil.tipDialog(this, "抓图失败" + MsgUtil.errMsg(), QMUITipDialog.Builder.ICON_TYPE_FAIL);
        }
        tipDialog.show();
        closeSubImButton(tipDialog);
    }

    /**
     * 是否在播放
     *
     * @return
     */
    private boolean isPlay() {
        QMUITipDialog tipDialog;
        if (playId <= 0) {
            tipDialog = MsgUtil.tipDialog(this, "请先播放视频", QMUITipDialog.Builder.ICON_TYPE_FAIL);
            tipDialog.show();
            closeSubImButton(tipDialog);
            return false;
        }
        return true;
    }

    private void closeSubImButton(final QMUITipDialog tipDialog) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
                mScreenshot.setImageResource(R.mipmap.jt);
            }
        }, 2 * 1000);
    }


    /**
     * 创建播放视图
     */
    public void createView() {
        playSurfaceView = new PlaySurfaceView(this);
        DisplayMetrics metrics = new DisplayMetrics();
        RelativeLayout relativeLayout = findViewById(R.id.root_relative);
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        playSurfaceView.setParam(metrics.widthPixels, metrics.heightPixels / 2);
        playSurfaceView.setBackgroundColor(getResources().getColor(R.color.btn_filled_blue_bg_disabled));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, R.id.topbar);
        playSurfaceView.setId(R.id.id_surfaceView);
        //动态添加布局
        relativeLayout.addView(playSurfaceView, lp);

        //动态添加布局
        LinearLayout linearLayout = findViewById(R.id.play_layout);
        ((RelativeLayout.LayoutParams) linearLayout.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.id_surfaceView);
    }


    private void initTopBar() {
        QMUIAlphaImageButton leftBackImageButton = mTopBar.addLeftBackImageButton();
        leftBackImageButton.setOnClickListener(this);
        mTopBar.setBackgroundColor(getResources().getColor(R.color.qmui_btn_blue_bg));
        mTopBar.setBackgroundColor(getResources().getColor(R.color.barColor));
        mTopBar.setTitle("通道1").setTextColor(getResources().getColor(R.color.qmui_config_color_white));
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
        boolean isLogOut = HkSdkUtil.HkLogOut(GlobalUtil.loginInfo.getLoginId());
        if (!isLogOut) {
            tip = MsgUtil.tipDialog(this, "注销失败", QMUITipDialog.Builder.ICON_TYPE_FAIL);
            tip.show();
            MsgUtil.stopHandlerMsg(tip, 2000);
        }
        if (isLogOut) {
            GlobalUtil.loginInfo = null;
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            this.finish();
            startActivity(intent);
            //注销成功
        }
    }


    private void setListeners() {
        mPlayAndStop.setOnClickListener(this);
        mLogOutBt.setOnClickListener(this);
        mScreenshot.setOnClickListener(this);
        mRecord.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        ytZoomIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, PTZCommand.ZOOM_OUT, R.mipmap.add_down, R.mipmap.add, ytZoomIn);
                return true;
            }
        });

        ytZoomOut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, PTZCommand.ZOOM_OUT, R.mipmap.del_down, R.mipmap.del, ytZoomOut);
                return true;
            }
        });

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, PTZCommand.PAN_LEFT);
                return true;
            }
        });

        leftUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, PTZCommand.UP_LEFT, R.mipmap.left_up_red, R.mipmap.left_up, leftUp);
                return true;
            }
        });


        leftDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, PTZCommand.UP_LEFT);
                return true;
            }
        });


        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, PTZCommand.PAN_RIGHT);
                return true;
            }
        });


        rightUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, PTZCommand.UP_RIGHT);
                return true;
            }
        });


        rightDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, PTZCommand.DOWN_RIGHT);
                return true;
            }
        });


        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, PTZCommand.TILT_UP);
                return true;
            }
        });

        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, PTZCommand.TILT_DOWN);
                return true;
            }
        });

        reset.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCommand(event, PTZCommand.PAN_AUTO);
                return true;
            }
        });
    }

    private void setCommand(MotionEvent event, int command) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                PTZControl(command, 0);
                break;
            case MotionEvent.ACTION_UP:
                if (!isCommand) {
                    return;
                }
                PTZControl(command, 1);
                break;
        }
    }


    public void PTZControl(int dwPTZCommand, int dwStop) {
        isCommand = HkSdkUtil.cloudOpera(playId, dwPTZCommand, dwStop);
        if (!isCommand) {
            String msg = "操作失败..." + MsgUtil.errMsg();
            if (MsgUtil.errMsgLast() == 23) {
                msg = getString(R.string.not_support);
            }
            MsgUtil.showDialog(this, msg, QMUITipDialog.Builder.ICON_TYPE_FAIL, 1000L);
            return;
        }
        System.out.println("PTZControl  PAN_LEFT 0 succ");
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
    private void setCommand(MotionEvent event, int command, int downColor, int upColor, ImageButton imageButton) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                imageButton.setImageResource(downColor);
                PTZControl(command, 0);
                break;
            case MotionEvent.ACTION_UP:
                if (!isCommand) {
                    imageButton.setImageResource(upColor);
                    return;
                }
                PTZControl(command, 1);
                imageButton.setImageResource(upColor);
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

}
