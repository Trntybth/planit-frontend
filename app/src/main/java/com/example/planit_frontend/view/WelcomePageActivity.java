package com.example.planit_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;

public class WelcomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomepage);

        // Fade in the logo
        fadeInLogo();

        // Navigate on screen tap
        findViewById(R.id.welcome_screen).setOnClickListener(v -> navigateToNextScreen());
    }

    // Logo fade-in animation
    private void fadeInLogo() {
        // Load the saved fadein animation from resources
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);

        // Apply it to the ImageView
        ImageView logo = findViewById(R.id.PlanItLogo);
        logo.startAnimation(fadeIn);  //
    }


    // Navigate to Member or Organisation homepage based on user status
    private void navigateToNextScreen() {
        String userType = getUserType(); // Replace with real user check (like shared prefs or API call)

        if ("Member".equals(userType)) {
            startActivity(new Intent(this, MemberHomePageActivity.class));
        } else if ("Organisation".equals(userType)) {
            startActivity(new Intent(this, OrganisationHomePageActivity.class));
        } else {
            startActivity(new Intent(this, SignupActivity.class));
        }
        finish(); // Close WelcomePageActivity
    }

    // Placeholder for real user type check
    private String getUserType() {
        // Example: Check shared preferences or local storage
        return null; // Change this when implementing real logic
    }
}
