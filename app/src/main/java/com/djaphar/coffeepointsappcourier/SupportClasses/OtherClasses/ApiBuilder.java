package com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses;

import com.djaphar.coffeepointsappcourier.ApiClasses.PointsApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiBuilder {

    private static final String baseUrl = "http://212.109.219.69:3007/";

    public static PointsApi getPointsApi() {
        return new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build().create(PointsApi.class);
    }
}
