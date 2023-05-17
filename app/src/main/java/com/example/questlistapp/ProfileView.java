package com.example.questlistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class ProfileView extends AppCompatActivity {

    private TextView nameTextView;
    private TextView ageTextView;
    private TextView phoneTextView;
    private TextView emailTextView;

    private static final int EDIT_PROFILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);



        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.caramel)));

        ImageView mImageView;
        mImageView = findViewById(R.id.imageAvatarView);
        mImageView.setImageResource(R.drawable.baseline_person_24);

        nameTextView = findViewById(R.id.profilename);
        ageTextView = findViewById(R.id.profileage);
        phoneTextView = findViewById(R.id.profilephonenumber);
        emailTextView = findViewById(R.id.profileemail);

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileView.this, EditProfile.class);
                startActivityForResult(intent, EDIT_PROFILE_REQUEST);
            }
        });

// Retrieve the values from the intent extras
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String age = intent.getStringExtra("age");
            String phone = intent.getStringExtra("phone");
            String email = intent.getStringExtra("email");

// Update the profile information
            nameTextView.setText(name);
            ageTextView.setText(age);
            phoneTextView.setText(phone);
            emailTextView.setText(email);
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK) {
            // Retrieve the data from the intent extras
            String name = data.getStringExtra("name");
            String age = data.getStringExtra("age");
            String phone = data.getStringExtra("phone");
            String email = data.getStringExtra("email");

            // Update the profile information
            nameTextView.setText(name);
            ageTextView.setText(age);
            phoneTextView.setText(phone);
            emailTextView.setText(email);
        }
    }
}