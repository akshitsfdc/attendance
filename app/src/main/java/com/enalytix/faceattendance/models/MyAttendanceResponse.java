package com.enalytix.faceattendance.models;

import java.util.List;

public class MyAttendanceResponse {

    private List<AttendanceModel> daily;
    private AttendanceModel monthly;
    private String error;

    public MyAttendanceResponse() {
    }


    public List<AttendanceModel> getDaily() {
        return daily;
    }

    public void setDaily(List<AttendanceModel> daily) {
        this.daily = daily;
    }

    public AttendanceModel getMonthly() {
        return monthly;
    }

    public void setMonthly(AttendanceModel monthly) {
        this.monthly = monthly;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
