package com.enalytix.faceattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

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
import com.enalytix.faceattendance.models.UserData;
import com.enalytix.faceattendance.services.AuthService;
import com.enalytix.faceattendance.utils.Routing;
import com.enalytix.faceattendance.utils.UIUtils;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OTPActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 60000;

    private static final String TAG = "OTPActivity";

    private EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five;
    private Button verifyButton;
    private UIUtils uiUtils;
    private Routing routing;
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

        uiUtils = new UIUtils(this, R.id.parent);
        routing = new Routing(this);

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
           this.routing.navigate(EmployeeHome.class, true);
        }else {
            this.uiUtils.showShortSnakeBar(getString(R.string.otp_invalid));
        }

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

        showLoading();
        AuthService authService = new AuthService();

        authService.getUserAuthDate(mobileNumber)
                .enqueue(new Callback<UserData>() {
                    @Override
                    public void onResponse(Call<UserData> call, Response<UserData> response) {

                        hideLoading();
                        if (!response.isSuccessful()) {
                            uiUtils.showShortSnakeBar(getString(R.string.otp_send_failed_mobile));
                            return;
                        }
                        UserData userData = response.body();

                        if(TextUtils.equals(userData.getStatus().toLowerCase(), getString(R.string.success_status))){
                            Log.d(TAG, "onResponse: otp : "+userData.getOtp());
//                          goToOtp(mobileNumber, userData.getOtp());
                            otp = userData.getOtp();
                            startTimer();
                        }else{
                            uiUtils.showShortSnakeBar(getString(R.string.otp_send_failed_mobile));
                            resend.setEnabled(true);
                        }

                    }

                    @Override
                    public void onFailure(Call<UserData> call, Throwable t) {
                        uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        resend.setEnabled(true);
                        hideLoading();
                    }
                });

    }

    private void showLoading(){
        this.loadingIndicator.setVisibility(View.VISIBLE);
        this.verifyButton.setEnabled(false);
        this.resend.setEnabled(false);
    }
    private void hideLoading(){
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