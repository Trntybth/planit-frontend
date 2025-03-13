package com.example.planit_frontend;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomepage); // Make sure the layout is set first

        // Get the logo ImageView
        ImageView logo = findViewById(R.id.PlanItLogo);

        // Load the fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);

        // Start the fade-in animation
        logo.startAnimation(fadeIn);
    }
}
