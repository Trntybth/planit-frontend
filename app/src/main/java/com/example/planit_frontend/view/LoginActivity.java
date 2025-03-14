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
import com.example.planit_frontend.viewmodel.LoginActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;


public class LoginActivity extends AppCompatActivity {

    private LoginActivityViewModel loginViewModel;


    @Override // connects viewmodel
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);

        // Google Sign-In button click listener
        findViewById(R.id.sign_in_button).setOnClickListener(v -> signInWithGoogle());

        // Observe LiveData for user sign-in state
        loginViewModel.getUserAccount().observe(this, account -> {
            if (account != null) {
                Log.d("GoogleSignIn", "User signed in: " + account.getEmail());
            }
        });
    }

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
