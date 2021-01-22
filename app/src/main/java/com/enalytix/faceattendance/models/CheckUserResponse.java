package com.enalytix.faceattendance.models;

import java.util.ArrayList;

public class CheckUserResponse {

    private String status;
    private String staffRole;
    private ArrayList<Site> sites;
    private String message;


    public CheckUserResponse() {
    }

    public CheckUserResponse(String status, String staffRole, ArrayList<Site> sites, String message) {
        this.status = status;
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


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
