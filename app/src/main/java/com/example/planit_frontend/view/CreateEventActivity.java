package com.example.planit_frontend.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to the layout for creating an event
        setContentView(R.layout.activity_createevent);
    }
}