package com.example.planit_frontend.view;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.planit_frontend.R;
import com.example.planit_frontend.model.User;
import com.example.planit_frontend.model.UserApiService;
import com.example.planit_frontend.viewmodel.LoginActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {

    private LoginActivityViewModel loginViewModel;
    private EditText usernameEditText;
    private RadioButton memberRadioButton, organisationRadioButton;
    private Button saveButton;

/*
    @Override // connects viewmodel
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Take user input from login activity xml
        usernameEditText = findViewById(R.id.usernameedittext);
        memberRadioButton = findViewById(R.id.memberbutton);
        organisationRadioButton = findViewById(R.id.organisationbutton);
        saveButton = findViewById(R.id.createaccountbutton);

        saveButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String userType = memberRadioButton.isChecked() ? "Member" : "Organisation";
            // Assuming the user has already logged in with Google

            saveUserToBackend(username, userType);
        });
    }

    private void saveUserToBackend(String username, String userType) {
        // Assuming you have already gotten the Google user's information
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleAccount != null) {
            String googleId = googleAccount.getId();
            String googleEmail = googleAccount.getEmail();

            // Create a User object to send to the backend
            User user = new User(); //fill

            // Call your backend API to save the user to MongoDB
            sendUserDataToBackend(user);
        }
    }

    private void sendUserDataToBackend(User user) {
        // Retrofit setup for calling your backend API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://your-backend-api-url/")  // Replace with your backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserApiService apiService = retrofit.create(UserApiService.class);

        // Make the API call to save the user
        Call<Void> call = apiService.saveUser(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // User saved successfully, navigate to the appropriate screen
                    navigateToNextScreen();
                } else {
                    // Handle failure (e.g., show error message)
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure (e.g., show error message)
            }
        });
    }
*/


// launch Google Sign-In Intent
private void signInWithGoogle() {
    Intent signInIntent = loginViewModel.getGoogleSignInClient().getSignInIntent();
    signInLauncher.launch(signInIntent);
}

// handles the Google Sign-In Result
private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                loginViewModel.handleSignInResult(task); // Pass result to ViewModel
            }
        }
);
}
