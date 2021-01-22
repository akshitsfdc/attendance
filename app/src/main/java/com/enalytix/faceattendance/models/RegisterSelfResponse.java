package com.enalytix.faceattendance.models;

public class RegisterSelfResponse {

    private String status;

    public RegisterSelfResponse() {
    }

    public RegisterSelfResponse(String status) {
        this.setStatus(status);
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
