package com.example.planit_frontend.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.Event;

import java.util.List;

public class OrganisationEventsAdapter extends RecyclerView.Adapter<OrganisationEventsAdapter.EventViewHolder> {

    private List<Event> eventsList;
    private OnItemClickListener listener;

    // Define the OnItemClickListener interface
    public interface OnItemClickListener {
        void onItemClick(Event event);
        void onUpdateClick(Event event); // Method for update button click
        void onDeleteClick(Event event); // New method for delete button click
    }

    public OrganisationEventsAdapter(List<Event> eventsList, OnItemClickListener listener) {
        this.eventsList = eventsList;
        this.listener = listener;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_updateevent, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventDescriptionTextView;
        TextView eventLocationTextView; // New field
        TextView eventCreatorTextView;

        TextView eventDateTextView;
        Button updateButton;
        Button deleteButton; // New field for delete button

        public EventViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventDescriptionTextView = itemView.findViewById(R.id.eventDescriptionTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
            eventCreatorTextView = itemView.findViewById(R.id.creatorEmailTextView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);

            updateButton = itemView.findViewById(R.id.updateButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = eventsList.get(position);

        Log.d("OrganisationEventsAdapter", "Creator Email: " + event.getCreatorEmail());


        holder.eventNameTextView.setText(event.getName());
        holder.eventDescriptionTextView.setText(event.getDescription());
        holder.eventLocationTextView.setText(event.getLocation());
        holder.eventCreatorTextView.setText(event.getCreatorEmail());

        holder.eventDateTextView.setText(event.getDate());

        holder.updateButton.setOnClickListener(v -> listener.onUpdateClick(event));

        // Set up the delete button
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(event));
    }

}
