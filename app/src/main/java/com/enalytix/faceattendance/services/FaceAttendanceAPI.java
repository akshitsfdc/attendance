package com.enalytix.faceattendance.services;


import com.enalytix.faceattendance.models.FaceAttendanceResponse;
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
import com.enalytix.faceattendance.models.SendOTPResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FaceAttendanceAPI {
//    check-user
    @POST("send-otp")
    Call<SendOTPResponse> getUserAuthData(@Body AuthRequest authRequest);

    @POST("check-user")
    Call<CheckUserResponse> checkUserData(@Body AuthRequest authRequest);

    @POST("save-attendance")
    Call<List<MarkAttendanceResponse>> markAttendance(@Body MarkAttendanceRequest markAttendanceRequest);

    @POST("delete-attendance")
    Call<CancelAttendanceResponse> cancelAttendance(@Body CancelAttendanceRequest cancelAttendanceRequest);

    @POST("self-register")
    Call<List<RegisterSelfResponse>> registerSelf(@Body RegisterSelfRequest cancelAttendanceRequest);

    @POST("get-attendance")
    Call<MyAttendanceResponse> getAttendance(@Body MyAttendanceRequest myAttendanceRequest);
    @Multipart
    @POST("uploadatt")
    Call<FaceAttendanceResponse> registerUserByFace(
            @Part("file\"; filename=\"pp.png\" ") RequestBody file,
            @Part("id") RequestBody id,
            @Part("siteid") RequestBody siteid
            );

    @Multipart
    @POST("getatt")
    Call<FaceAttendanceResponse> validateByFace(
            @Part("file\"; filename=\"pp.png\" ") RequestBody file,
            @Part("siteid") RequestBody siteid
    );


}
