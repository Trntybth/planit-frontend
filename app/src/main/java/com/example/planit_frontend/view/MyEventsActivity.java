package com.example.planit_frontend.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planit_frontend.R;
import com.example.planit_frontend.adapters.MyEventsAdapter;
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.Event;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyEventsActivity extends AppCompatActivity {

    private RecyclerView myEventsRecyclerView;
    private MyEventsAdapter myEventsAdapter;
    private List<Event> events;
    private String memberEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myevents);

        myEventsRecyclerView = findViewById(R.id.myEventsRecyclerView);
        myEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        events = new ArrayList<>();
        myEventsAdapter = new MyEventsAdapter(events, this);
        myEventsRecyclerView.setAdapter(myEventsAdapter);

        // Get the active member email using the session manager
        SessionManager sessionManager = new SessionManager(this); // Assuming your SessionManager class is used to handle session data
        memberEmail = sessionManager.getActiveEmail();  // Retrieve the active email from session manager

        // Load the events for this user
        loadSignedUpEvents();

        // Set up the "Go Back" button functionality
        Button goBackButton = findViewById(R.id.GoBackButton); // Ensure you have this button in your layout
        goBackButton.setOnClickListener(v -> onBackPressed());
    }

    private void loadSignedUpEvents() {
        // Use the RetrofitInstance you already have
        Retrofit retrofit = RetrofitInstance.getRetrofitInstance();

        // Create the ApiService from the retrofit instance
        ApiService apiService = retrofit.create(ApiService.class);

        // Call the API with the member email
        Call<List<Event>> call = apiService.getSignedUpEvents(memberEmail);

        // Enqueue the API call
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events.clear();
                    events.addAll(response.body());
                    myEventsAdapter.notifyDataSetChanged();
                } else {
                    // Handle case where no events are found
                    Toast.makeText(MyEventsActivity.this, "No events found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                // Handle network failure
                Toast.makeText(MyEventsActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
