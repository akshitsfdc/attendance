package com.enalytix.faceattendance.services;

import com.enalytix.faceattendance.models.AuthRequest;
import com.enalytix.faceattendance.models.UserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

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

    public Call<UserData> getUserAuthDate(String mobileNumber){

        return jsonPlaceHolderApi.getUserAuthData(new AuthRequest(mobileNumber));


    }
}
