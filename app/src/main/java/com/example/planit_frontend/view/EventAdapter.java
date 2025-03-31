package com.example.planit_frontend.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventsList;
    private AdapterView.OnItemClickListener listener;
    // Constructor to receive events list
    public EventAdapter(List<Event> events) {
        this.eventsList = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each event
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = eventsList.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDescription.setText(event.getDescription());
        holder.eventLocation.setText(event.getLocation());
        holder.eventCreator.setText(event.getCreator());
        holder.eventDate.setText(event.getDate());
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    // ViewHolder class for event item views
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventDescription, eventLocation, eventCreator, eventDate;

        public EventViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.eventNameTextView);
            eventDescription = view.findViewById(R.id.eventDescriptionTextView);
            eventLocation = view.findViewById(R.id.eventLocationTextView);
            eventCreator = view.findViewById(R.id.eventCreatorTextView);
            eventDate = view.findViewById(R.id.eventDateTextView);
        }
    }
}
