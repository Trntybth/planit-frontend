
package com.example.planit_frontend.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // members
    @POST("members")
    Call<Void> createMember(@Body Member member);

    // organisations

    @POST("organisations")
    Call<Void> createOrganisation(@Body Organisation organisation);

    /* put the event in the eventsCreated list */
    @PUT("organisations/email/{email}/events")
    Call<Organisation> addEventToOrganisation(@Path("email") String email, @Body Event event);

    // events

    /* get all events */

    @GET("events") // Replace with your actual endpoint URL
    Call<List<Event>> getAllEvents();

    @POST("events/events")
    Call<Event> createEvent(@Body Event event);

    @PUT("/events/{eventId}")
    Call<ApiResponse<Event>> updateEvent(@Path("eventId") String eventId, @Body Event updatedEvent);

    @GET("events/events/{email}")
    Call<List<Event>> getEventsByEmail(@Path("email") String email);

    @DELETE("events/{eventId}")
    Call<Void> deleteEvent(@Path("eventId") String eventId);

    // users

    @GET("users/check-member")
    Call<Boolean> checkMemberExists(@Query("email") String email, @Query("username") String username);

    @GET("users/check-organisation")
    Call<Boolean> checkOrganisationExists(@Query("email") String email, @Query("username") String username);


    @GET("users/email/{email}")
    Call<ApiResponse<Member>> getMemberByEmail(@Path("email") String email);

    @GET("users/email/{email}")
    Call<ApiResponse<Organisation>> getOrganisationByEmail(@Path("email") String email);


    // get the usertype
    @GET("users/email/{email}/usertype")
    Call<String> getUserTypeByEmail(@Path("email") String email);

    // sign up

    @POST("events/signup/signup")
    Call<ResponseBody> signUpForEvent(
            @Query("memberEmail") String memberEmail,
            @Query("eventId") String eventId,
            @Body SignUpRequest signUpRequest
    );


    @GET("events/signup/signed-up-events")
    Call<List<Event>> getSignedUpEvents(@Query("memberEmail") String memberEmail);

    // add remove one
    @POST("events/signup/un-sign-up")
    Call<ResponseBody> removeEventSignup(
            @Query("memberEmail") String memberEmail,
            @Query("eventId") String eventId
    );


}

