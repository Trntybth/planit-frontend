package com.example.planit_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planit_frontend.adapters.AllEventsAdapter;
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.Event;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.SessionManager;
import com.example.planit_frontend.R;
import com.example.planit_frontend.model.SignUpRequest;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberHomePageActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private RecyclerView recyclerViewAllEvents;
    private AllEventsAdapter allEventsAdapter;
    private List<Event> eventList = new ArrayList<>();
    private ApiService apiService; // ✅ clearly declare ApiService

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberhomepage);

        sessionManager = new SessionManager(this);

        // ✅ Clearly initialize apiService here
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        recyclerViewAllEvents = findViewById(R.id.allEventsRecyclerView);
        recyclerViewAllEvents.setLayoutManager(new LinearLayoutManager(this));

        fetchEvents();

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logoutUser());

        Button viewMyEventsButton = findViewById(R.id.viewMyEventsButton);
        viewMyEventsButton.setOnClickListener(v -> navigateToMyEventsActivity());
    }

    private void logoutUser() {
        sessionManager.clearSession();
        Intent intent = new Intent(MemberHomePageActivity.this, SignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void fetchEvents() {
        ApiService apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        Call<List<Event>> call = apiService.getAllEvents();

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventList.clear();
                    eventList.addAll(response.body());

                    allEventsAdapter = new AllEventsAdapter(eventList, MemberHomePageActivity.this, event -> {
                        addEventToMyEvents(event);
                    });
                    recyclerViewAllEvents.setAdapter(allEventsAdapter);
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
        String memberEmail = sessionManager.getActiveEmail();
        String eventId = event.getId();
        SignUpRequest signUpRequest = new SignUpRequest(true);

        apiService.signUpForEvent(memberEmail, eventId, signUpRequest)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MemberHomePageActivity.this, "Event added to My Events", Toast.LENGTH_SHORT).show();
                        } else {
                            if(response.code() == 409){
                                Toast.makeText(MemberHomePageActivity.this, "Already signed up.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MemberHomePageActivity.this, "Signup failed: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(MemberHomePageActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void navigateToMyEventsActivity() {
        Intent intent = new Intent(MemberHomePageActivity.this, MyEventsActivity.class);
        startActivity(intent);
    }
}
