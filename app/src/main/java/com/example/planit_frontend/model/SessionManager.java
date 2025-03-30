package com.example.planit_frontend.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

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
        Object user = getActiveUser();
        if (user instanceof Member) {
            return (Member) user;
        }
        return null;
    }

    // Get the active Organisation from SharedPreferences
    public Organisation getActiveOrganisation() {
        Object user = getActiveUser();
        if (user instanceof Organisation) {
            return (Organisation) user;
        }
        return null;
    }

    // Save the active Member to SharedPreferences
    public void saveActiveMember(Member member) {
        String memberJson = gson.toJson(member);
        sharedPreferences.edit()
                .putString("active_user", memberJson)
                .putString("active_user_type", "Member")
                .apply();
    }

    // Save the active Organisation to SharedPreferences
    public void saveActiveOrganisation(Organisation organisation) {
        String organisationJson = gson.toJson(organisation);
        sharedPreferences.edit()
                .putString("active_user", organisationJson)
                .putString("active_user_type", "Organisation")
                .apply();
    }

    // Clear the session (remove active user)
    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }
}
