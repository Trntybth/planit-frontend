package com.example.planit_frontend.model;


import java.util.ArrayList;
import java.util.List;

public class Member extends User {
    private List<Event> eventsList; public


    // getter setter
    List<Event> getEventsList() {
        return eventsList;
    }
    public void setEventsList(List<Event> eventsList) {
        this.eventsList = eventsList;
    }

    // constructor to create a Member object using Google Sign-In info and user input
    public Member(String googleId, String email, String username) {
        this.setUsername(username);     // Set the username from user input
        this.setContactEmail(email);    // Set the email from Google
        this.setName("");               // Optionally, set name from Google if available
        this.setUserType("Member");     // Set user type to "Member"
        this.eventsList = new ArrayList<>(); // Initialize with an empty list
    }



}
