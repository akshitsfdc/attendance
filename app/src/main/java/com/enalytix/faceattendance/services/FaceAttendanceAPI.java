package com.enalytix.faceattendance.services;


import com.enalytix.faceattendance.models.AttendanceRequest;
import com.enalytix.faceattendance.models.AttendanceResponse;
import com.enalytix.faceattendance.models.AuthRequest;
import com.enalytix.faceattendance.models.UserData;

import java.io.File;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface FaceAttendanceAPI {

    @POST("send-otp")
    Call<UserData> getUserAuthData(@Body AuthRequest authRequest);

//    @POST("uploadatt")
//    Call<AttendanceResponse> registerUserByFace(@Body AttendanceRequest authRequest);

    @Multipart
    @POST("uploadatt")
    Call<AttendanceResponse> registerUserByFace(
            @Part("file\"; filename=\"pp.png\" ") RequestBody file,
            @Part("id") RequestBody id,
            @Part("siteid") RequestBody siteid
            );

    //    @POST("uploadatt")
//    Call<AttendanceResponse> registerUserByFace(@Body AttendanceRequest authRequest);
//    @Multipart
//    @POST("uploadatt")
//    Call<AttendanceResponse> registerUserByFace(
//            @PartMap Map<String, RequestBody> params
//    );
//    @GET("posts")
//    Call<List<Post>> getPosts(
//            @Query("userId") Integer[] userId,
//            @Query("_sort") String sort,
//            @Query("_order") String order
//    );
//    @GET("posts")
//    Call<List<Post>> getPosts(@QueryMap Map<String, String> parameters);
//    @GET("posts/{id}/comments")
//    Call<List<Comment>> getComments(@Path("id") int postId);
//    @GET
//    Call<List<Comment>> getComments(@Url String url);
//    @POST("posts")
//    Call<Post> createPost(@Body Post post);
//    @FormUrlEncoded
//    @POST("posts")
//    Call<Post> createPost(
//            @Field("userId") int userId,
//            @Field("title") String title,
//            @Field("body") String text
//    );
//    @FormUrlEncoded
//    @POST("posts")
//    Call<Post> createPost(@FieldMap Map<String, String> fields);
//    @Headers({"Static-Header1: 123", "Static-Header2: 456"})
//    @PUT("posts/{id}")
//    Call<Post> putPost(@Header("Dynamic-Header") String header,
//                       @Path("id") int id,
//                       @Body Post post);
//    @PATCH("posts/{id}")
//    Call<Post> patchPost(@HeaderMap Map<String, String> headers,
//                         @Path("id") int id,
//                         @Body Post post);
//    @DELETE("posts/{id}")
//    Call<Void> deletePost(@Path("id") int id);


}
