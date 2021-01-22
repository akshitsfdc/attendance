package com.enalytix.faceattendance.models;

public class CancelAttendanceResponse {


    private String status;
    private String transactionId;
    private String empName;
    private String thumbnailImgUrl;

    public CancelAttendanceResponse() {
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
