package com.enalytix.faceattendance.services;

import android.app.Activity;

import com.enalytix.faceattendance.models.FaceAttendanceResponse;
import com.enalytix.faceattendance.models.AuthRequest;
import com.enalytix.faceattendance.models.SendOTPResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FaceSenderService {

    private Activity activity;
    private FaceAttendanceAPI jsonPlaceHolderApi;
    private static final String BASE_URL = "http://3.129.150.29/";//"http://ec2-3-129-150-29.us-east-2.compute.amazonaws.com/";

    public FaceSenderService(Activity activity){

        this.activity = activity;
        Gson gson = new GsonBuilder()
                .setLenient()
                .serializeNulls()
                .create();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request newRequest = originalRequest.newBuilder()
                                .header("uniqueid", "SFALGSAI421KFSAFSGSSGWQRWWTW")
                                .addHeader("content-type", "multipart/form-data")
//                                .addHeader("content-type", "text/plain")

                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(loggingInterceptor)

                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        jsonPlaceHolderApi = retrofit.create(FaceAttendanceAPI.class);
    }


    public Call<SendOTPResponse> getUserAuthDate(String mobileNumber){


        return jsonPlaceHolderApi.getUserAuthData(new AuthRequest(mobileNumber));


    }
    public Call<FaceAttendanceResponse> registerUser(File fileObj, String userId, String siteId){

        RequestBody fbody = RequestBody.create(fileObj,MediaType.parse("image/*"));

        RequestBody id = RequestBody.create(userId, MediaType.parse("text/plain")
                );

        RequestBody siteid = RequestBody.create(siteId, MediaType.parse("text/plain"));

        return jsonPlaceHolderApi.registerUserByFace(fbody, id, siteid);
    }

    public Call<FaceAttendanceResponse> validateUser(File fileObj, String siteId){

//        siteId = "1"; //to be changed in production


        RequestBody fbody = RequestBody.create(fileObj,MediaType.parse("image/*"));

        RequestBody siteid = RequestBody.create(siteId, MediaType.parse("text/plain"));

        return jsonPlaceHolderApi.validateByFace(fbody, siteid);
    }

//    public Call<AttendanceResponse> registerUser(File file, String id, String siteId){
//        AttendanceRequest attendanceRequest =  new AttendanceRequest(file, id, siteId);
//        return jsonPlaceHolderApi.registerUserByFace(attendanceRequest);
//
//    }
}
