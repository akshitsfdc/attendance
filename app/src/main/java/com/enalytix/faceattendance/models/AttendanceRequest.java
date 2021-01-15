package com.enalytix.faceattendance.models;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class AttendanceRequest {

    private byte[] file;
    private String id;

    @SerializedName("siteid")
    private String siteId;

    public AttendanceRequest(byte[] file, String id, String siteId) {
        this.file = file;
        this.id = id;
        this.siteId = siteId;
    }

    public AttendanceRequest() {
    }


    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}
