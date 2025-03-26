package com.example.planit_frontend.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {



    @POST("members")
    Call<Void> createMember(@Body Member member);

    @POST("organisations")
    Call<Void> createOrganisation(@Body Organisation organisation);
    @POST("/api/v1/auth/google")
    Call<Void> sendGoogleToken(@Body String idToken);

    @POST("users")
    Call<Void> saveUser(@Body User user);  // Use @Body to send the user data as a POST request

    @POST("events")
    Call<Event> createEvent(@Body Event event);
}