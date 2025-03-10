package com.example.planit_frontend.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.planit_frontend.UserApiService;
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

public class MainActivityViewModel extends AndroidViewModel {
    private final GoogleSignInClient googleSignInClient;
    private final MutableLiveData<GoogleSignInAccount> userAccount = new MutableLiveData<>();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        // configures google sign in with cloud console client id
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("898751084700-cc0f2pdg3ucfjkm3egko17q5cn0h7qpa.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(application, gso);
    }

    // manages google sign in flow, obtains google ID token, sends token to API for user authentication, keeps track of signed in user info using LiveData

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public MutableLiveData<GoogleSignInAccount> getUserAccount() {
        return userAccount;
    }

    public void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                userAccount.setValue(account);
                sendTokenToBackend(account.getIdToken());
            }
        } catch (ApiException e) {
            Log.e("GoogleSignIn", "Sign-in failed", e);
        }
    }

    private void sendTokenToBackend(String idToken) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/users/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserApiService apiService = retrofit.create(UserApiService.class);

        Call<Void> call = apiService.sendGoogleToken(idToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("BackendAuth", "Token successfully sent to backend!");
                } else {
                    Log.e("BackendAuth", "Failed to send token: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("BackendAuth", "Error: " + t.getMessage());
            }
        });
    }
}

