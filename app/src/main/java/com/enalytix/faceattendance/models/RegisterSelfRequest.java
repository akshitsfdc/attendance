package com.enalytix.faceattendance.models;

import com.google.gson.annotations.SerializedName;

public class RegisterSelfRequest {

    @SerializedName("mobile_no")
    private String mobileNumber;
    @SerializedName("site_code")
    private String siteCode;

    public RegisterSelfRequest() {
    }

    public RegisterSelfRequest(String mobileNumber, String siteCode) {
        this.mobileNumber = mobileNumber;
        this.siteCode = siteCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
}
