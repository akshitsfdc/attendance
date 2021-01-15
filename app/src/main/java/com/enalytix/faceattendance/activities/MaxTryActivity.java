package com.enalytix.faceattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.enalytix.faceattendance.R;

public class MaxTryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_try);
        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setUIUtils(R.id.parent);
        super.setObjects();

        super.setCurrentDate();

    }
}