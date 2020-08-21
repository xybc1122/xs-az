package com.example.xs.mvp.model;

import java.io.Serializable;

public class PlaySurfaceViewInfo implements Serializable {
    /**
     * 播放视频 id NET_DVR_RealPlay_V40
     */
    private int playId;
    /**
     * 播放id 代码 m_iStartChan
     */
    private int playTartChan;
    /**
     * 是否能播放
     */
    private boolean isPlay;


    public boolean getIsPlay() {
        return isPlay;
    }

    public void setIsPlay(boolean play) {
        isPlay = play;
    }

    public int getPlayId() {
        return playId;
    }

    public void setPlayId(int playId) {
        this.playId = playId;
    }

    public PlaySurfaceViewInfo(int playTartChan) {
        this.playTartChan = playTartChan;
    }

    public PlaySurfaceViewInfo() {

    }

    public int getPlayTartChan() {
        return playTartChan;
    }

    public void setPlayTartChan(int playTartChan) {
        this.playTartChan = playTartChan;
    }
}
