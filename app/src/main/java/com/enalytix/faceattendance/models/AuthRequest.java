package com.enalytix.faceattendance.models;

public class AuthRequest {

    private String mobile_no;

    public AuthRequest() {
    }

    public AuthRequest(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }
}
