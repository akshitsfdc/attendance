package com.enalytix.faceattendance.models;

import com.google.gson.annotations.SerializedName;

public class MyAttendanceRequest {

    @SerializedName("mobile_no")
    private String mobileNumber;

    @SerializedName("mon_year")
    private String monthYear;


    public MyAttendanceRequest() {
    }

    public MyAttendanceRequest(String mobileNumber, String monthYear) {
        this.setMobileNumber(mobileNumber);
        this.setMonthYear(monthYear);
    }


    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }
}
