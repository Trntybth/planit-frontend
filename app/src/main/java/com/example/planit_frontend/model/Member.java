package com.example.planit_frontend.model;


import java.util.ArrayList;
import java.util.List;

public class Member extends User {
    private List<Event> eventsList; public

    // constructor to create a Member object using Google Sign-In info and user input
    Member(String username, String email, String name) {
        this.setUsername(username);  // Set the username from user input
        this.setEmail(email);        // Set the email from Google
        this.setName(name);            // Optionally, set name from Google if available
        this.setUserType("Member");  // Set user type to "Member"
        this.eventsList = new ArrayList<>();  // Initialize with an empty list
    }


}
