package com.example.planit_frontend.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.ApiResponse;
import com.example.planit_frontend.model.ApiService;
import com.example.planit_frontend.model.Member;
import com.example.planit_frontend.model.Organisation;
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
import com.google.gson.Gson;  // Import Gson for JSON conversion

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInOptions gso;
    private ApiService apiService;
    private SessionManager sessionManager;  // Add SessionManager instance

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

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

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



    // Check if the user is a Member
    private void checkMemberInDatabase(String email) {
        Log.d("LoginActivity", "Checking Member with email: " + email);

        Call<ApiResponse<Member>> memberCall = apiService.getMemberByEmail(email);
        memberCall.enqueue(new Callback<ApiResponse<Member>>() {
            @Override
            public void onResponse(Call<ApiResponse<Member>> call, Response<ApiResponse<Member>> response) {
                Log.d("LoginActivity", "Member API Response Code: " + response.code());
                Log.d("LoginActivity", "Member API Response Body: " + new Gson().toJson(response.body()));

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Member> apiResponse = response.body();
                    Member member = apiResponse.getData();
                    String type = apiResponse.getType();

                    if ("Organisation".equals(type)) {
                        Log.d("LoginActivity", "User is an Organisation. Redirecting...");
                        Toast.makeText(LoginActivity.this, "Organisation login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, OrganisationHomePageActivity.class));
                    } else if (member != null) {
                        Log.d("LoginActivity", "Logging in as Member: " + member.getEmail());
                        sessionManager.saveActiveMember(member);
                        Toast.makeText(LoginActivity.this, "Member login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MemberHomePageActivity.class));
                    } else {
                        Log.d("LoginActivity", "No Member found, checking Organisation...");
                        checkOrganisationInDatabase(email);
                    }
                } else {
                    Log.d("LoginActivity", "Member API call failed, checking Organisation...");
                    checkOrganisationInDatabase(email);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Member>> call, Throwable t) {
                Log.e("LoginActivity", "Error: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error checking Member", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOrganisationInDatabase(String email) {
        Log.d("LoginActivity", "Checking Organisation with email: " + email);

        Call<ApiResponse<Organisation>> organisationCall = apiService.getOrganisationByEmail(email);
        organisationCall.enqueue(new Callback<ApiResponse<Organisation>>() {
            @Override
            public void onResponse(Call<ApiResponse<Organisation>> call, Response<ApiResponse<Organisation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Organisation organisation = response.body().getData();
                    Log.d("LoginActivity", "Received Organisation: " + organisation.getName());

                    // Here you can check if it's the correct user type
                    if ("Organisation".equals(organisation.getUserType())) {
                        Log.d("LoginActivity", "User is an Organisation. Redirecting...");
                        Toast.makeText(LoginActivity.this, "Organisation login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, OrganisationHomePageActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Member login detected", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("LoginActivity", "No Organisation found.");
                    Toast.makeText(LoginActivity.this, "No Organisation found with this email", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Organisation>> call, Throwable t) {
                Log.e("LoginActivity", "Error: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error checking Organisation", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
