package com.enalytix.faceattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.utils.Routing;

public class MainActivity extends AppCompatActivity {

    private Routing routing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routing = new Routing(this);

        setContentView(R.layout.activity_main);

        int SPLASH_DISPLAY_LENGTH = 1000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                routing.navigate(LoginActivity.class, true);
            }
        }, SPLASH_DISPLAY_LENGTH);


    }
}