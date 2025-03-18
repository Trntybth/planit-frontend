package com.example.planit_frontend.view;


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
import androidx.lifecycle.ViewModelProvider;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.Member;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.UserApiService;
import com.example.planit_frontend.viewmodel.LoginActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityViewModel loginActivityViewModel;
    private GoogleSignInClient googleSignInClient;

    private ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("LoginActivity", "Sign-in result code: " + result.getResultCode());
                Log.e("LoginActivity", "Sign-in failed or canceled. Result code: " + result.getResultCode());

                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult(ApiException.class);
                        Log.d("LoginActivity", "Sign-in successful: " + account.getEmail());
                        Toast.makeText(this, "Signed in as: " + account.getEmail(), Toast.LENGTH_SHORT).show();
                        handleSignIn(account);
                    } catch (ApiException e) {
                        Log.e("LoginActivity", "Google Sign-In failed: " + e.getStatusCode(), e);
                        Toast.makeText(this, "Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("LoginActivity", "Sign-in failed or canceled. Result code: " + result.getResultCode());
                    Toast.makeText(this, "Sign-In Cancelled. Result code: " + result.getResultCode(), Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize ViewModel
        loginActivityViewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);

        // Set up Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check if user is already signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            handleSignIn(account);  // Sign-in already done, proceed with member creation
        }

        // Google Sign-In Button click listener
        findViewById(R.id.sign_in_button).setOnClickListener(v -> {
            GoogleSignInAccount signedInAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (signedInAccount != null) {
                // If already signed in, proceed to the next step
                handleSignIn(signedInAccount);
            } else {
                // If not signed in, start the sign-in process
                Intent signInIntent = googleSignInClient.getSignInIntent();
                signInLauncher.launch(signInIntent);
            }
        });

        // Create Account Button click listener
        findViewById(R.id.createaccountbutton).setOnClickListener(v -> {
            Log.d("LoginActivity", "Create account button clicked");

            GoogleSignInAccount signedInAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (signedInAccount == null) {
                // If user is not signed in, ask them to sign in first
                Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show();
                Intent signInIntent = googleSignInClient.getSignInIntent();
                signInLauncher.launch(signInIntent);
            } else {
                // If user is signed in, proceed to create account
                String username = ((EditText) findViewById(R.id.usernameedittext)).getText().toString();
                RadioButton memberRadioButton = findViewById(R.id.memberbutton);
                RadioButton organisationRadioButton = findViewById(R.id.organisationbutton);

                if (memberRadioButton.isChecked()) {
                    Log.d("LoginActivity", "Member radio button selected");
                    createMember(signedInAccount, username);
                } else if (organisationRadioButton.isChecked()) {
                    Log.d("LoginActivity", "Organisation radio button selected");
                    createOrganisation(signedInAccount, username);
                } else {
                    Toast.makeText(this, "Please select Member or Organisation.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void handleSignIn(GoogleSignInAccount account) {
        if (account != null) {
            // Get the email and Google profile info
            String email = account.getEmail();

            // Show a toast for successful login
            Toast.makeText(this, "Successfully logged in as: " + email, Toast.LENGTH_SHORT).show();

            // Check if the "Create Account" button was clicked
            findViewById(R.id.createaccountbutton).setOnClickListener(v -> {
                String username = ((EditText) findViewById(R.id.usernameedittext)).getText().toString();

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
        Log.d("LoginActivity", "Creating new Member with username: " + username + " and email: " + account.getEmail());

        // Create a Member object with the data
        Member member = new Member(username, account.getEmail());  // 'userType' is automatically set to "Member"

        // Get the Retrofit instance and create the UserApiService
        UserApiService apiService = RetrofitInstance.getRetrofitInstance().create(UserApiService.class);

        // Make the network call asynchronously
        Call<Void> call = apiService.createMember(member);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Member was created successfully, navigate to Member's home page
                    goToMemberHomePage();
                } else {
                    // Log response for better error handling
                    Log.e("LoginActivity", "Failed to create Member, response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorResponse = response.errorBody().string();
                            Log.e("LoginActivity", "Error response: " + errorResponse);
                        } catch (IOException e) {
                            Log.e("LoginActivity", "Error reading the error body", e);
                        }
                    }
                    Toast.makeText(LoginActivity.this, "Failed to create Member", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("NetworkError", "Error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }



    private void createOrganisation(GoogleSignInAccount account, String username) {
        // Logic to create a new Organisation in your backend or database
        Log.d("LoginActivity", "Creating new Organisation with username: " + username + " and email: " + account.getEmail());
        // Example: send the data to your backend API to save this Organisation
        // Organisation organisation = new Organisation(username, account.getEmail());
        // sendToBackend(organisation);
        // Navigate to Organisation's home page
        goToOrganisationHomePage();
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
}
