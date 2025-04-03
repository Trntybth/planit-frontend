
package com.example.planit_frontend.model;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Organisation extends User {

    // constructor to create an Organisation object using Google Sign-In info and user input
    public Organisation(String username, String email, String name) {
        this.setUsername(username);      // Set the username from user input
        this.setName(name);               // Optionally, set name from Google if available
        this.setEmail(email);     // Set the email from Google
        this.setUserType("Organisation");  // Set user type to "Organisation"
    }

}
