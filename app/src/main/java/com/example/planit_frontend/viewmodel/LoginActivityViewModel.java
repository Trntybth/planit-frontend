
package com.example.planit_frontend.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planit_frontend.model.User;
import com.example.planit_frontend.view.MemberHomePageActivity;
import com.example.planit_frontend.view.OrganisationHomePageActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

public class LoginActivityViewModel extends AndroidViewModel {
    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
    }


}
