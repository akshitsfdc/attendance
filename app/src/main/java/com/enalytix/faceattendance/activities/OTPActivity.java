package com.enalytix.faceattendance.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.helper.GenericTextWatcher;
import com.enalytix.faceattendance.models.CheckUserResponse;
import com.enalytix.faceattendance.models.SendOTPResponse;
import com.enalytix.faceattendance.services.AuthService;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OTPActivity extends BaseActivity {

    private static final long START_TIME_IN_MILLIS = 60000;

    private static final String TAG = "OTPActivity";

    private EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five;
    private Button verifyButton;
    private TextView desText;
    private TextView resend;
    private String otp;
    private CircularProgressIndicator loadingIndicator;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    private TextView mTextViewCountDown;
    private String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        super.context = this;
//        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();
        super.setUIUtils(R.id.parent);


        otp_textbox_one = findViewById(R.id.otp_edit_box1);
        otp_textbox_two = findViewById(R.id.otp_edit_box2);
        otp_textbox_three = findViewById(R.id.otp_edit_box3);
        otp_textbox_four = findViewById(R.id.otp_edit_box4);
        otp_textbox_five = findViewById(R.id.otp_edit_box5);
        verifyButton = findViewById(R.id.verifyButton);
        desText = findViewById(R.id.desText);
        resend = findViewById(R.id.resend);

        mTextViewCountDown = findViewById(R.id.counterText);

        this.loadingIndicator = findViewById(R.id.loadingIndicator);



        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five};

        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit));

        this.otp = (String) this.routing.getParam("otp");
        this.mobileNumber = (String) this.routing.getParam("mobileNumber");

        verifyButton.setOnClickListener(v ->{
            verifyUser();
        });
        resend.setOnClickListener(v -> {
            resendOTP(this.mobileNumber);
        });

        this.changeLabels();
        startTimer();
    }

    private void changeLabels(){

        try {
            String text = desText.getText().toString();

            String finalLabel = text+" "+this.mobileNumber;
            desText.setText(finalLabel);

        }catch (Exception e){

        }

    }

    private boolean validateForm(){

        boolean valid = true;

        String t1 = otp_textbox_one.getText().toString().trim();
        String t2 = otp_textbox_two.getText().toString().trim();
        String t3 = otp_textbox_three.getText().toString().trim();
        String t4 = otp_textbox_four.getText().toString().trim();
        String t5 = otp_textbox_five.getText().toString().trim();

        if(TextUtils.isEmpty(t1) || TextUtils.isEmpty(t2) || TextUtils.isEmpty(t3) || TextUtils.isEmpty(t4)
        ||  TextUtils.isEmpty(t5)){
            this.uiUtils.showLongSnakeBar(getString(R.string.otp_enter_otp));
            valid = false;
        }

        return valid;
    }

    private void verifyUser(){

        if(!this.validateForm()){
            return;
        }

        if(TextUtils.equals(getFullOtpEntered(), this.otp)){
//           this.routing.navigate(EmployeeHome.class, true);
            checkUser();
        }else {
            this.uiUtils.showShortSnakeBar(getString(R.string.otp_invalid));
        }

    }

    private void checkUser(){
        showLoadingIndicator();
        AuthService authService = new AuthService();

        authService.checkUser(mobileNumber)
                .enqueue(new Callback<CheckUserResponse>() {
                    @Override
                    public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {

                        hideLoadingIndicator();
                        if (!response.isSuccessful()) {
                            uiUtils.showShortSnakeBar(getString(R.string.otp_send_failed_mobile));
                            return;
                        }

                        CheckUserResponse checkUserResponse = response.body();
                        if(checkUserResponse == null){
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            return;
                        }
                        if(TextUtils.equals(checkUserResponse.getStatus().toLowerCase(), getString(R.string.active_status))){
                            setFinalUserData(checkUserResponse);
                            if( !fileUtils.saveUserDataInSharedPre(MainActivity.USER_DATA)){
                                Log.d(TAG, "onResponse: saveUserDataInSharedPre : failed "+MainActivity.USER_DATA.toString());
                            }

                            routing.navigateAndClear(EmployeeHome.class);

                        }else{
                            routing.navigateAndClear(LoginActivity.class);
                            uiUtils.showShortSnakeBar(getString(R.string.otp_inactive_user));
                        }

                    }

                    @Override
                    public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                        uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        hideLoadingIndicator();
                    }
                });

    }
    private String getFullOtpEntered(){

        String t1 = otp_textbox_one.getText().toString().trim();
        String t2 = otp_textbox_two.getText().toString().trim();
        String t3 = otp_textbox_three.getText().toString().trim();
        String t4 = otp_textbox_four.getText().toString().trim();
        String t5 = otp_textbox_five.getText().toString().trim();

        return t1+t2+t3+t4+t5;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            View view = getCurrentFocus();
            if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
                int scrcoords[] = new int[2];
                view.getLocationOnScreen(scrcoords);
                float x = ev.getRawX() + view.getLeft() - scrcoords[0];
                float y = ev.getRawY() + view.getTop() - scrcoords[1];
                if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                    ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
            }
            return super.dispatchTouchEvent(ev);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    private void resendOTP(String mobileNumber){

        showLoadingIndicator();
        AuthService authService = new AuthService();

        authService.getUserAuthDate(mobileNumber)
                .enqueue(new Callback<SendOTPResponse>() {
                    @Override
                    public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {

                        hideLoadingIndicator();
                        if (!response.isSuccessful()) {
                            uiUtils.showShortSnakeBar(getString(R.string.otp_send_failed_mobile));
                            return;
                        }

                        SendOTPResponse sendOTPResponse = response.body();
                        if(sendOTPResponse == null){
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            return;
                        }
                        if(TextUtils.equals(sendOTPResponse.getStatus().toLowerCase(), getString(R.string.success_status))){
//                            Log.d(TAG, "onResponse: otp : "+userData.getOtp());
                            otp = sendOTPResponse.getOtp();
                            setUserData(sendOTPResponse, mobileNumber);

                            startTimer();

                        }else{
                            uiUtils.showShortSnakeBar(getString(R.string.otp_send_failed_mobile));
                            resend.setEnabled(true);
                        }

                    }

                    @Override
                    public void onFailure(Call<SendOTPResponse> call, Throwable t) {
                        uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        resend.setEnabled(true);
                        hideLoadingIndicator();
                    }
                });


    }

    private void setUserData(SendOTPResponse sendOTPResponse, String mobileNumber){

        MainActivity.USER_DATA.setMobileNumber(mobileNumber);
        MainActivity.USER_DATA.setCheckoutCompleted(true);
        MainActivity.USER_DATA.setCheckInCompleted(false);
        MainActivity.USER_DATA.setEmpCode(sendOTPResponse.getEmpCode());
        MainActivity.USER_DATA.setEmployeeId(sendOTPResponse.getEmployeeId());
        MainActivity.USER_DATA.setThumbnail(sendOTPResponse.getThumbnail());
        MainActivity.USER_DATA.setStaffRole(sendOTPResponse.getStaffRole());
        MainActivity.USER_DATA.setSites(sendOTPResponse.getSites());
    }

    private void setFinalUserData(CheckUserResponse checkUserResponse){
        MainActivity.USER_DATA.setStatus(checkUserResponse.getStatus());
        MainActivity.USER_DATA.setSites(checkUserResponse.getSites());
        MainActivity.USER_DATA.setStaffRole(checkUserResponse.getStaffRole());
    }

    private void showLoadingIndicator(){
        this.loadingIndicator.setVisibility(View.VISIBLE);
        this.verifyButton.setEnabled(false);
        this.resend.setEnabled(false);
    }
    private void hideLoadingIndicator(){
        this.loadingIndicator.setVisibility(View.GONE);
        this.verifyButton.setEnabled(true);

    }

    private void startTimer() {
        mTextViewCountDown.setVisibility(View.VISIBLE);
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {

                resend.setEnabled(true);
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                mCountDownTimer.cancel();
                mTextViewCountDown.setVisibility(View.GONE);

            }
        }.start();

    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }



}