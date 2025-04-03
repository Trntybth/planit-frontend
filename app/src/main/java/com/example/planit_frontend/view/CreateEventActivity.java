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
import com.example.planit_frontend.model.Organisation;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.SessionManager;

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
    private SessionManager sessionManager;

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

    private void saveEventToDatabase() {
        String name = eventName.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();
        String location = eventLocation.getText().toString().trim();
        String dateString = eventDateTextView.getText().toString().trim();

        Log.d("CreateEventActivity", "Event Details - Name: " + name + ", Description: " + description + ", Location: " + location + ", Date: " + dateString);

        if (name.isEmpty() || description.isEmpty() || location.isEmpty() || dateString.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            Log.d("CreateEventActivity", "Fields are empty, aborting event creation.");
            return;
        }

        // Get the active email from session
        String activeEmail = sessionManager.getActiveEmail();
        if (activeEmail == null) {
            Toast.makeText(this, "No active session found. Please log in.", Toast.LENGTH_SHORT).show();
            Log.d("CreateEventActivity", "No active session found, cannot create event.");
            return;
        }

        Log.d("CreateEventActivity", "Creating event for user with email: " + activeEmail);

        // Create an event with the active email as the creator
        Event event = new Event(name, description, location, activeEmail, dateString); // Using email instead of username

        // Log the event object details before making the API call
        Log.d("CreateEventActivity", "Event object to be sent: " +
                "Name: " + event.getName() + ", " +
                "Description: " + event.getDescription() + ", " +
                "Location: " + event.getLocation() + ", " +
                "Creator: " + event.getCreatorEmail() + ", " +
                "Date: " + event.getDate());

        // Make POST request to create the event in the events table
        Log.d("CreateEventActivity", "Sending request to create event...");
        userApiService.createEvent(event).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Event createdEvent = response.body();
                    Toast.makeText(CreateEventActivity.this, "Event Created!", Toast.LENGTH_SHORT).show();
                    Log.d("CreateEventActivity", "Event created successfully: " + createdEvent);

                    // Update the organisation with the new event
                    updateOrganisationWithEvent(createdEvent, activeEmail);

                    finish();
                } else {
                    Toast.makeText(CreateEventActivity.this, "Failed to create event: " + response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("CreateEventActivity", "Error code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                // Log the failure reason if the request fails
                Log.e("CreateEventActivity", "Request failed: " + t.getMessage(), t);
                Toast.makeText(CreateEventActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrganisationWithEvent(Event event, String email) {
        Log.d("CreateEventActivity", "Updating organisation with event...");
        // Send the event to the backend to add to the organisation using email as identifier
        userApiService.addEventToOrganisation(email, event).enqueue(new Callback<Organisation>() {
            @Override
            public void onResponse(Call<Organisation> call, Response<Organisation> response) {
                if (response.isSuccessful()) {
                    Log.d("CreateEventActivity", "Organisation updated successfully with event.");
                } else {
                    Log.e("CreateEventActivity", "Failed to update organisation with event: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Organisation> call, Throwable t) {
                Log.e("CreateEventActivity", "Error updating organisation: " + t.getMessage());
            }
        });
    }
}