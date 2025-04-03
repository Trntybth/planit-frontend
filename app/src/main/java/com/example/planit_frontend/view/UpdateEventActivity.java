package com.example.planit_frontend.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.ApiResponse;
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.Event;
import com.example.planit_frontend.model.RetrofitInstance;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpdateEventActivity extends AppCompatActivity {

    private EditText eventNameEditText, eventDescriptionEditText, eventLocationEditText;
    private Button updateEventButton, selectDateButton;
    private TextView eventDateTextView;
    private String eventId; // Store event ID
    private String updatedDate;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateevent);

        // Get event ID passed from previous activity
        eventId = getIntent().getStringExtra("event_id");

        // Initialize UI elements
        eventNameEditText = findViewById(R.id.eventName);
        eventDescriptionEditText = findViewById(R.id.eventDescription);
        eventLocationEditText = findViewById(R.id.eventLocation);
        updateEventButton = findViewById(R.id.updateEventButton);
        selectDateButton = findViewById(R.id.selectDateButton); // Button to select the date
        eventDateTextView = findViewById(R.id.eventDateTextView); // TextView to display the selected date

        // Initialize API Service
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        // Pre-fill the form with existing event details
        eventNameEditText.setText(getIntent().getStringExtra("event_name"));
        eventDescriptionEditText.setText(getIntent().getStringExtra("event_description"));
        eventDateTextView.setText(getIntent().getStringExtra("event_date")); // Set initial date

        // Set up date selection functionality
        selectDateButton.setOnClickListener(v -> showDatePickerDialog());

        // Set up the update event button
        updateEventButton.setOnClickListener(v -> updateEvent());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Format the selected date as a String (yyyy-MM-dd)
            updatedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay); // Format as "yyyy-MM-dd"

            // Display the formatted date in the TextView
            eventDateTextView.setText(updatedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void updateEvent() {
        String updatedName = eventNameEditText.getText().toString().trim();
        String updatedDescription = eventDescriptionEditText.getText().toString().trim();
        String updatedLocation = eventLocationEditText.getText().toString().trim();

        if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedDate == null) {
            Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Event updatedEvent = new Event(eventId, updatedName, updatedDescription, updatedLocation, updatedDate);

        apiService.updateEventById(eventId, updatedEvent).enqueue(new Callback<ApiResponse<Event>>() {
            @Override
            public void onResponse(Call<ApiResponse<Event>> call, Response<ApiResponse<Event>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(UpdateEventActivity.this, "Event updated successfully!", Toast.LENGTH_SHORT).show();
                    navigateBack();
                } else {
                    Toast.makeText(UpdateEventActivity.this, "Update failed, try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Event>> call, Throwable t) {
                Toast.makeText(UpdateEventActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateBack() {
        Intent intent = new Intent(this, OrganisationHomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
