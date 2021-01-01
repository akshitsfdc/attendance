package com.enalytix.faceattendance.utils;

import android.app.Activity;
import android.content.Intent;

import com.enalytix.faceattendance.activities.LoginActivity;

public class Routing {

    private Activity activity;

    public Routing(Activity activity){
        this.activity = activity;
    }

    public <T> void navigate(Class<T> next, boolean shouldFinish){
        Intent i = new Intent(activity, next);
        activity.startActivity(i);

        if(shouldFinish) {
            activity.finish();
        }
    }

}
