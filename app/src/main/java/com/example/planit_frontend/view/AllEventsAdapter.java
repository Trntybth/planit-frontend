package com.example.planit_frontend.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.Event;

import java.util.List;

public class AllEventsAdapter extends RecyclerView.Adapter<AllEventsAdapter.EventViewHolder> {

    private List<Event> eventsList;
    private OnItemClickListener listener;

    // Define the OnItemClickListener interface
    public interface OnItemClickListener {
        void onAddToMyEventsClick(Event event); // Handle the "Add to My Events" button click
    }

    // Constructor to initialize the event list and listener
    public AllEventsAdapter(List<Event> eventsList, OnItemClickListener listener) {
        this.eventsList = eventsList;
        this.listener = listener;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item (e.g., item_signup_event.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allevents, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        // Get the event object at the current position
        Event event = eventsList.get(position);

        // Bind data to views
        holder.eventNameTextView.setText(event.getName());
        holder.eventDescriptionTextView.setText(event.getDescription());
        holder.eventLocationTextView.setText(event.getLocation());
        holder.eventDateTextView.setText(event.getDate());


        // Set up the button's click listener
        holder.addToMyEventsButton.setOnClickListener(v -> {
            // When the button is clicked, trigger the listener's method
            listener.onAddToMyEventsClick(event);
        });
    }

    @Override
    public int getItemCount() {
        // Return the number of events in the list
        return eventsList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // Declare views for event data and the button
        TextView eventNameTextView;
        TextView eventDescriptionTextView;
        TextView eventLocationTextView;
        TextView eventDateTextView;
        Button addToMyEventsButton; // Button for adding to "My Events"

        public EventViewHolder(View itemView) {
            super(itemView);
            // Initialize views from the layout
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventDescriptionTextView = itemView.findViewById(R.id.eventDescriptionTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);

            addToMyEventsButton = itemView.findViewById(R.id.addButton); // Button to add event to "My Events"
        }
    }


}