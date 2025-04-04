package com.example.planit_frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.Event;

import java.util.List;

public class AllEventsAdapter extends RecyclerView.Adapter<AllEventsAdapter.EventViewHolder> {

    private List<Event> eventsList;
    private OnItemClickListener listener;
    private Context context;

    public AllEventsAdapter(List<Event> eventList, Context context, OnItemClickListener listener) {
        this.eventsList = eventList;
        this.context = context;
        this.listener = listener;
    }

    // Define the OnItemClickListener interface
    public interface OnItemClickListener {
        void onAddToMyEventsClick(Event event);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item (e.g., item_signup_event.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allevents, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = eventsList.get(position);

        holder.eventNameTextView.setText(event.getName());
        holder.eventDescriptionTextView.setText(event.getDescription());
        holder.eventLocationTextView.setText(event.getLocation());
        holder.eventDateTextView.setText(event.getDate());

        holder.addToMyEventsButton.setOnClickListener(v -> {
            listener.onAddToMyEventsClick(event); // Pass event to listener
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventDescriptionTextView;
        TextView eventLocationTextView;
        TextView eventDateTextView;
        Button addToMyEventsButton;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventDescriptionTextView = itemView.findViewById(R.id.eventDescriptionTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
            addToMyEventsButton = itemView.findViewById(R.id.addButton);
        }
    }
}
