package com.example.planit_frontend.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_ACTIVE_EMAIL = "active_email";
    private static final String TAG = "SessionManager";  // Use TAG for consistent logging
    private SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Get the active email from SharedPreferences
    public String getActiveEmail() {
        String email = sharedPreferences.getString(KEY_ACTIVE_EMAIL, null);
        Log.d(TAG, "Retrieved email: " + email);  // Log here using TAG
        return email;
    }

    // Save the active email to SharedPreferences
    public void saveActiveEmail(String email) {
        if (email == null) {
            Log.e(TAG, "Cannot save null active email.");  // Log error if email is null
            return;
        }

        Log.d(TAG, "Saving active email: " + email);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACTIVE_EMAIL, email);
        editor.apply();  // Apply changes asynchronously
    }

    // Clear the session (remove email)
    public void clearSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_ACTIVE_EMAIL);  // Remove the active email from SharedPreferences
        editor.apply();  // Apply changes asynchronously
        Log.d(TAG, "Session cleared.");
    }
}
