package com.example.planit_frontend.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.Event;

public class UpdateEventActivity extends AppCompatActivity {

    private TextView eventNameTextView, eventDescriptionTextView, eventLocationTextView, eventDateTextView;
    private Button updateEventButton;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateevent);

        // Find the views
        eventNameTextView = findViewById(R.id.eventNameTextView);
        eventDescriptionTextView = findViewById(R.id.eventDescriptionTextView);
        eventLocationTextView = findViewById(R.id.eventLocationTextView);
        eventDateTextView = findViewById(R.id.eventDateTextView);
        updateEventButton = findViewById(R.id.updateEventButton);

        // Get the event ID from the intent
        String eventId = getIntent().getStringExtra("event_id");

        // Fetch the event from shared preferences or your database using the event ID
        event = getEventById(eventId); // Implement this method to fetch the event details

        // Set event details in the TextViews
        if (event != null) {
            eventNameTextView.setText(event.getName());
            eventDescriptionTextView.setText(event.getDescription());
            eventLocationTextView.setText(event.getLocation());
            eventDateTextView.setText(event.getDate());
        }

        // Set click listener for update button
        updateEventButton.setOnClickListener(v -> {
            // Handle the event update logic here (e.g., send data back to the backend or shared preferences)
            // For now, just log the update
            Log.d("UpdateEventActivity", "Event Updated: " + event.getName());
        });
    }

    private Event getEventById(String eventId) {
        // This is where you fetch the event using the event ID (either from shared preferences, a database, or an API)
        // For now, just returning a dummy event for demonstration purposes
        return new Event(eventId, "Sample Event", "Event Description", "Event Location", "Event Creator", "2025-12-31");
    }
}

