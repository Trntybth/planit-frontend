package com.example.planit_frontend.adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.Event;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.SessionManager;
import com.example.planit_frontend.view.MyEventsActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.MyViewHolder> {

    private List<Event> events;
    private Context context;
    private ApiService apiService;
    private SessionManager sessionManager;

    // Constructor
    public MyEventsAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;

        // Initialize ApiService and SessionManager
        this.apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        this.sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_myevents, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventNameTextView.setText(event.getName());
        holder.eventDescriptionTextView.setText(event.getDescription());
        holder.eventLocationTextView.setText(event.getLocation());
        holder.creatorEmailTextView.setText(event.getCreatorEmail());
        holder.eventDateTextView.setText(event.getDate());

        // Existing remove button logic
        holder.removeButton.setOnClickListener(v -> {
            String memberEmail = sessionManager.getActiveEmail();
            if (memberEmail != null) {
                removeEventSignup(event.getId(), memberEmail, holder.getAdapterPosition());
            } else {
                Log.e("Remove Event", "User email not found");
            }
        });

        // ✅ Add to Calendar button logic
        holder.addToCalendarButton.setOnClickListener(v -> {
            addEventToGoogleCalendar(event);
        });
    }

    private void addEventToGoogleCalendar(Event event) {
        try {
            String eventDate = event.getDate(); // format should be yyyy-MM-dd (adjust parsing accordingly)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = sdf.parse(eventDate);

            // Adjust event duration (e.g., 1 hour)
            Calendar beginTime = Calendar.getInstance();
            beginTime.setTime(startDate);
            beginTime.set(Calendar.HOUR_OF_DAY, 9);  // Example start time (9 AM)
            beginTime.set(Calendar.MINUTE, 0);

            Calendar endTime = (Calendar) beginTime.clone();
            endTime.add(Calendar.HOUR, 1); // 1 hour event

            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.Events.TITLE, event.getName());
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation());
            intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription());
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());

            // Start the calendar intent
            context.startActivity(intent);
        } catch (ParseException e) {
            Log.e("Calendar Error", "Error parsing event date", e);
            Toast.makeText(context, "Invalid event date.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    // Method to remove the event signup
    private void removeEventSignup(String eventId, String memberEmail, int position) {
        apiService.removeEventSignup(memberEmail, eventId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    events.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, events.size());
                    Toast.makeText(context, "Successfully un-signed up.", Toast.LENGTH_SHORT).show();
                    Log.d("Remove Event", "Successfully un-signed up and removed event.");
                } else {
                    Toast.makeText(context, "Failed to un-sign up: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("Remove Event", "Un-signup failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Remove Event", "Network error during un-signup.", t);
            }
        });
    }



    // ViewHolder for each event item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventDescriptionTextView;
        TextView eventLocationTextView;
        TextView creatorEmailTextView;
        TextView eventDateTextView;
        Button removeButton;
        Button addToCalendarButton;  // ✅ new button

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventDescriptionTextView = itemView.findViewById(R.id.eventDescriptionTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
            creatorEmailTextView = itemView.findViewById(R.id.creatorEmailTextView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
            addToCalendarButton = itemView.findViewById(R.id.addToCalendarButton);  // ✅ bind button
        }
    }
}
