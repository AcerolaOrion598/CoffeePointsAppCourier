package com.djaphar.coffeepointsappcourier.ApiClasses;

import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;
import com.google.gson.Gson;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PointsApi {

    @POST("api/couriers/auth")
    Call<SecondCredentials> getCode(@Body FirstCredentials credentials);

    @POST("api/couriers/codecheck")
    Call<User> login(@Body SecondCredentials credentials);

    @GET("api/couriers/{id}")
    Call<User> requestUser(@Path("id") String id, @HeaderMap Map<String, String> headers);

    @PUT("api/couriers/{id}/self")
    Call<User> requestUnsetOwner(@Path("id") String id, @HeaderMap Map<String, String> headers, @Body UpdatedUser updatedUser);
}
