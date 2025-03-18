package com.example.planit_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;

public class OrganisationHomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisationhomepage);

        // Find the button by its ID
        Button createNewEventButton = findViewById(R.id.createNewEventButton);

        // Set an OnClickListener on the button
        createNewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to ActivityCreateEvent
                Intent intent = new Intent(OrganisationHomePageActivity.this, CreateEventActivity.class);
                startActivity(intent);  // Start the new activity
            }
        });
    }
}
