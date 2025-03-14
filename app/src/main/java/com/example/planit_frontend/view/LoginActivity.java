package com.example.planit_frontend.view;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.planit_frontend.R;

import com.example.planit_frontend.viewmodel.LoginActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityViewModel loginViewModel;
    private EditText usernameEditText;
    private RadioButton memberRadioButton, organisationRadioButton;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);

        usernameEditText = findViewById(R.id.usernameedittext);
        memberRadioButton = findViewById(R.id.memberbutton);
        organisationRadioButton = findViewById(R.id.organisationbutton);
        saveButton = findViewById(R.id.createaccountbutton);

        findViewById(R.id.sign_in_button).setOnClickListener(v -> signInWithGoogle());

        saveButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String userType = memberRadioButton.isChecked() ? "Member" : "Organisation";
            loginViewModel.saveUserToBackend(username, userType, this);
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = loginViewModel.getGoogleSignInClient().getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    loginViewModel.handleSignInResult(task, this);
                }
            }
    );
}
