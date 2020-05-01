package com.djaphar.coffeepointsappcourier.ApiClasses;

import com.djaphar.coffeepointsappcourier.LocalDataClasses.Supervisor;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;

import java.util.ArrayList;
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

    @GET("api/couriers/me")
    Call<User> requestUser(@HeaderMap Map<String, String> headers);

    @GET("api/couriers/me")
    Call<UpdatableUser> requestUpdatableUser(@HeaderMap Map<String, String> headers);

    @GET("api/supervisors/{id}")
    Call<Supervisor> requestSupervisor(@Path("id") String id);
    
    @PUT("api/couriers/me")
    Call<User> requestUpdateCourier(@HeaderMap Map<String, String> headers, @Body UpdatableUser updatableUser);

    @GET("api/supervisors/{id}/products")
    Call<ArrayList<Product>> requestSupervisorProducts(@Path("id") String id, @HeaderMap Map<String, String> headers);

    @GET("api/products/{id}/togglelist")
    Call<Void> requestProductsListToggle(@Path("id") String id, @HeaderMap Map<String, String> headers);

    @GET("api/couriers/{id}/unsubscribe")
    Call<User> requestUnsubscribe(@Path("id") String id, @HeaderMap Map<String, String> headers);
}
