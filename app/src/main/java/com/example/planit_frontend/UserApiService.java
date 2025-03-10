package com.example.planit_frontend;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApiService {

    @POST("/api/v1/auth/google")
    Call<Void> sendGoogleToken(@Body String idToken);
}
