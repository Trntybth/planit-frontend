package com.example.planit_frontend.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventsList;
    private OnItemClickListener listener;

    // Define the OnItemClickListener interface
    public interface OnItemClickListener {
        void onItemClick(Event event);
        void onUpdateClick(Event event); // New method for update button click
    }

    public EventAdapter(List<Event> eventsList, OnItemClickListener listener) {
        this.eventsList = eventsList;
        this.listener = listener;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
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
        Button updateButton;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventDescriptionTextView = itemView.findViewById(R.id.eventDescriptionTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView); // Add this in your XML layout
            updateButton = itemView.findViewById(R.id.updateButton);
        }
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = eventsList.get(position);
        holder.eventNameTextView.setText(event.getName());
        holder.eventDescriptionTextView.setText(event.getDescription());
        holder.eventLocationTextView.setText(event.getLocation()); // Bind location

        holder.updateButton.setOnClickListener(v -> listener.onUpdateClick(event));
    }


    public void updateEvents(List<Event> newEvents) {
        this.eventsList.clear(); // Clear the current list
        this.eventsList.addAll(newEvents); // Add new data
        notifyDataSetChanged(); // Notify RecyclerView to refresh
    }

}
