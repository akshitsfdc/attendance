package com.enalytix.faceattendance.activities;
import android.os.Bundle;

import com.enalytix.faceattendance.R;


public class RegisterAgainResponseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_again_response);

        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();
        super.setUIUtils(R.id.attParent);

        super.setThumbnail();
        super.setTitle();
        super.setCurrentDate();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        routing.navigate(EmployeeHome.class, true);

    }
}