package com.enalytix.faceattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.models.CheckUserResponse;
import com.enalytix.faceattendance.models.Site;
import com.enalytix.faceattendance.services.AuthService;
import com.enalytix.faceattendance.services.GeoService;
import com.enalytix.faceattendance.services.PermissionsServices;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupervisorHomeActivity extends BaseActivity {

    private PermissionsServices permissionsServices;
    private GeoService geoService;
    private int maxAttendanceTry = 3;
    private RelativeLayout loginWrapper;
    private RelativeLayout logoutWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_home);

        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();
//        super.setUIUtils(R.id.parent);
//
//        super.setThumbnail();
//        super.setTitle();
//        super.setCurrentDate();

        permissionsServices = new PermissionsServices(this);
        geoService = new GeoService(this);

        loginWrapper = findViewById(R.id.loginWrapper);
        logoutWrapper = findViewById(R.id.logoutWrapper);

        permissionsServices.askForReadPermission();

        loginWrapper.setOnClickListener(v -> {
            if(!MainActivity.USER_DATA.isCheckInCompleted()){
                goToLogin();
            }else {
                uiUtils.showShortSnakeBar("You need to check out first");
            }

        });
        logoutWrapper.setOnClickListener(v -> {
            if(!MainActivity.USER_DATA.isCheckoutCompleted()){
                goToCheckout();
            }else {
                uiUtils.showShortSnakeBar("You need to check in first");
            }

        });


//        checkUser(MainActivity.USER_DATA.getMobileNumber());
    }

    private void goToLogin(){

        boolean isException = false;
        String siteLatLong = "";

        if(MainActivity.USER_DATA.getAttendanceTry() >= maxAttendanceTry){
            routing.navigate(MaxTryActivity.class, false);
            return;
        }

        for (Site site : MainActivity.USER_DATA.getSites()) {

            try {
                siteLatLong +=" / "+site.getSiteLatitude()+" , "+site.getSiteLongitude() +"["+site.getGeocodingArea()+"]";

                if(site.getSiteLatitude().length() == 0 || site.getSiteLongitude().length() == 0){
                    continue;
                }
                if(geoService.isWithinRange(site.getGeocodingArea(), Double.parseDouble(site.getSiteLatitude()), Double.parseDouble(site.getSiteLongitude()))){
                    routing.appendParams("attType","CheckIn");
                    routing.appendParams("siteCode",site.getSiteCode());
                    routing.appendParams("siteName", site.getSiteName());
                    routing.navigate(RecordAttendanceActivity.class, false);
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
                isException = true;
            }

        }
        routing.appendParams("target",siteLatLong);
        routing.appendParams("isException", isException);
        routing.navigate(NotAtLocationActivity.class, false);


    }


    private void goToCheckout(){

        boolean isException = false;
        String siteLatLong = "";

        if(MainActivity.USER_DATA.getAttendanceTry() >= maxAttendanceTry){
            routing.navigate(MaxTryActivity.class, false);
            return;
        }

        for (Site site : MainActivity.USER_DATA.getSites()) {



            try {

                siteLatLong +=" / "+site.getSiteLatitude()+" , "+site.getSiteLongitude() +"["+site.getGeocodingArea()+"]";

                if(site.getSiteLatitude().length() == 0 || site.getSiteLongitude().length() == 0){
                    continue;
                }

                Log.d(TAG, "goToCheckout: >> C1159 : "+site.getGeocodingArea());
                if(geoService.isWithinRange(site.getGeocodingArea(), Double.parseDouble(site.getSiteLatitude()), Double.parseDouble(site.getSiteLongitude()))){
                    routing.appendParams("attType","CheckOut");
                    routing.appendParams("siteCode",site.getSiteCode());
                    routing.appendParams("siteName", site.getSiteName());
                    routing.navigate(RecordAttendanceActivity.class, false);
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
                isException = true;
            }

        }

        routing.appendParams("target",siteLatLong);
        routing.appendParams("isException", isException);
        routing.navigate(NotAtLocationActivity.class, false);
    }

    private void checkUser(String mobileNumber){
        showLoading();
        AuthService authService = new AuthService();

        authService.checkUser(mobileNumber)
                .enqueue(new Callback<CheckUserResponse>() {
                    @Override
                    public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {

                        hideLoading();
                        if (!response.isSuccessful()) {
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
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

                        }else{
                            routing.navigateAndClear(LoginActivity.class);
                            uiUtils.showShortSnakeBar(getString(R.string.otp_inactive_user));
                        }

                    }

                    @Override
                    public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                        uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        hideLoading();
                    }
                });

    }

    private void setFinalUserData(CheckUserResponse checkUserResponse){
        MainActivity.USER_DATA.setStatus(checkUserResponse.getStatus());
        MainActivity.USER_DATA.setSites(checkUserResponse.getSites());
        MainActivity.USER_DATA.setStaffRole(checkUserResponse.getStaffRole());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: requestCode >>"+requestCode);

        switch (requestCode) {
            case PermissionsServices.READ_PERMISSION_CODE: {
                Log.d("Home Activity", "onRequestPermissionsResult: grantResults[0] >>"+grantResults[0]);
                if (PermissionsServices.isAlreadyGranted(grantResults[0])) {

                    permissionsServices.askForWritePermission();

                } else {
                    uiUtils.showShortSnakeBar("You must provide read Permission");
                }

                break;
            }

            case PermissionsServices.WRITE_PERMISSION_CODE: {

                if (grantResults.length > 0 && PermissionsServices.isAlreadyGranted(grantResults[0])) {

                    permissionsServices.askForCameraPermission();


                } else {
                    uiUtils.showShortSnakeBar("You must provide write Permission");
                }

                break;
            }

            case PermissionsServices.CAMERA_PERMISSION_CODE: {

                if (grantResults.length > 0 && PermissionsServices.isAlreadyGranted(grantResults[0])) {

                    permissionsServices.askForAccessFineLocationPermission();

                } else {
                    uiUtils.showShortSnakeBar("You must provide Camera Permission");
                }


                break;
            }
            case PermissionsServices.ACCESS_FINE_PERMISSION_CODE: {

                if (grantResults.length > 0 && PermissionsServices.isAlreadyGranted(grantResults[0])) {

                    permissionsServices.askForAccessCoarseLocationPermission();

                } else {
                    uiUtils.showShortSnakeBar("You must provide Location Permission");
                }


                break;
            }
            case PermissionsServices.ACCESS_COARSE_PERMISSION_CODE: {

                if (grantResults.length > 0 && PermissionsServices.isAlreadyGranted(grantResults[0])) {


                } else {
                    uiUtils.showShortSnakeBar("You must provide Location Permission");
                }


                break;
            }

            case -1:
                break;
        }
    }
}