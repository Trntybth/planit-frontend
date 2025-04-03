package com.example.planit_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.RetrofitInstance;
import com.example.planit_frontend.model.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";
    private GoogleSignInOptions gso;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        sessionManager = new SessionManager(this);

        String email = sessionManager.getActiveEmail();
        if (email != null) {
            Log.d(TAG, "Retrieved Email from session: " + email);
        } else {
            Log.d(TAG, "No active email found in session.");
        }

        findViewById(R.id.sign_in_button).setOnClickListener(view -> signIn());
    }

    private void signIn() {
        Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            task.addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    if (task.isSuccessful()) {
                        GoogleSignInAccount account = task.getResult();
                        if (account != null) {
                            Log.d(TAG, "Google Sign-In successful: " + account.getEmail());
                            sessionManager.saveActiveEmail(account.getEmail());
                            proceedToNextScreen(account.getEmail());
                        }
                    } else {
                        Log.e(TAG, "Google Sign-In failed", task.getException());
                    }
                }
            });
        }
    }

    private void proceedToNextScreen(String email) {
        Log.d(TAG, "Proceeding with email: " + email);

        // Make the call expecting a String (either "Organisation" or "Member")
        Call<String> userTypeCall = apiService.getUserTypeByEmail(email);
        userTypeCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String userType = response.body().trim();  // Clean any leading/trailing spaces
                    Log.d(TAG, "User Type: " + userType);

                    // Handle user type logic (e.g., navigate to home page)
                    if (userType.equals("Organisation")) {
                        Intent intent = new Intent(LoginActivity.this, OrganisationHomePageActivity.class);
                        startActivity(intent);
                        finish();  // Close the current login screen
                    } else if (userType.equals("Member")) {
                        Intent intent = new Intent(LoginActivity.this, MemberHomePageActivity.class);
                        startActivity(intent);
                        finish();  // Close the current login screen
                    } else {
                        Toast.makeText(LoginActivity.this, "Unknown user type", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Failed to retrieve user type");
                    Toast.makeText(LoginActivity.this, "Failed to retrieve user type", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Error retrieving user type", t);
                Toast.makeText(LoginActivity.this, "Network error. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
