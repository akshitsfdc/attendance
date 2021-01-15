package com.enalytix.faceattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.enalytix.faceattendance.R;

public class NotAtLocationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_at_location);

        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();
//        super.setUIUtils(R.id.preview);

//        super.setThumbnail();
//        super.setTitle();

    }
}