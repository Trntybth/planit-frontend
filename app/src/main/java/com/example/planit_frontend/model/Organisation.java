package com.example.planit_frontend.model;

import java.util.List;

public class Organisation extends User {
    private List<Event> eventsCreated;

    // getter amd setter
    public List<Event> getEventsCreated() {
        return eventsCreated;
    }

    public void setEventsCreated(List<Event> eventsCreated) {
        this.eventsCreated = eventsCreated;
    }


}
