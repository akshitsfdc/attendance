package com.enalytix.faceattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.models.UserData;
import com.enalytix.faceattendance.utils.FileUtils;
import com.enalytix.faceattendance.utils.Routing;

public class MainActivity extends BaseActivity {

    public static UserData USER_DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        super.context = this;
//        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();

        USER_DATA = new UserData();

        routing = new Routing(this);
        int SPLASH_DISPLAY_LENGTH = 1000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(checkLogin()){
                    routing.navigate(EmployeeHome.class, true);
                }else {
                    routing.navigate(LoginActivity.class, true);
                }

            }
        }, SPLASH_DISPLAY_LENGTH);


    }

    private boolean checkLogin(){
        FileUtils fileUtils = new FileUtils(this);
        UserData userData = fileUtils.getUserDataFromSharedPre();
        if(userData != null){
            MainActivity.USER_DATA = userData;
        }
        return userData != null;
    }
}