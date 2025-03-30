package com.example.planit_frontend.model;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    @GET("users/email/{email}")
    Call<Member> getUserByEmail(@Path("email") String email);

    @GET("check-email-exists")
    Call<Boolean> checkEmailExists(@Query("email") String email);

    @POST("members")
    Call<Void> createMember(@Body Member member);

    @POST("organisations")
    Call<Void> createOrganisation(@Body Organisation organisation);

    @POST("events")
    Call<Event> createEvent(@Body Event event);

    @GET("users/check-member")
    Call<Boolean> checkMemberExists(@Query("email") String email, @Query("username") String username);

    @GET("users/check-organisation")
    Call<Boolean> checkOrganisationExists(@Query("email") String email, @Query("username") String username);


    // Check if the user is a Member by email
    @GET("users/email/{email}")
    Call<Member> getMemberByEmail(@Path("email") String email);

    // Check if the user is an Organisation by email
    @GET("users/email/{email}")
    Call<Organisation> getOrganisationByEmail(@Path("email") String email);



}