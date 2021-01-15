package com.enalytix.faceattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.models.UserData;
import com.enalytix.faceattendance.utils.Routing;

public class MainActivity extends AppCompatActivity {

    public static UserData USER_DATA;
    private Routing routing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        USER_DATA = new UserData();

        routing = new Routing(this);
        int SPLASH_DISPLAY_LENGTH = 1000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                routing.navigate(LoginActivity.class, true);
            }
        }, SPLASH_DISPLAY_LENGTH);


    }
}