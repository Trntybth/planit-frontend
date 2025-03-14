package com.example.planit_frontend.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserApiService {

    @POST("/api/v1/auth/google")
    Call<Void> sendGoogleToken(@Body String idToken);

    @GET("Users")
    Call<User> getUserType();
}