package com.enalytix.faceattendance.models;

public class RegisterUserResponse {

    private String status;

    public RegisterUserResponse() {
    }

    public RegisterUserResponse(String status) {
        this.setStatus(status);
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
