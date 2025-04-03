package com.example.planit_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.ApiResponse;
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.Member;
import com.example.planit_frontend.model.Organisation;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomePageActivity extends AppCompatActivity {

    private String userType; // Store user type here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomepage);

        // Fade in the logo
        fadeInLogo();

        // Navigate on screen tap
        findViewById(R.id.welcome_screen).setOnClickListener(v -> getUserType());
    }

    // Logo fade-in animation
    private void fadeInLogo() {
        // Load the saved fadein animation from resources
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);

        // Apply it to the ImageView
        ImageView logo = findViewById(R.id.PlanItLogo);
        logo.startAnimation(fadeIn);
    }

    // Navigate to Member or Organisation homepage based on user status
    private void navigateToNextScreen() {
        if ("Member".equals(userType)) {
            startActivity(new Intent(this, MemberHomePageActivity.class));
            Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();  // Add Toast for Member login
        } else if ("Organisation".equals(userType)) {
            startActivity(new Intent(this, OrganisationHomePageActivity.class));
            Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();  // Add Toast for Organisation login
        } else {
            startActivity(new Intent(this, SignupActivity.class));
        }
        finish(); // Close WelcomePageActivity
    }

    private void getUserType() {
        SessionManager sessionManager = new SessionManager(this);
        String email = sessionManager.getActiveEmail();

        if (email == null) {
            // No email found in session, navigate directly to Signup page
            Log.d("UserTypeCheck", "No email found in session, redirecting to Signup.");
            startActivity(new Intent(this, SignupActivity.class));
            finish(); // Optionally close the current activity so the user can't go back
        } else {
            // Make an API call to check if the email belongs to an Organisation
            checkOrganisationByEmail(email, sessionManager);
        }
    }

    private void checkOrganisationByEmail(String email, SessionManager sessionManager) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        // Call the backend to check if the email is associated with an Organisation
        Call<ApiResponse<Organisation>> organisationCall = apiService.getOrganisationByEmail(email);
        organisationCall.enqueue(new Callback<ApiResponse<Organisation>>() {
            @Override
            public void onResponse(Call<ApiResponse<Organisation>> call, Response<ApiResponse<Organisation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Organisation organisation = response.body().getData();
                    if (organisation != null) {
                        // Email is associated with an Organisation
                        sessionManager.saveActiveEmail(email);  // Ensure email is saved
                        userType = "Organisation"; // Set userType to Organisation
                        Log.d("UserTypeCheck", "User is an Organisation");
                        navigateToNextScreen(); // Navigate to Organisation's home page
                    }
                } else {
                    // If the email is not an organisation, check for a member
                    checkMemberByEmail(email, sessionManager);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Organisation>> call, Throwable t) {
                Log.e("UserTypeCheck", "Error checking Organisation", t);
            }
        });
    }

    private void checkMemberByEmail(String email, SessionManager sessionManager) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        // Call the backend to check if the email is associated with a Member
        Call<ApiResponse<Member>> memberCall = apiService.getMemberByEmail(email);
        memberCall.enqueue(new Callback<ApiResponse<Member>>() {
            @Override
            public void onResponse(Call<ApiResponse<Member>> call, Response<ApiResponse<Member>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Member member = response.body().getData();
                    if (member != null) {
                        // Email is associated with a Member
                        sessionManager.saveActiveEmail(email);  // Ensure email is saved
                        userType = "Member"; // Set userType to Member
                        Log.d("UserTypeCheck", "User is a Member");
                        navigateToNextScreen(); // Navigate to Member's home page
                    }
                } else {
                    Log.d("UserTypeCheck", "No Member or Organisation found for this email");
                    // Handle case when no Member or Organisation is found
                    userType = "None";
                    navigateToNextScreen(); // Navigate to Signup if no user found
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Member>> call, Throwable t) {
                Log.e("UserTypeCheck", "Error checking Member", t);
            }
        });
    }
}
