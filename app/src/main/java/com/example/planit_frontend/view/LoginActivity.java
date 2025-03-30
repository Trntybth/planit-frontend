package com.example.planit_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.Member;
import com.example.planit_frontend.model.Organisation;
import com.example.planit_frontend.model.RetrofitInstance;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;  // Import Gson for JSON conversion

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInOptions gso;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Google Sign-In options
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Initialize ApiService using RetrofitInstance
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        // Set up sign-in button click listener
        findViewById(R.id.sign_in_button).setOnClickListener(view -> signIn());
    }

    private void signIn() {
        Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of Google Sign-In
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            task.addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    if (task.isSuccessful()) {
                        GoogleSignInAccount account = task.getResult();
                        if (account != null) {
                            // Now use the new methods to check if the user exists as a Member or Organisation
                            checkMemberInDatabase(account.getEmail());
                        }
                    } else {
                        // Google Sign-In failed, display a message
                        Toast.makeText(LoginActivity.this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkMemberInDatabase(String email) {
        Log.d("LoginActivity", "Checking Member with email: " + email);  // Log the email
        // First, check if the user is a Member
        Call<Member> memberCall = apiService.getMemberByEmail(email);
        memberCall.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                Log.d("LoginActivity", "Member response code: " + response.code()); // Log response code
                Log.d("LoginActivity", "Member response body: " + response.body()); // Log the response body

                if (response.isSuccessful() && response.body() != null) {
                    // If Member found, save and proceed
                    setActiveMember(response.body());
                    Toast.makeText(LoginActivity.this, "Member login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MemberHomePageActivity.class));
                } else {
                    // If no Member, check if the user is an Organisation
                    Log.d("LoginActivity", "Member not found, checking Organisation...");
                    checkOrganisationInDatabase(email);
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Log.e("Error", "Failed to get Member: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error checking Member", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOrganisationInDatabase(String email) {
        Log.d("LoginActivity", "Checking Organisation with email: " + email);  // Log the email
        // Check if the user is an Organisation
        Call<Organisation> organisationCall = apiService.getOrganisationByEmail(email);
        organisationCall.enqueue(new Callback<Organisation>() {
            @Override
            public void onResponse(Call<Organisation> call, Response<Organisation> response) {
                Log.d("LoginActivity", "Organisation response code: " + response.code()); // Log response code
                Log.d("LoginActivity", "Organisation response body: " + response.body()); // Log the response body

                if (response.isSuccessful() && response.body() != null) {
                    // If Organisation found, save and proceed
                    setActiveOrganisation(response.body());
                    Toast.makeText(LoginActivity.this, "Organisation login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, OrganisationHomePageActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "No account found with this email.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Organisation> call, Throwable t) {
                Log.e("Error", "Failed to get Organisation: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error checking Organisation", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setActiveMember(Member member) {
        // Convert the member object to JSON and save it
        String memberJson = new Gson().toJson(member);
        getSharedPreferences("user_session", MODE_PRIVATE).edit()
                .putString("active_user", memberJson)
                .putString("active_user_type", "Member")
                .apply();
    }

    private void setActiveOrganisation(Organisation organisation) {
        // Convert the organisation object to JSON and save it
        String organisationJson = new Gson().toJson(organisation);
        getSharedPreferences("user_session", MODE_PRIVATE).edit()
                .putString("active_user", organisationJson)
                .putString("active_user_type", "Organisation")
                .apply();
    }
}
