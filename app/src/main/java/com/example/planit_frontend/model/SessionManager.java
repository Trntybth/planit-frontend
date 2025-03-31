package com.example.planit_frontend.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Get the active user (either Member or Organisation) from SharedPreferences
    public Object getActiveUser() {
        String userJson = sharedPreferences.getString("active_user", null);
        String userType = sharedPreferences.getString("active_user_type", null);

        if (userJson != null && userType != null) {
            if (userType.equals("Member")) {
                return gson.fromJson(userJson, Member.class);
            } else if (userType.equals("Organisation")) {
                return gson.fromJson(userJson, Organisation.class);
            }
        }
        return null;  // Return null if no active user found
    }

    // Get the active Member from SharedPreferences
    public Member getActiveMember() {
        String memberJson = sharedPreferences.getString("activeMember", null);
        if (memberJson != null) {
            return new Gson().fromJson(memberJson, Member.class);
        }
        return null;
    }

    public Organisation getActiveOrganisation() {
        String organisationJson = sharedPreferences.getString("active_user", null);  // Check for "active_user" key

        // Log all entries in SharedPreferences to see what is stored
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SessionManager", "Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        if (organisationJson != null) {
            // Assuming the stored data in "active_user" corresponds to the Organisation data
            Organisation organisation = new Gson().fromJson(organisationJson, Organisation.class);  // Deserialize into Organisation

            return organisation;
        }

        Log.d("SessionManager", "No organisation found in session.");
        return null;
    }





    // Save the active Member to SharedPreferences
    public void saveActiveMember(Member member) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String memberJson = new Gson().toJson(member);
        editor.putString("activeMember", memberJson);

        // Clear any previous "active_user_type"
        editor.putString("active_user_type", "Member");
        editor.apply();  // or commit() for synchronous saving
    }

    public void saveActiveOrganisation(Organisation organisation) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String organisationJson = new Gson().toJson(organisation);

        // Log the JSON string to verify that the object is being serialized correctly
        Log.d("SessionManager", "Organisation JSON: " + organisationJson);

        editor.putString("activeOrganisation", organisationJson);

        // Clear any previous "active_user_type"
        editor.putString("active_user_type", "Organisation");
        editor.apply();  // or commit() for synchronous saving
    }


    // Clear the session (remove active user)
    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }
}