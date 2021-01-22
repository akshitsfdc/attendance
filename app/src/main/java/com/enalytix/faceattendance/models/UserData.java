package com.enalytix.faceattendance.models;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class UserData {

    private String status;
    private String employeeId;
    private String empCode;
    private String thumbnail;
    private String otp;
    private String staffRole;
    private ArrayList<Site> sites;
    private int attendanceTry;
    private String mobileNumber;

    public UserData() {
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

    @NonNull
    @Override
    public String toString() {

        return "toString: Status : "+this.getStatus()+" Otp: "+this.getOtp()
                +" EmpCode: "+this.getEmpCode()+" EmployeeId: "+this.getEmployeeId()+" StaffRole: "+this.getStaffRole()+" Thumbnail: "+this.getThumbnail()
                +" siteIds; "+this.getSites();
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getAttendanceTry() {
        return attendanceTry;
    }

    public void setAttendanceTry(int attendanceTry) {
        this.attendanceTry = attendanceTry;
    }
}