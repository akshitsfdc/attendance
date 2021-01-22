package com.enalytix.faceattendance.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SendOTPResponse {

    private String status;
    private String employeeId;
    private String empCode;
    private String thumbnail;
    private String otp;
    private String staffRole;
    private ArrayList<Site> sites;
    private String message;

    public SendOTPResponse() {
    }

    public SendOTPResponse(String status, String employeeId, String empCode, String thumbnail, String otp, String staffRole, ArrayList<Site> sites, String message) {
        this.status = status;
        this.employeeId = employeeId;
        this.empCode = empCode;
        this.thumbnail = thumbnail;
        this.otp = otp;
        this.staffRole = staffRole;
        this.sites = sites;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getStaffRole() {
        return staffRole;
    }

    public void setStaffRole(String staffRole) {
        this.staffRole = staffRole;
    }

    public ArrayList<Site> getSites() {
        return sites;
    }

    public void setSites(ArrayList<Site> sites) {
        this.sites = sites;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
