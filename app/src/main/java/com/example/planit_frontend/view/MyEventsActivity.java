package com.example.planit_frontend.view;

import android.os.Bundle;
import android.util.Log;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyEventsActivity extends AppCompatActivity {

    private RecyclerView myEventsRecyclerView;
    private MyEventsAdapter myEventsAdapter;
    private List<Event> events;
    private String memberEmail;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myevents);

        myEventsRecyclerView = findViewById(R.id.myEventsRecyclerView);
        myEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        events = new ArrayList<>();

        // Initialize SessionManager and get the active member email
        sessionManager = new SessionManager(this);
        memberEmail = sessionManager.getActiveEmail();  // Retrieve the active email from session manager

        // Initialize ApiService
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        // Initialize the adapter with the events list and context
        myEventsAdapter = new MyEventsAdapter(events, this);
        myEventsRecyclerView.setAdapter(myEventsAdapter);

        // Load the events for this user
        loadSignedUpEvents();

        // Set up the "Go Back" button functionality
        Button goBackButton = findViewById(R.id.GoBackButton);
        goBackButton.setOnClickListener(v -> onBackPressed());
    }

    public void loadSignedUpEvents() {
        // Use the RetrofitInstance to get events from the backend
        Call<List<Event>> call = apiService.getSignedUpEvents(memberEmail);

        // Enqueue the API call
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> newEvents = response.body();
                    // Prevent duplicates by checking if event already exists in the list
                    for (Event event : newEvents) {
                        if (!events.contains(event)) {
                            events.add(event);
                        }
                    }
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
