package com.example.planit_frontend.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.Event;
import com.example.planit_frontend.model.Member;
import com.example.planit_frontend.model.Organisation;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventName, eventDescription, eventLocation;
    private TextView eventDateTextView;
    private Button selectDateButton, createEventButton;
    private ApiService userApiService;

    // Initialize SessionManager
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createevent);

        eventName = findViewById(R.id.eventName);
        eventDescription = findViewById(R.id.eventDescription);
        eventLocation = findViewById(R.id.eventLocation);
        eventDateTextView = findViewById(R.id.eventDateTextView);
        selectDateButton = findViewById(R.id.selectDateButton);
        createEventButton = findViewById(R.id.createEventButton);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        userApiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        selectDateButton.setOnClickListener(v -> showDatePickerDialog());

        createEventButton.setOnClickListener(v -> saveEventToDatabase());
        Log.d("SessionManager", "Organisation from session: " + sessionManager.getActiveOrganisation());

    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Format the selected date as a String (yyyy-MM-dd)
            String dateString = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay); // Format as "yyyy-MM-dd"

            // Display the formatted date in the TextView
            eventDateTextView.setText(dateString);
        }, year, month, day);

        datePickerDialog.show();
    }

    private Member getActiveMember() {
        return sessionManager.getActiveMember();  // Use SessionManager to retrieve the active member
    }

    private Organisation getActiveOrganisation() {
        Organisation organisation = sessionManager.getActiveOrganisation();
        if (organisation == null) {
            Log.d("CreateEventActivity", "No organisation found in session.");
        } else {
            Log.d("CreateEventActivity", "Found organisation: " + organisation.getName());
        }
        return organisation;
    }


    private void saveEventToDatabase() {
        String name = eventName.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();
        String location = eventLocation.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || location.isEmpty() || eventDateTextView.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected date as a string
        String dateString = eventDateTextView.getText().toString();
        Organisation creator = getActiveOrganisation();
        if (creator == null) {
            Toast.makeText(this, "No organisation found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Now creator is just a String, so we pass the name directly
        String creatorName = creator.getName();  // This is the organisation's name
        Event event = new Event(name, description, location, creatorName, dateString);  // Set creator as the name

        // Make POST request to create the event in the events table
        userApiService.createEvent(event).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Event createdEvent = response.body();
                    Toast.makeText(CreateEventActivity.this, "Event Created!", Toast.LENGTH_SHORT).show();

                    // ✅ Add event to stored Organisation's eventsCreated list locally
                    addEventToStoredOrganisation(createdEvent);

                    // Optionally, also update the backend with the event in the organisation's event list (if necessary)
                    updateOrganisationWithEvent(createdEvent);

                    finish();
                } else {
                    Toast.makeText(CreateEventActivity.this, "Failed to create event: " + response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("CreateEvent", "Error code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addEventToStoredOrganisation(Event event) {
        Organisation organisation = getActiveOrganisation();
        if (organisation != null) {
            // Check if the event list is null, and initialize it if needed
            if (organisation.getEventsCreated() == null) {
                organisation.setEventsCreated(new ArrayList<>());
            }

            // Add the new event to the organisation's event list
            organisation.getEventsCreated().add(event);

            // Log the event addition for debugging
            Log.d("CreateEvent", "Adding event: " + event.getName() + " to organisation's event list");

            // Save the updated organisation back to SessionManager
            sessionManager.saveActiveOrganisation(organisation);

            // Optionally, also save the events to SharedPreferences (if desired)
            String eventsJson = new Gson().toJson(organisation.getEventsCreated());
            sessionManager.saveEventsToSharedPreferences(eventsJson);  // Call your method to save to SharedPreferences
        } else {
            Log.e("CreateEvent", "No organisation found to add event");
        }
    }

    // This method will ensure the updated organisation's eventsCreated list gets updated
    private void updateOrganisationWithEvent(Event event) {
        Organisation organisation = getActiveOrganisation();
        if (organisation != null) {
            // Ensure the organisation username is fetched
            String organisationUsername = organisation.getUsername();

            // Make PUT request to update the organisation with the newly created event
            userApiService.addEventToOrganisation(organisationUsername, event).enqueue(new Callback<Organisation>() {
                @Override
                public void onResponse(Call<Organisation> call, Response<Organisation> response) {
                    if (response.isSuccessful()) {
                        // Optionally, save the updated organisation after the event is added
                        sessionManager.saveActiveOrganisation(response.body());
                    } else {
                        Log.e("CreateEvent", "Failed to update organisation with event: " + response.code() + " " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Organisation> call, Throwable t) {
                    Log.e("CreateEvent", "Error updating organisation: " + t.getMessage());
                }
            });
        } else {
            Log.e("CreateEvent", "No organisation found to update");
        }
    }

}
