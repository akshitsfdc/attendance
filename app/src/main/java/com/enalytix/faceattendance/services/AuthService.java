package com.enalytix.faceattendance.services;

import com.enalytix.faceattendance.activities.MainActivity;
import com.enalytix.faceattendance.models.AuthRequest;
import com.enalytix.faceattendance.models.CancelAttendanceRequest;
import com.enalytix.faceattendance.models.CancelAttendanceResponse;
import com.enalytix.faceattendance.models.CheckUserResponse;
import com.enalytix.faceattendance.models.MarkAttendanceRequest;
import com.enalytix.faceattendance.models.MarkAttendanceResponse;
import com.enalytix.faceattendance.models.MyAttendanceRequest;
import com.enalytix.faceattendance.models.MyAttendanceResponse;
import com.enalytix.faceattendance.models.RegisterSelfRequest;
import com.enalytix.faceattendance.models.RegisterSelfResponse;
import com.enalytix.faceattendance.models.RegisterUserRequest;
import com.enalytix.faceattendance.models.RegisterUserResponse;
import com.enalytix.faceattendance.models.SendOTPResponse;
import com.enalytix.faceattendance.models.UserData;
import com.enalytix.faceattendance.utils.UIUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthService {

    private FaceAttendanceAPI jsonPlaceHolderApi;
    private static final String BASE_URL = "https://enalytix.com/api/";
    public AuthService(){

        Gson gson = new GsonBuilder().serializeNulls().create();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", "38i9wqi9wmkdidjad0ds008w")
                                .addHeader("content-type", "application/json")
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        jsonPlaceHolderApi = retrofit.create(FaceAttendanceAPI.class);
    }

    public Call<SendOTPResponse> getUserAuthDate(String mobileNumber){

        return jsonPlaceHolderApi.getUserAuthData(new AuthRequest(mobileNumber));


    }
    public Call<List<MarkAttendanceResponse>> markAttendance(String attendanceType, String siteCode, int submittedBy){

        UIUtils uiUtils = new UIUtils();

        MarkAttendanceRequest markAttendanceRequest = new MarkAttendanceRequest();
        markAttendanceRequest.setMobileNumber(MainActivity.USER_DATA.getMobileNumber());
        markAttendanceRequest.setSideCode(siteCode);
        markAttendanceRequest.setAttendanceType(attendanceType);
        markAttendanceRequest.setAttendanceDateTime(uiUtils.getCurrentTimeString());
        markAttendanceRequest.setSubmittedBy(submittedBy);

        return jsonPlaceHolderApi.markAttendance(markAttendanceRequest);

    }

    public Call<CheckUserResponse> checkUser(String mobileNumber){

        return jsonPlaceHolderApi.checkUserData(new AuthRequest(mobileNumber));
    }

    public Call<CancelAttendanceResponse> cancelAttendance(String transactionId){

        return jsonPlaceHolderApi.cancelAttendance(new CancelAttendanceRequest(transactionId));
    }

    public Call<List<RegisterSelfResponse>> registerSelf(String mobileNumber, String siteCode){

        return jsonPlaceHolderApi.registerSelf(new RegisterSelfRequest(mobileNumber, siteCode));
    }

    public Call<MyAttendanceResponse> getMyAttendance(String mobileNumber, String monthYear){

        return jsonPlaceHolderApi.getAttendance(new MyAttendanceRequest(mobileNumber, monthYear));
    }

    public Call<List<RegisterUserResponse>> registerEmployee(RegisterUserRequest registerUserRequest){

        return jsonPlaceHolderApi.registerUser(registerUserRequest);
    }
}
