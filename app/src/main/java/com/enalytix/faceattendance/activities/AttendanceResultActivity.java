package com.enalytix.faceattendance.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.models.CancelAttendanceResponse;
import com.enalytix.faceattendance.models.MarkAttendanceResponse;
import com.enalytix.faceattendance.models.RegisterSelfResponse;
import com.enalytix.faceattendance.models.Site;
import com.enalytix.faceattendance.services.AuthService;
import com.enalytix.faceattendance.utils.FileUtils;
import com.enalytix.faceattendance.utils.Routing;
import com.enalytix.faceattendance.utils.UIUtils;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceResultActivity extends BaseActivity {

    private static String TAG = "AttendanceResult";

    private ImageView posBigImage;
    private ImageView posSmallImage;
    private ImageView negPosBigImage;
    private RelativeLayout resultNegativeWrapper;
    private RelativeLayout resultPositiveWrapper;
    private Button ngRegisterNow;
    private Button reCaptureButton;
    private Button cancelAttButton;
    private MarkAttendanceResponse markAttendanceResponse;
    private int maxAttendanceTry = 3;
    private TextView phoneNumber;
    private TextView empName;
    private TextView siteTitle;
    private TextView updateReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_result);

        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();
        super.setUIUtils(R.id.parent);

        super.setThumbnail();
//        super.setTitle();
        super.setCurrentDate();

        posBigImage = findViewById(R.id.posBigImage);
        posSmallImage = findViewById(R.id.posSmallImage);
        resultNegativeWrapper = findViewById(R.id.resultNegativeWrapper);
        resultPositiveWrapper = findViewById(R.id.resultPositiveWrapper);
        negPosBigImage = findViewById(R.id.negPosBigImage);
        ngRegisterNow = findViewById(R.id.ngRegisterNow);
        reCaptureButton = findViewById(R.id.reCaptureButton);
        cancelAttButton = findViewById(R.id.cancelAttButton);
        phoneNumber = findViewById(R.id.phoneNumber);
        empName = findViewById(R.id.empName);
        siteTitle = findViewById(R.id.siteTitle);
        updateReg = findViewById(R.id.updateReg);


        String title = (String)routing.getParam("siteName");
        super.setCustomTitle(title);
        boolean isSuccess = (boolean)routing.getParam("isSuccess");
        if(isSuccess){
            resultNegativeWrapper.setVisibility(View.GONE);
            resultPositiveWrapper.setVisibility(View.VISIBLE);

            markAttendanceResponse = (MarkAttendanceResponse)routing.getParam("attData");

            setBitmap("xyz", true);


            MainActivity.USER_DATA.setAttendanceTry(0);
            if(!fileUtils.saveUserDataInSharedPre(MainActivity.USER_DATA)){
                Log.d(TAG, "onCreate: user data save failed.");
            }
            setLabels();
        }else {
            resultNegativeWrapper.setVisibility(View.VISIBLE);
            resultPositiveWrapper.setVisibility(View.GONE);
            MainActivity.USER_DATA.setAttendanceTry(MainActivity.USER_DATA.getAttendanceTry()+1);
            if(MainActivity.USER_DATA.getAttendanceTry() >= maxAttendanceTry){
                if(!fileUtils.saveUserDataInSharedPre(MainActivity.USER_DATA)){
                    Log.d(TAG, "onCreate: user data save failed.");
                }
            }
            setBitmap("xyz", false);
        }

        ngRegisterNow.setOnClickListener(v -> {
            registerSelf();
//            super.routing.navigate(RegisterAgainResponseActivity.class, false);
        });
        updateReg.setOnClickListener(v -> {
            registerSelf();
        });
        reCaptureButton.setOnClickListener(v -> {
            if(MainActivity.USER_DATA.getAttendanceTry() >= maxAttendanceTry) {
               routing.navigate(EmployeeHome.class, true);
            }else{
                onBackPressed();
            }
        });
        cancelAttButton.setOnClickListener(v -> {
            cancelAttendance();
        });


    }

    private void setLabels(){
        empName.setText(markAttendanceResponse.getEmpName());
        phoneNumber.setText(getMaskedPhoneNumber(MainActivity.USER_DATA.getMobileNumber()));
    }
    private String getMaskedPhoneNumber(String phoneNumber){

        if(phoneNumber.length() < 10){
            return phoneNumber;
        }else {
            StringBuilder newNumber = new StringBuilder(phoneNumber);
            newNumber.setCharAt(phoneNumber.length()-4, 'x');
            newNumber.setCharAt(phoneNumber.length()-3, 'x');

            return newNumber.toString();
        }


    }
    private void cancelAttendance(){

        showLoading();
        AuthService authService = new AuthService();

        Log.d(TAG, "cancelAttendance: markAttendanceResponse.getTransactionId() : "+markAttendanceResponse.getTransactionId());
        authService.cancelAttendance(markAttendanceResponse.getTransactionId())
                .enqueue(new Callback<CancelAttendanceResponse>() {
                    @Override
                    public void onResponse(Call<CancelAttendanceResponse> call, Response<CancelAttendanceResponse> response) {
                        hideLoading();
                        if (!response.isSuccessful()) {
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            return;
                        }
                        CancelAttendanceResponse cancelAttendanceResponse = response.body();
                        if(cancelAttendanceResponse == null){
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            return;
                        }

                        if(TextUtils.equals(cancelAttendanceResponse.getStatus().toLowerCase(), getString(R.string.success_status))){

                          onBackPressed();
                        }else {
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        }

                    }

                    @Override
                    public void onFailure(Call<CancelAttendanceResponse> call, Throwable t) {
                        uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        hideLoading();
                    }
                });
    }
    private void setBitmap(String fileName, boolean isPositive){

        File folder = fileUtils.getFolder("offline_images");

        final File file = fileUtils.getFile(folder, fileName);


        if(file.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            if(myBitmap != null){
                if(isPositive){
                    fileUtils.setImage(posSmallImage, markAttendanceResponse.getThumbnailImgUrl());
                    posBigImage.setImageBitmap((myBitmap));
                }else {
//                    fileUtils.setImage(negPosBigImage, MainActivity.USER_DATA.getThumbnail());
                    negPosBigImage.setImageBitmap((myBitmap));

                    //negPosBigImage.setImageBitmap(myBitmap);
                }

            }

        }
    }

    private void registerSelf(){

        showLoading();
        AuthService authService = new AuthService();
        String siteCode = (String) routing.getParam("siteCode");


        authService.registerSelf(MainActivity.USER_DATA.getMobileNumber(), siteCode)
                .enqueue(new Callback<List<RegisterSelfResponse>>() {
                    @Override
                    public void onResponse(Call<List<RegisterSelfResponse>> call, Response<List<RegisterSelfResponse>> response) {

                        hideLoading();
                        if (!response.isSuccessful()) {
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            return;
                        }

                        List<RegisterSelfResponse> registerSelfResponses = response.body();
                        if(registerSelfResponses == null){
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            return;
                        }
                        if(registerSelfResponses.get(0).getStatus() != null && TextUtils.equals(registerSelfResponses.get(0).getStatus().toLowerCase(), getString(R.string.success_status))){
                            Log.d(TAG, "onResponse: status >> "+registerSelfResponses.get(0).getStatus());
                            routing.navigate(RegisterAgainResponseActivity.class, false);

                        }else {
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RegisterSelfResponse>> call, Throwable t) {
                        uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        hideLoading();
                    }
                });
    }

}