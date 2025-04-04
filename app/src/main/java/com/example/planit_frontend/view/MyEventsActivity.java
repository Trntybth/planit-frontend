package com.example.planit_frontend.view;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.planit_frontend.R;

public class MyEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myevents); // Ensure this is your correct layout file

        // Find the "Go Back" button by its ID
        Button goBackButton = findViewById(R.id.GoBackButton);

        // Set an OnClickListener to handle the click event
        goBackButton.setOnClickListener(v -> {
            // Navigate back to the MemberHomePageActivity
            Intent intent = new Intent(MyEventsActivity.this, MemberHomePageActivity.class);
            startActivity(intent); // Start the activity
            finish(); // Optionally, you can call finish() to remove MyEventsActivity from the back stack
        });
    }
}
