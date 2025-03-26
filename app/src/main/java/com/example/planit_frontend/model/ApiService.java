package com.example.planit_frontend.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    @GET("users/email/{email}")
    Call<Member> getUserByEmail(@Path("email") String email);


    // Method to get an Organisation by email
    @GET("organisations/email")  // Replace with your actual endpoint
    Call<Organisation> getOrganisationByEmail(@Query("email") String email);

    @GET("check-email-exists")
    Call<Boolean> checkEmailExists(@Query("email") String email);

    @POST("members")
    Call<Void> createMember(@Body Member member);

    @POST("organisations")
    Call<Void> createOrganisation(@Body Organisation organisation);

    @POST("events")
    Call<Event> createEvent(@Body Event event);


}