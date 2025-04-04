package com.example.planit_frontend.view;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.Member;
import com.example.planit_frontend.model.Organisation;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.ApiService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;

    // Google Sign-In result launcher
// Google Sign-In result launcher
    private final ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();
                            Log.d("SignupActivity", "Google Sign-In successful: " + account.getEmail());
                        } else {
                            Log.d("SignupActivity", "Google Sign-In failed or cancelled.");
                        }
                    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        // Set up Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Find the button
        Button signInRedirectButton = findViewById(R.id.sign_in_redirect_button);
        signInRedirectButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Google Sign-In Button click listener
        findViewById(R.id.createaccountbutton).setOnClickListener(v -> {
            Log.d("SignupActivity", "Create account button clicked");

            GoogleSignInAccount signedInAccount = GoogleSignIn.getLastSignedInAccount(this);

            if (signedInAccount == null) {
                // Not signed in -> Force Sign-In
                Log.d("SignupActivity", "User not signed in. Prompting Google Sign-In.");
                forceFullSignOutAndOpenPicker();
            } else {
                // Already signed in -> Proceed to account creation
                Log.d("SignupActivity", "User already signed in: " + signedInAccount.getEmail());

                String username = ((EditText) findViewById(R.id.usernameedittext)).getText().toString();
                RadioButton memberRadioButton = findViewById(R.id.memberbutton);
                RadioButton organisationRadioButton = findViewById(R.id.organisationbutton);

                if (memberRadioButton.isChecked()) {
                    Log.d("SignupActivity", "Member radio button selected");
                    createMember(signedInAccount, username);
                } else if (organisationRadioButton.isChecked()) {
                    Log.d("SignupActivity", "Organisation radio button selected");
                    createOrganisation(signedInAccount, username);
                } else {
                    Toast.makeText(this, "Please select Member or Organisation.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Force sign-out, revoke access, and open the Google Sign-In account picker.
     */
    private void forceFullSignOutAndOpenPicker() {
        googleSignInClient.revokeAccess().addOnCompleteListener(this, revokeTask -> {
            Log.d("SignupActivity", "Google Sign-In fully revoked.");

            googleSignInClient.signOut().addOnCompleteListener(this, signOutTask -> {
                Log.d("SignupActivity", "Signed out completely.");

                // Open Google account picker (forces fresh sign-in)
                Intent signInIntent = googleSignInClient.getSignInIntent();
                signInIntent.putExtra("force_code_for_refresh_token", true); // Ensures full reset
                signInLauncher.launch(signInIntent);
            });
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        resetGoogleSignIn(null); // Reset sign-in on every visit
    }



    /**
     * Ensures Google Sign-In is fully reset before proceeding.
     * Calls onResetComplete.run() when done (if not null).
     */


    private void resetGoogleSignIn(@Nullable Runnable onResetComplete) {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            googleSignInClient.revokeAccess().addOnCompleteListener(this, revokeTask -> {
                Log.d("SignupActivity", "Google Sign-In fully reset.");
                if (onResetComplete != null) {
                    onResetComplete.run(); // Proceed only after reset
                }
            });
        });
    }

    private void handleSignIn(GoogleSignInAccount account) {
        if (account != null) {
            // Get the email and Google profile info
            String email = account.getEmail();
            String name = account.getDisplayName();  // This will return the user's name from the Google account

            // Check if the "Create Account" button was clicked
            findViewById(R.id.createaccountbutton).setOnClickListener(v -> {
                String username = name != null && !name.isEmpty() ? name : ((EditText) findViewById(R.id.usernameedittext)).getText().toString();

                // Check if the user has selected "Member"
                RadioButton memberRadioButton = findViewById(R.id.memberbutton);
                if (memberRadioButton.isChecked()) {
                    createMember(account, username);
                } else {
                    Toast.makeText(this, "Please select Member or Organisation.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
        }
    }



    private void createMember(GoogleSignInAccount account, String username) {
        if (account == null || account.getEmail() == null) {
            Toast.makeText(SignupActivity.this, "Google account details are missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = account.getEmail();
        ApiService apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        // First, check if the member already exists
        apiService.checkMemberExists(email, username).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    // Member already exists
                    Toast.makeText(SignupActivity.this, "Username or Email already taken!", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed with creating the member
                    String displayName = account.getDisplayName() != null ? account.getDisplayName() : username;
                    Member member = new Member(username, email, displayName);

                    apiService.createMember(member).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                goToMemberHomePage();
                            } else {
                                Toast.makeText(SignupActivity.this, "Failed to create Member", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(SignupActivity.this, "Network error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Network error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void createOrganisation(GoogleSignInAccount account, String orgName) {
        if (account == null || account.getEmail() == null) {
            Toast.makeText(this, "Google Sign-In account is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = account.getEmail();
        String displayName = account.getDisplayName();

        // Ensure orgName is not empty
        final String finalOrgName = (orgName == null || orgName.isEmpty()) ? generateRandomUsername() : orgName;

        ApiService apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        // Check if the organisation already exists
        apiService.checkOrganisationExists(email, finalOrgName).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    Toast.makeText(SignupActivity.this, "Organisation name or email already taken!", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed with organisation creation
                    Organisation organisation = new Organisation(finalOrgName, email, displayName);

                    // Store organisation data in SharedPreferences
                    Gson gson = new Gson();
                    SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
                    sharedPreferences.edit().putString("organisation", gson.toJson(organisation)).apply();

                    apiService.createOrganisation(organisation).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                goToOrganisationHomePage();
                            } else {
                                Toast.makeText(SignupActivity.this, "Failed to create Organisation. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(SignupActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Network error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private Organisation getStoredOrganisation() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        String organisationJson = sharedPreferences.getString("organisation", null);

        if (organisationJson != null) {
            Gson gson = new Gson();
            Organisation organisation = gson.fromJson(organisationJson, Organisation.class);
            if (organisation != null) {
                return organisation;
            } else {
                Log.e("SharedPreferences", "Failed to deserialize Organisation object.");
                return null;  // Handle invalid or corrupted data.
            }
        } else {
            Log.e("SharedPreferences", "No organisation data found in SharedPreferences.");
            return null;  // Handle case where no data is stored.
        }
    }


    private void goToMemberHomePage() {
        Intent intent = new Intent(this, MemberHomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToOrganisationHomePage() {
        // Navigate to the organisation home page
        Intent intent = new Intent(this, OrganisationHomePageActivity.class);
        startActivity(intent);
        finish();
    }

    // Method to generate a random username
    private String generateRandomUsername() {
        // Define the characters to use in the username
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder username = new StringBuilder();

        // Generate a random username of a desired length (e.g., 8 characters)
        for (int i = 0; i < 8; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            username.append(characters.charAt(randomIndex));
        }

        return username.toString();  // Return the generated random username
    }

}