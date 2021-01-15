package com.enalytix.faceattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.models.UserData;
import com.enalytix.faceattendance.services.AuthService;
import com.enalytix.faceattendance.utils.Routing;
import com.enalytix.faceattendance.utils.UIUtils;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Button loginButton;
    private EditText mobileNumber;
    private Routing routing;
    private UIUtils uiUtils;
    private CircularProgressIndicator loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.routing = new Routing(this);
        this.uiUtils = new UIUtils(this, R.id.parent);

        this.loginButton = findViewById(R.id.loginButton);
        this.mobileNumber = findViewById(R.id.mobileNumber);
        this.loadingIndicator = findViewById(R.id.loadingIndicator);



        this.setListeners();
    }
    private void setListeners(){

        this.loginButton.setOnClickListener(v -> {
            this.loginButtonClick();
        });
        this.mobileNumber.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction()!=KeyEvent.ACTION_DOWN)
                return false;
            if(keyCode == KeyEvent.KEYCODE_ENTER ){
               hideKeyboard();
                return true;
            }
            return false;
        });
    }

    private void loginButtonClick(){


        if(!this.validateForm()){
            return;
        }

        String mobileNumber = this.mobileNumber.getText().toString().trim();

        this.sendOTP(mobileNumber);

    }
    private boolean validateForm(){

        boolean valid = true;

        String mobileNumber = this.mobileNumber.getText().toString().trim();

        if(TextUtils.isEmpty(mobileNumber) || !TextUtils.isDigitsOnly(mobileNumber) || mobileNumber.length() < 10){
            valid = false;
            this.mobileNumber.setError(getString(R.string.invalid_mobile));
            this.uiUtils.showShortSnakeBar(getString(R.string.invalid_mobile_des));
        }

        return valid;
    }
    private void sendOTP(String mobileNumber){

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
                        if(userData == null){
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            return;
                        }
                        if(TextUtils.equals(userData.getStatus().toLowerCase(), getString(R.string.success_status))){
                            Log.d(TAG, "onResponse: otp : "+userData.getOtp());
                            goToOtp(mobileNumber, userData.getOtp());
                            MainActivity.USER_DATA = userData;
                        }else{
                            uiUtils.showShortSnakeBar(getString(R.string.otp_send_failed_mobile));
                        }

                    }

                    @Override
                    public void onFailure(Call<UserData> call, Throwable t) {
                        uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        hideLoading();
                    }
                });

    }

    private void goToOtp(String mobileNumber, String otp){

        this.routing.appendParams("mobileNumber", mobileNumber);
        this.routing.appendParams("otp", otp);
        this.routing.navigate(OTPActivity.class, false);

    }

    private void showLoading(){
        this.loadingIndicator.setVisibility(View.VISIBLE);
        this.loginButton.setEnabled(false);
    }
    private void hideLoading(){
        this.loadingIndicator.setVisibility(View.GONE);
        this.loginButton.setEnabled(true);
    }
    private void hideKeyboard(){
        try {
            ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
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
}