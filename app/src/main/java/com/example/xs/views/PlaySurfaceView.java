package com.example.xs.views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.xs.utils.HkSdkUtil;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;

public class PlaySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    //布局的宽
    private int mIWidth = 0;
    //布局的高
    private int mIHeight = 0;

    public SurfaceHolder mhHolder;


    public boolean bCreate = false;

    public int getmIWidth() {
        return mIWidth;
    }

    public void setmIWidth(int mIWidth) {
        this.mIWidth = mIWidth;
    }

    public int getmIHeight() {
        return mIHeight;
    }

    public void setmIHeight(int mIHeight) {
        this.mIHeight = mIHeight;
    }

    public SurfaceHolder getMhHolder() {
        return mhHolder;
    }

    public void setMhHolder(SurfaceHolder mhHolder) {
        this.mhHolder = mhHolder;
    }

    public String TAG = "PlaySurfaceView";

    public PlaySurfaceView(Context context) {
        super(context);
        mhHolder = this.getHolder();
        mhHolder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        mhHolder.setFormat(PixelFormat.TRANSLUCENT);
        System.out.println("surfaceChanged");
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        bCreate = true;
        System.out.println("surfaceCreated");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        System.out.println("surfaceDestroyed");
        bCreate = false;

    }

    public void setParam(int iWidth, int iHeight) {
        mIHeight = iHeight;
        mIWidth = iWidth;
        mhHolder.setFixedSize(mIWidth, mIHeight);//分辨率
    }

    public int startPreview(int iLoginId, int iChan) {
        Log.i(TAG, "preview channel:" + iChan);
        while (!bCreate) {
            try {
                Thread.sleep(200);
                Log.i(TAG, "wait for surface create");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = iChan;//通道号
        previewInfo.dwStreamType = 0; //码流类型：0-主码流，1-子码流，
        previewInfo.bBlocked = 1; //0- 非阻塞取流，1- 阻塞取流
        previewInfo.hHwnd = mhHolder; //播放窗口的句柄
        int iPreviewHandle = HkSdkUtil.playVideo(iLoginId, previewInfo, null);
        // 窗口句柄
        if (iPreviewHandle < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HkSdkUtil.HkErrorNumber());
            return -1;
        }
        return iPreviewHandle;

    }

    public boolean stopPreview(int iPreviewHandle) {
        return HkSdkUtil.stopPlayVideo(iPreviewHandle);
    }

}
