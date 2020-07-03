package com.example.xs.bean;

import java.io.Serializable;

public class DeviceInfo_30 implements Serializable {
    public byte[] sSerialNumber = new byte[48];
    public byte byAlarmInPortNum;
    public byte byAlarmOutPortNum;
    public byte byDiskNum;
    public byte byDVRType;
    public byte byChanNum;
    public byte byStartChan;
    public byte byAudioChanNum;
    public int byIPChanNum;
    public byte byZeroChanNum;
    public int wDevType;
    public byte byStartDChan;
    public byte byStartDTalkChan;
    public byte byHighDChanNum;

    public DeviceInfo_30() {
    }

    public byte[] getsSerialNumber() {
        return sSerialNumber;
    }

    public void setsSerialNumber(byte[] sSerialNumber) {
        this.sSerialNumber = sSerialNumber;
    }

    public byte getByAlarmInPortNum() {
        return byAlarmInPortNum;
    }

    public void setByAlarmInPortNum(byte byAlarmInPortNum) {
        this.byAlarmInPortNum = byAlarmInPortNum;
    }

    public byte getByAlarmOutPortNum() {
        return byAlarmOutPortNum;
    }

    public void setByAlarmOutPortNum(byte byAlarmOutPortNum) {
        this.byAlarmOutPortNum = byAlarmOutPortNum;
    }

    public byte getByDiskNum() {
        return byDiskNum;
    }

    public void setByDiskNum(byte byDiskNum) {
        this.byDiskNum = byDiskNum;
    }

    public byte getByDVRType() {
        return byDVRType;
    }

    public void setByDVRType(byte byDVRType) {
        this.byDVRType = byDVRType;
    }

    public byte getByChanNum() {
        return byChanNum;
    }

    public void setByChanNum(byte byChanNum) {
        this.byChanNum = byChanNum;
    }

    public byte getByStartChan() {
        return byStartChan;
    }

    public void setByStartChan(byte byStartChan) {
        this.byStartChan = byStartChan;
    }

    public byte getByAudioChanNum() {
        return byAudioChanNum;
    }

    public void setByAudioChanNum(byte byAudioChanNum) {
        this.byAudioChanNum = byAudioChanNum;
    }

    public int getByIPChanNum() {
        return byIPChanNum;
    }

    public void setByIPChanNum(int byIPChanNum) {
        this.byIPChanNum = byIPChanNum;
    }

    public byte getByZeroChanNum() {
        return byZeroChanNum;
    }

    public void setByZeroChanNum(byte byZeroChanNum) {
        this.byZeroChanNum = byZeroChanNum;
    }

    public int getwDevType() {
        return wDevType;
    }

    public void setwDevType(int wDevType) {
        this.wDevType = wDevType;
    }

    public byte getByStartDChan() {
        return byStartDChan;
    }

    public void setByStartDChan(byte byStartDChan) {
        this.byStartDChan = byStartDChan;
    }

    public byte getByStartDTalkChan() {
        return byStartDTalkChan;
    }

    public void setByStartDTalkChan(byte byStartDTalkChan) {
        this.byStartDTalkChan = byStartDTalkChan;
    }

    public byte getByHighDChanNum() {
        return byHighDChanNum;
    }

    public void setByHighDChanNum(byte byHighDChanNum) {
        this.byHighDChanNum = byHighDChanNum;
    }
}
