package com.enalytix.faceattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.models.UserData;
import com.enalytix.faceattendance.services.AuthService;
import com.enalytix.faceattendance.utils.Routing;
import com.enalytix.faceattendance.utils.UIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Button loginButton;
    private EditText mobileNumber;
    private Routing routing;
    private UIUtils uiUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.routing = new Routing(this);
        this.uiUtils = new UIUtils(this, R.id.parent);

        this.loginButton = findViewById(R.id.loginButton);
        this.mobileNumber = findViewById(R.id.mobileNumber);

        this.setListeners();
    }
    private void setListeners(){

        this.loginButton.setOnClickListener(v -> {
            this.loginButtonClick();
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

        AuthService authService = new AuthService();

        authService.getUserAuthDate(mobileNumber)
                .enqueue(new Callback<UserData>() {
                    @Override
                    public void onResponse(Call<UserData> call, Response<UserData> response) {

                        if (!response.isSuccessful()) {
                            uiUtils.showShortSnakeBar(getString(R.string.otp_send_failed_mobile));
                            return;
                        }
                        UserData userData = response.body();

                        if(TextUtils.equals(userData.getStatus().toLowerCase(), getString(R.string.success_status))){
                            goToOtp(mobileNumber);
                        }
                        assert userData != null;
                    }

                    @Override
                    public void onFailure(Call<UserData> call, Throwable t) {
                        uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                    }
                });

    }

    private void goToOtp(String mobileNumber){
        this.routing.appendParams("mobileNumber", mobileNumber);
        this.routing.navigate(OTPActivity.class, false);
    }
}