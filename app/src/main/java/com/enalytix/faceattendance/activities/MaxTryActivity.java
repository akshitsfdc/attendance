package com.enalytix.faceattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.models.RegisterSelfResponse;
import com.enalytix.faceattendance.services.AuthService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaxTryActivity extends BaseActivity {

    private Button sendMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_try);
        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setUIUtils(R.id.parent);
        super.setObjects();
        super.setTitle();
        super.setThumbnail();
        super.setCurrentDate();

        sendMsg = findViewById(R.id.sendMsg);

        sendMsg.setOnClickListener(v -> {
            registerSelf();
        });

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