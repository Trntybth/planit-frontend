package com.example.planit_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.Event;
import com.example.planit_frontend.model.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrganisationHomePageActivity extends AppCompatActivity {

    // Initialize SessionManager
    SessionManager sessionManager;
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventsAdapter;
    private List<Event> eventsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisationhomepage);

        // Initialize the session manager
        sessionManager = new SessionManager(this);

        // Find the button by its ID
        Button createNewEventButton = findViewById(R.id.createNewEventButton);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);

        // Setup RecyclerView
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load events from shared preferences
        loadEventsFromSharedPreferences();

        // Set up the adapter for RecyclerView only after eventsList is populated
        eventsAdapter = new EventAdapter(eventsList);
        eventsRecyclerView.setAdapter(eventsAdapter);

        // Set an OnClickListener on the button
        createNewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to CreateEventActivity
                Intent intent = new Intent(OrganisationHomePageActivity.this, CreateEventActivity.class);
                startActivity(intent);  // Start the new activity
            }
        });
    }

    private void loadEventsFromSharedPreferences() {
        // Get the list of events from SharedPreferences (assuming the events are saved as a JSON string)
        // This can be replaced with your actual method of retrieving events from SharedPreferences
        String eventsJson = sessionManager.getUserEvents(); // Replace with the actual key

        // Log the raw JSON string
        Log.d("OrganisationHomePage", "Events JSON: " + eventsJson);

        if (eventsJson != null && !eventsJson.isEmpty()) {
            // Parse the JSON string into a list of Event objects
            eventsList = parseJsonToEventsList(eventsJson);
            // Log the parsed events list
            Log.d("OrganisationHomePage", "Parsed Events List: " + eventsList.toString());
        } else {
            // No events found, initialize an empty list
            eventsList = new ArrayList<>();
            Log.d("OrganisationHomePage", "No events found.");
        }
    }

    private List<Event> parseJsonToEventsList(String eventsJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Event>>(){}.getType();
        return gson.fromJson(eventsJson, type);
    }
}

