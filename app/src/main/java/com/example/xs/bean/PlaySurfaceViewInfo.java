package com.example.xs.bean;

import java.io.Serializable;

public class PlaySurfaceViewInfo implements Serializable {
    /**
     * 播放视频id
     */
    private int playId;
    /**
     * 播放id 代码
     */
    private int playCode;

    public int getPlayId() {
        return playId;
    }

    public void setPlayId(int playId) {
        this.playId = playId;
    }

    public PlaySurfaceViewInfo(int playId) {
        this.playId = playId;
    }

    public PlaySurfaceViewInfo() {

    }

    public int getPlayCode() {
        return playCode;
    }

    public void setPlayCode(int playCode) {
        this.playCode = playCode;
    }
}
