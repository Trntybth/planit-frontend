package com.example.planit_frontend;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SelectUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectuser);  // Your layout XML

        Button userButton = findViewById(R.id.memberbutton);
        Button organisationButton = findViewById(R.id.organisationbutton);

        // Set click listener for user button
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to member sign-up page
                Intent intent = new Intent(SelectUserActivity.this, MemberSignupActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for organisation button
        organisationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to organisation sign-up page
                Intent intent = new Intent(SelectUserActivity.this, OrganisationSignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
