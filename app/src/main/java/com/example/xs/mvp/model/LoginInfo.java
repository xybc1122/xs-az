package com.example.xs.mvp.model;

import java.io.Serializable;

public class LoginInfo implements Serializable {

    private Integer loginId;

    private int m_iStartChan = 0; // start channel number
    private int m_iChanNum = 0; // channel number


    public LoginInfo() {
    }

    public LoginInfo(Integer loginId, int m_iStartChan, int m_iChanNum) {
        this.loginId = loginId;
        this.m_iStartChan = m_iStartChan;
        this.m_iChanNum = m_iChanNum;
    }

    public Integer getLoginId() {
        return loginId;
    }

    public void setLoginId(Integer loginId) {
        this.loginId = loginId;
    }

    public int getM_iStartChan() {
        return m_iStartChan;
    }

    public void setM_iStartChan(int m_iStartChan) {
        this.m_iStartChan = m_iStartChan;
    }

    public int getM_iChanNum() {
        return m_iChanNum;
    }

    public void setM_iChanNum(int m_iChanNum) {
        this.m_iChanNum = m_iChanNum;
    }
}
