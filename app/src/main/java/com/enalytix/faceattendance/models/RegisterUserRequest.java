package com.enalytix.faceattendance.models;

import com.google.gson.annotations.SerializedName;

public class RegisterUserRequest {

    @SerializedName("mobile_no")
    private String mobileNumber;
    @SerializedName("site_code")
    private String sideCode;
    @SerializedName("emp_name")
    private String empName;
    @SerializedName("emp_code")
    private String empCode;
    @SerializedName("front_img")
    private String frontImg;
    @SerializedName("left_img")
    private String leftImg;
    @SerializedName("right_img")
    private String rightImg;

    public RegisterUserRequest() {
    }

    public RegisterUserRequest(String mobileNumber, String sideCode, String empName, String empCode, String frontImg, String leftImg, String rightImg) {
        this.setMobileNumber(mobileNumber);
        this.setSideCode(sideCode);
        this.setEmpName(empName);
        this.setEmpCode(empCode);
        this.setFrontImg(frontImg);
        this.setLeftImg(leftImg);
        this.setRightImg(rightImg);
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getSideCode() {
        return sideCode;
    }

    public void setSideCode(String sideCode) {
        this.sideCode = sideCode;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getFrontImg() {
        return frontImg;
    }

    public void setFrontImg(String frontImg) {
        this.frontImg = frontImg;
    }

    public String getLeftImg() {
        return leftImg;
    }

    public void setLeftImg(String leftImg) {
        this.leftImg = leftImg;
    }

    public String getRightImg() {
        return rightImg;
    }

    public void setRightImg(String rightImg) {
        this.rightImg = rightImg;
    }
}
