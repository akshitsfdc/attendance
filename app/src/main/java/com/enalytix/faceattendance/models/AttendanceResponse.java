package com.enalytix.faceattendance.models;

import com.google.gson.annotations.SerializedName;

public class AttendanceResponse {


    @SerializedName("Id")
    private String id;
    private boolean isFaceVisible;
    private String error;

    public AttendanceResponse() {
    }

    public AttendanceResponse(String id, boolean isFaceVisible, String error) {
        this.setId(id);
        this.setFaceVisible(isFaceVisible);
        this.setError(error);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFaceVisible() {
        return isFaceVisible;
    }

    public void setFaceVisible(boolean faceVisible) {
        isFaceVisible = faceVisible;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
