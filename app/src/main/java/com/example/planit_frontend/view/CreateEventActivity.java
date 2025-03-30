package com.example.planit_frontend.view;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.Event; // Use your custom Event model
import com.example.planit_frontend.model.Organisation;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.ApiService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private String selectedDate;


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

    private Organisation getStoredOrganisation() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        String organisationJson = sharedPreferences.getString("organisation", null);

        if (organisationJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(organisationJson, Organisation.class);
        } else {
            return null;  // Or handle the case when no Organisation is stored
        }
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
        Organisation creator = getStoredOrganisation();
        if (creator == null) {
            Toast.makeText(this, "No organisation found", Toast.LENGTH_SHORT).show();
            return;
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create event
        Event event = new Event(name, description, location, creator, dateString);

        userApiService.createEvent(event).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateEventActivity.this, "Event Created!", Toast.LENGTH_SHORT).show();

                    // ✅ Add event to stored Organisation's eventsCreated list
                    addEventToStoredOrganisation(event);

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
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        String organisationJson = sharedPreferences.getString("organisation", null);

        if (organisationJson != null) {
            Gson gson = new Gson();
            Organisation organisation = gson.fromJson(organisationJson, Organisation.class);

            // Add the new event to the list
            if (organisation.getEventsCreated() == null) {
                organisation.setEventsCreated(new ArrayList<>());
            }
            organisation.getEventsCreated().add(event);

            // Save updated organisation back to SharedPreferences
            sharedPreferences.edit().putString("organisation", gson.toJson(organisation)).apply();
        }
    }

}
