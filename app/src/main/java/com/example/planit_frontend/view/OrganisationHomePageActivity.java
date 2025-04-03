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
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.Event;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.SessionManager;
import com.example.planit_frontend.view.EventAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganisationHomePageActivity extends AppCompatActivity implements EventAdapter.OnItemClickListener {

    private RecyclerView eventsRecyclerView;
    private EventAdapter eventsAdapter;
    private List<Event> eventsList = new ArrayList<>();
    private ApiService apiService;
    private String username;  // Assuming you get the username of the logged-in user

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisationhomepage);


        sessionManager = new SessionManager(this);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        // Fetch events using username
        fetchOrganisationEvents();

        // Find the "Create Event" button
        Button createEventButton = findViewById(R.id.createNewEventButton);

        // Set an onClickListener for the button
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateEventClicked(v);
            }
        });
    }

    public void onCreateEventClicked(View view) {
        // Navigate to CreateEventActivity to create a new event
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Fetch events whenever the activity resumes
        fetchOrganisationEvents();
    }

    private void fetchOrganisationEvents() {
        // Replace 'username' with 'email' in the API call
        String email = sessionManager.getActiveEmail();  // Retrieve the email from session
        if (email != null) {
            Call<List<Event>> call = apiService.getEventsForOrganisationsByEmail(email);  // API call by email

            call.enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        eventsList.clear();
                        eventsList.addAll(response.body());
                        // Pass both parameters to the adapter
                        eventsAdapter = new EventAdapter(eventsList, OrganisationHomePageActivity.this);
                        eventsRecyclerView.setAdapter(eventsAdapter);
                    } else {
                        // Handle failure (e.g. show an error message)
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



}