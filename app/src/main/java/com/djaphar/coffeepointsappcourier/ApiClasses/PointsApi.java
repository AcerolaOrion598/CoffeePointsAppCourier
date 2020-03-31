package com.djaphar.coffeepointsappcourier.ApiClasses;

import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PointsApi {

    @POST("api/couriers/auth")
    Call<SecondCredentials> getCode(@Body FirstCredentials credentials);

    @POST("api/couriers/codecheck")
    Call<User> login(@Body SecondCredentials credentials);
}
