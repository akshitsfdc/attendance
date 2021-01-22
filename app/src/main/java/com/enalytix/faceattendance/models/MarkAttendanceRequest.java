package com.enalytix.faceattendance.models;

import com.google.gson.annotations.SerializedName;

public class MarkAttendanceRequest {

    @SerializedName("site_code")
    private String sideCode;
    @SerializedName("mobile_no")
    private String mobileNumber;
    @SerializedName("attendance_type")
    private String attendanceType;
    @SerializedName("attendance_datetime")
    private String attendanceDateTime;
    @SerializedName("submited_by")
    private int submittedBy;

    public MarkAttendanceRequest() {
    }

    public MarkAttendanceRequest(String sideCode, String mobileNumber, String attendanceType, String attendanceDateTime, int submittedBy) {
        this.setSideCode(sideCode);
        this.setMobileNumber(mobileNumber);
        this.setAttendanceType(attendanceType);
        this.setAttendanceDateTime(attendanceDateTime);
        this.setSubmittedBy(submittedBy);
    }


    public String getSideCode() {
        return sideCode;
    }

    public void setSideCode(String sideCode) {
        this.sideCode = sideCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getAttendanceDateTime() {
        return attendanceDateTime;
    }

    public void setAttendanceDateTime(String attendanceDateTime) {
        this.attendanceDateTime = attendanceDateTime;
    }

    public int getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(int submittedBy) {
        this.submittedBy = submittedBy;
    }
}
