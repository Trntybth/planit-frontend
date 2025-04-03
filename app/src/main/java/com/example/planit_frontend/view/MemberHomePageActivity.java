package com.example.planit_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.planit_frontend.model.SessionManager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;

public class MemberHomePageActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberhomepage);

        sessionManager = new SessionManager(this);

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void logoutUser() {
        // Clear the active session
        sessionManager.clearSession();

        // Redirect the user to the Create Account page
        Intent intent = new Intent(MemberHomePageActivity.this, SignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears back stack
        startActivity(intent);
    }
}