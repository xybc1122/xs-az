package com.example.xs.bean;

import java.io.Serializable;

public class LoginInfo implements Serializable {

    private Integer loginId;

    private DeviceInfo_30 deviceInfo_30;

    public LoginInfo(Integer loginId, DeviceInfo_30 deviceInfo_30) {
        this.loginId = loginId;
        this.deviceInfo_30 = deviceInfo_30;
    }

    public LoginInfo() {
    }

    public Integer getLoginId() {
        return loginId;
    }

    public void setLoginId(Integer loginId) {
        this.loginId = loginId;
    }


    public DeviceInfo_30 getDeviceInfo_30() {
        return deviceInfo_30;
    }

    public void setDeviceInfo_30(DeviceInfo_30 deviceInfo_30) {
        this.deviceInfo_30 = deviceInfo_30;
    }
}
