package com.example.planit_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.Event;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.SessionManager;
import com.example.planit_frontend.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberHomePageActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private RecyclerView recyclerViewAllEvents;
    private AllEventsAdapter allEventsAdapter;
    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberhomepage);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Initialize the RecyclerView
        recyclerViewAllEvents = findViewById(R.id.allEventsRecyclerView);
        recyclerViewAllEvents.setLayoutManager(new LinearLayoutManager(this)); // Use a LinearLayoutManager for a vertical list

        // Fetch the events from the backend
        fetchEvents();

        // Set up logout button
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        // Clear the active session
        sessionManager.clearSession();

        // Redirect the user to the Create Account page
        Intent intent = new Intent(MemberHomePageActivity.this, SignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears back stack
        startActivity(intent);
    }

    private void fetchEvents() {
        // Correctly getting the ApiService instance from RetrofitInstance
        ApiService apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        Call<List<Event>> call = apiService.getAllEvents(); // Get all events from API

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventList = response.body(); // Assign the fetched events to the list
                    // Set the events list to the adapter
                    allEventsAdapter = new AllEventsAdapter(eventList, event -> {
                        // Handle the "Add to My Events" button click here
                        addEventToMyEvents(event);
                    });
                    recyclerViewAllEvents.setAdapter(allEventsAdapter); // Set the adapter for the RecyclerView
                } else {
                    Toast.makeText(MemberHomePageActivity.this, "Failed to fetch events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e("MemberHomePageActivity", "Error fetching events: " + t.getMessage());
                Toast.makeText(MemberHomePageActivity.this, "Error fetching events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEventToMyEvents(Event event) {
        // Handle the "Add to My Events" logic here, e.g., make a POST request or update a local list/database
        Toast.makeText(MemberHomePageActivity.this, "Event added to My Events", Toast.LENGTH_SHORT).show();
    }
}
