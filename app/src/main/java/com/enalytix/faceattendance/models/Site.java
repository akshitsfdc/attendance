package com.enalytix.faceattendance.models;

import androidx.annotation.NonNull;

public class Site {


    private String siteCode;
    private String siteLatitude;
    private String siteLongitude;
    private String siteName;
    private float geocodingArea;

    public Site() {
    }


    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteLatitude() {
        return siteLatitude;
    }

    public void setSiteLatitude(String siteLatitude) {
        this.siteLatitude = siteLatitude;
    }

    public String getSiteLongitude() {
        return siteLongitude;
    }

    public void setSiteLongitude(String siteLongitude) {
        this.siteLongitude = siteLongitude;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @NonNull
    @Override
    public String toString() {
        return " SiteCode: "+this.getSiteCode()+" SiteLatitude: "+this.getSiteLatitude()+" SiteLongitude: "+this.getSiteLongitude()+" SiteName: "+this.getSiteName();
    }

    public float getGeocodingArea() {
        return geocodingArea;
    }

    public void setGeocodingArea(float geocodingArea) {
        this.geocodingArea = geocodingArea;
    }
}
