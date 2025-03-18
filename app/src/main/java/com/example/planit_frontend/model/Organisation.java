package com.example.planit_frontend.model;

import java.util.ArrayList;
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

    // constructor to create an Organisation object using Google Sign-In info and user input
    public Organisation(String username, String email, String name) {
        this.setUsername(username);      // Set the username from user input
        this.setEmail(email);     // Set the email from Google
        this.setName(name);               // Optionally, set name from Google if available
        this.setUserType("Organisation");  // Set user type to "Organisation"
        this.eventsCreated = new ArrayList<>();  // Initialize with an empty list
    }
}
