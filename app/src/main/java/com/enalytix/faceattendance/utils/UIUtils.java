package com.enalytix.faceattendance.utils;

import android.app.Activity;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class UIUtils {

    private Activity activity;
    private View parentView;

    public UIUtils(Activity activity){
        this.activity = activity;
    }
    public UIUtils(Activity activity, int parentResourceId){
        this.activity = activity;
        this.parentView = this.activity.findViewById(parentResourceId);
    }
    public void setParentView( int parentResourceId){
        this.parentView = this.activity.findViewById(parentResourceId);
    }
    public  void showShortSnakeBar(String msg){
        Snackbar.make(this.parentView, msg, Snackbar.LENGTH_SHORT).show();
    }
    public  void showLongSnakeBar(String msg){
        Snackbar.make(this.parentView, msg, Snackbar.LENGTH_LONG).show();
    }

    public String getMonthFromIndex(int index){
        String[] months= {
                "JAN", "FAB", "MAR", "APR", "MAY", "JUN", "JUL", "AGU", "SEPT", "OCT", "NOV", "DEC"
        };
        return months[index];
    }
    public String getAmOrPm(int index){
        String[] am_pm = {"AM", "PM"};

        return am_pm[index];
    }
}
