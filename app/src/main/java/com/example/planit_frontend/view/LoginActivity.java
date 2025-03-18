package com.example.planit_frontend.view;

import android.app.Activity;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;


public class LoginActivity extends AppCompatActivity {



    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    private GoogleSignInClient googleSignInClient;

    private ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult();
                    handleSignIn(account);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Google Sign-In Button click listener
        findViewById(R.id.sign_in_button).setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            signInLauncher.launch(signInIntent);
        });
    }


    private void handleSignIn(GoogleSignInAccount account) {
        // Handle the signed-in account. You can pass the data to your ViewModel or directly to the UI
        if (account != null) {
            // Example: Show the user's email
            String email = account.getEmail();
            Toast.makeText(this, "Signed in as: " + email, Toast.LENGTH_SHORT).show();

            // Navigate to the home page or perform the registration
            // If the user is new, register them, else go to their home page
            // For example, if you have a condition to check if the user is registered:
            if (isNewUser(account)) {
                // Create new user (call your registration process)
                createNewUser(account);
            } else {
                // Navigate to user home page
                goToUserHomePage();
            }
        } else {
            Log.e("LoginActivity", "Google Sign-In failed");
        }
    }

    private boolean isNewUser(GoogleSignInAccount account) {
        // Logic to check if user is new or already exists (e.g., by checking in your database)
        return true; // Example: assume the user is new
    }

    private void createNewUser(GoogleSignInAccount account) {
        // Logic to create the user in your system using the Google Sign-In data (name, email, etc.)
        Log.d("LoginActivity", "Creating new user with email: " + account.getEmail());
    }

    private void goToUserHomePage() {
        // Navigate to the home page (example for Member's home page)
        Intent intent = new Intent(this, MemberHomePageActivity.class);
        startActivity(intent);
        finish();
    }
}
