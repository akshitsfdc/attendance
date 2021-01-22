package com.enalytix.faceattendance.models;

public class AttendanceModel implements Comparable {


    private String siteCode;
    private String siteName;
    private String empCode;
    private String empName;
    private String date;
    private String totalHour;
    private String colorCode;
    private String attendancePer;
    private int dateValue;

    public AttendanceModel() {
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalHour() {
        return totalHour;
    }

    public void setTotalHour(String totalHour) {
        this.totalHour = totalHour;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getAttendancePer() {
        return attendancePer;
    }

    public void setAttendancePer(String attendancePer) {
        this.attendancePer = attendancePer;
    }


    @Override
    public int compareTo(Object o) {

        AttendanceModel attendanceModel = (AttendanceModel) o;

        return this.dateValue - attendanceModel.getDateValue();
    }

    public int getDateValue() {
        return dateValue;
    }

    public void setDateValue(int dateValue) {
        this.dateValue = dateValue;
    }
}
