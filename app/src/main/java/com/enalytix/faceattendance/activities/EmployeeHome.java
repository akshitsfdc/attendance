package com.enalytix.faceattendance.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.models.Site;
import com.enalytix.faceattendance.services.GeoService;
import com.enalytix.faceattendance.services.PermissionsServices;
import com.enalytix.faceattendance.utils.Routing;
import com.enalytix.faceattendance.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

public class EmployeeHome extends BaseActivity {

    private ImageView profilePicture;
    private TextView siteTitle;
    private TextView timeLabel;
    private UIUtils uiUtils;

    private RelativeLayout loginWrapper;
    private RelativeLayout logoutWrapper;
    private GeoService geoService;
    private PermissionsServices permissionsServices;

    double lat = 29.3835;
    double longg = 78.1324;

    private float range = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);


        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();
        super.setUIUtils(R.id.parent);

//        super.setThumbnail();
//        super.setTitle();
        super.setCurrentDate();


        permissionsServices = new PermissionsServices(this);
        geoService = new GeoService(this);

        loginWrapper = findViewById(R.id.loginWrapper);
        logoutWrapper = findViewById(R.id.logoutWrapper);


        loginWrapper.setOnClickListener(v -> {
            goToLogin();
        });
        logoutWrapper.setOnClickListener(v -> {
            goToLogout();
        });

        permissionsServices.askForAccessFineLocationPermission();
        permissionsServices.askForAccessCoarseLocationPermission();

    }


    private void goToLogin(){
        if(geoService.isWithinRange(range, lat, longg)){
            routing.navigate(RecordAttendanceActivity.class, false);
        }else{
            routing.navigate(NotAtLocationActivity.class, false);
        }


    }

    private void goToLogout(){
        if(geoService.isWithinRange(range, lat, longg)){
            routing.navigate(RecordAttendanceActivity.class, false);
        }else{
            routing.navigate(NotAtLocationActivity.class, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}