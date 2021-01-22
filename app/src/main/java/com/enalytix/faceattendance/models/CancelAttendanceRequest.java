package com.enalytix.faceattendance.models;

import com.google.gson.annotations.SerializedName;

public class CancelAttendanceRequest {

    @SerializedName("transaction_id")
    private String transactionId;

    public CancelAttendanceRequest() {
    }

    public CancelAttendanceRequest(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
