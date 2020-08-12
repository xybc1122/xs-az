package com.example.xs.views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;

public class PlaySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private int m_iWidth = 0;
    private int m_iHeight = 0;

    public SurfaceHolder m_hHolder;
    public boolean bCreate = false;

    public String TAG = "PlaySurfaceView";

    public PlaySurfaceView(Context context) {
        super(context);
        m_hHolder = this.getHolder();
        m_hHolder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        m_hHolder.setFormat(PixelFormat.TRANSLUCENT);
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
        m_iHeight = iHeight;
        m_iWidth = iWidth;
        m_hHolder.setFixedSize(m_iWidth, m_iHeight);//分辨率
    }

    public int startPreview(int iUserID, int iChan) {
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
        previewInfo.hHwnd = m_hHolder; //播放窗口的句柄
        int iPreviewHandle = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iUserID, previewInfo, null);
        // 窗口句柄
        if (iPreviewHandle < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        return iPreviewHandle;

    }

    public void stopPreview(int iPreviewHandle) {
        HCNetSDK.getInstance().NET_DVR_StopRealPlay(iPreviewHandle);
    }

}
