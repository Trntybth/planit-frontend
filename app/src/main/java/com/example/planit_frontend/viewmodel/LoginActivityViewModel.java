package com.example.planit_frontend.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.planit_frontend.model.Member;
import com.example.planit_frontend.model.Organisation;
import com.example.planit_frontend.model.User;
import com.example.planit_frontend.model.UserApiService;
import com.example.planit_frontend.view.MemberHomePageActivity;
import com.example.planit_frontend.view.OrganisationHomePageActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivityViewModel extends AndroidViewModel {
    private final GoogleSignInClient googleSignInClient;

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("898751084700-cc0f2pdg3ucfjkm3egko17q5cn0h7qpa.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(application, gso);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public void handleSignInResult(Task<GoogleSignInAccount> task, Context context) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                Toast.makeText(context, "Signed in as " + account.getEmail(), Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Toast.makeText(context, "Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveUserToBackend(String username, String userType, Context context) {

        // Check if username is empty or null
        if (username == null || username.trim().isEmpty()) {
            Toast.makeText(context, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;  // Exit the method early if username is invalid
        }
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(context);

        if (googleAccount != null) {
            User user = "Member".equals(userType) ?
                    new Member(googleAccount.getId(), googleAccount.getEmail(), username) :
                    new Organisation(googleAccount.getId(), googleAccount.getEmail(), username);

            sendUserDataToBackend(user, userType, context);
        }
    }

    private void sendUserDataToBackend(User user, String userType, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/users")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserApiService apiService = retrofit.create(UserApiService.class);

        Call<Void> call = apiService.saveUser(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    navigateToNextScreen(userType, context);
                } else {
                    Toast.makeText(context, "Error saving user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToNextScreen(String userType, Context context) {
        Intent intent = "Member".equals(userType) ?
                new Intent(context, MemberHomePageActivity.class) :
                new Intent(context, OrganisationHomePageActivity.class);

        context.startActivity(intent);
    }
}
