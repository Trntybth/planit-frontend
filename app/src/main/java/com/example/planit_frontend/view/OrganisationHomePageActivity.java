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

import com.example.planit_frontend.R;
import com.example.planit_frontend.adapters.OrganisationEventsAdapter;
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.Event;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganisationHomePageActivity extends AppCompatActivity implements OrganisationEventsAdapter.OnItemClickListener {

    private RecyclerView eventsRecyclerView;
    private OrganisationEventsAdapter eventsAdapter;
    private List<Event> eventsList = new ArrayList<>();
    private ApiService apiService;
    private String username;  // Assuming you get the username of the logged-in user

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisationhomepage);

        sessionManager = new SessionManager(this);

        // Initialize RecyclerView
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize API service
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        // Initialize the adapter with an empty events list
        eventsAdapter = new OrganisationEventsAdapter(eventsList, OrganisationHomePageActivity.this);
        eventsRecyclerView.setAdapter(eventsAdapter); // Attach the adapter to the RecyclerView

        // Fetch events
        fetchOrganisationEvents();

        // Create Event Button
        Button createEventButton = findViewById(R.id.createNewEventButton);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateEventClicked(v); // Explicitly call the onCreateEventClicked method
            }
        });


        // Logout Button
        Button logoutButton = findViewById(R.id.logoutButtonOrg);
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch events whenever the activity resumes
        fetchOrganisationEvents();
    }

    private void fetchOrganisationEvents() {
        // Retrieve the logged-in user's email from SessionManager
        String email = sessionManager.getActiveEmail();

        if (email != null) {
            // Call the API to get events for this specific email
            Call<List<Event>> call = apiService.getEventsByEmail(email);

            call.enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("API Response", "Events: " + response.body().toString());  // Log the events returned from the API
                        eventsList.clear(); // Clear the existing list
                        // Filter the events based on the logged-in user's email
                        for (Event event : response.body()) {
                            Log.d("Event", "Event CreatorEmail: " + event.getCreatorEmail());  // Log the creatorEmail
                            if (event.getCreatorEmail() != null && event.getCreatorEmail().equals(email)) {
                                eventsList.add(event);
                            }
                        }
                        // Notify the adapter that the data has changed
                        eventsAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Error", "Failed to load events.");
                    }
                }

                @Override
                public void onFailure(Call<List<Event>> call, Throwable t) {
                    Log.e("Error", t.getMessage());
                }
            });
        } else {
            Log.e("Error", "Email is null.");
        }
    }

    @Override
    public void onItemClick(Event event) {
        // Handle the event item click, e.g., show details
        Toast.makeText(this, "Event clicked: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateClick(Event event) {
        // Log the event details to check what's being passed
        Log.d("UpdateEvent", "Event ID: " + event.getId());
        Log.d("UpdateEvent", "Event Name: " + event.getName());
        Log.d("UpdateEvent", "Event Description: " + event.getDescription());
        Log.d("UpdateEvent", "Event Location: " + event.getLocation());
        Log.d("UpdateEvent", "Event Date: " + event.getDate());

        // Handle the update button click
        Toast.makeText(this, "Update event: " + event.getName(), Toast.LENGTH_SHORT).show();

        // Create an Intent to open UpdateEventActivity and pass the event details
        Intent intent = new Intent(this, UpdateEventActivity.class);
        intent.putExtra("eventId", event.getId());  // Pass eventId to the next activity
        intent.putExtra("eventName", event.getName());  // Pass event name if necessary
        intent.putExtra("eventDescription", event.getDescription());  // Pass event description if necessary
        intent.putExtra("eventLocation", event.getLocation());  // Pass event location if necessary
        intent.putExtra("eventDate", event.getDate());  // Pass event date if necessary
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Event event) {
        // Show a confirmation message to the user (optional)
        Toast.makeText(this, "Deleting event: " + event.getName(), Toast.LENGTH_SHORT).show();

        // Call the API to delete the event from the backend
        Call<Void> deleteCall = apiService.deleteEvent(event.getId());  // Assuming you have a deleteEvent method in ApiService

        deleteCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // If the deletion is successful, remove the event from the list and notify the adapter
                    eventsList.remove(event);  // Remove the event from the list
                    eventsAdapter.notifyDataSetChanged();  // Notify the adapter that the data has changed

                    // Show a success message
                    Toast.makeText(OrganisationHomePageActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // If the deletion failed (e.g., event not found), show an error message
                    Toast.makeText(OrganisationHomePageActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // If the network call fails, show an error message
                Toast.makeText(OrganisationHomePageActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
        // Clear the active session
        sessionManager.clearSession();

        // Redirect the user to the Signup page
        Intent intent = new Intent(OrganisationHomePageActivity.this, SignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears back stack
        startActivity(intent);
    }

    public void onCreateEventClicked(View view) {
        // Navigate to CreateEventActivity to create a new event
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }
}
