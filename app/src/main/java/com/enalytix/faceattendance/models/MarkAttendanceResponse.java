package com.enalytix.faceattendance.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MarkAttendanceResponse implements Serializable {

    private String status;
    @SerializedName("transactionID")
    private String transactionId;
    private String empName;
    private String thumbnailImgUrl;

    public MarkAttendanceResponse() {
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getThumbnailImgUrl() {
        return thumbnailImgUrl;
    }

    public void setThumbnailImgUrl(String thumbnailImgUrl) {
        this.thumbnailImgUrl = thumbnailImgUrl;
    }
}
